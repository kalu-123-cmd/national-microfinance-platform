import { useState } from 'react'
import { CreditCard, Clock, CheckCircle, AlertCircle, Plus } from 'lucide-react'
import Modal from '../components/ui/Modal'
import Spinner from '../components/ui/Spinner'
import { applyLoan } from '../api/loan'

const mockLoans = [
  { id:'L001', loanNumber:'LOAN-001', amount:10000, outstanding:3500, rate:18, months:12, status:'ACTIVE', nextDue:'2026-07-25', nextAmount:950 },
  { id:'L002', loanNumber:'LOAN-002', amount:5000,  outstanding:0,    rate:18, months:6,  status:'CLOSED', nextDue:'-',          nextAmount:0 },
]

const statusStyle: Record<string,string> = {
  ACTIVE:    'badge-success',
  CLOSED:    'badge-gray',
  SUBMITTED: 'badge-info',
  APPROVED:  'badge-warning',
  REJECTED:  'badge-danger',
}

export default function LoansPage() {
  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [msg, setMsg] = useState('')
  const [form, setForm] = useState({ userId:'user-001', requestedAmount:'', tenureMonths:'12', purpose:'', walletId:'WAL-00123456', loanType:'PERSONAL' })
  const set = (k:string) => (e:React.ChangeEvent<HTMLInputElement|HTMLSelectElement|HTMLTextAreaElement>) =>
    setForm(f => ({ ...f, [k]: e.target.value }))

  const handleApply = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true); setMsg('')
    try {
      await applyLoan({ ...form, requestedAmount: parseFloat(form.requestedAmount), tenureMonths: parseInt(form.tenureMonths) })
      setMsg('Loan application submitted! ✅')
      setTimeout(() => { setOpen(false); setMsg('') }, 2000)
    } catch (err:any) {
      setMsg(err?.response?.data?.message ?? 'Application failed.')
    } finally { setLoading(false) }
  }

  const totalOutstanding = mockLoans.reduce((s,l) => s + l.outstanding, 0)

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="page-title">Loans</h1>
          <p className="text-sm text-gray-500 mt-0.5">Manage your loan applications and repayments</p>
        </div>
        <button onClick={() => setOpen(true)} className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" /> Apply for Loan
        </button>
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="card">
          <p className="text-xs text-gray-400 font-medium">Total Outstanding</p>
          <p className="text-2xl font-black text-gray-900 mt-1">{totalOutstanding.toLocaleString()} ETB</p>
        </div>
        <div className="card">
          <p className="text-xs text-gray-400 font-medium">Active Loans</p>
          <p className="text-2xl font-black text-gray-900 mt-1">{mockLoans.filter(l => l.status==='ACTIVE').length}</p>
        </div>
        <div className="card">
          <p className="text-xs text-gray-400 font-medium">Next Payment</p>
          <p className="text-2xl font-black text-gray-900 mt-1">950 ETB</p>
          <p className="text-xs text-gray-400 mt-0.5">Due July 25</p>
        </div>
      </div>

      {/* Loans list */}
      <div className="card">
        <h2 className="section-title">My Loans</h2>
        <div className="space-y-3">
          {mockLoans.map(loan => (
            <div key={loan.id} className="border border-gray-100 rounded-2xl p-4 hover:border-primary-200 hover:bg-primary-50/30 transition-all">
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center">
                    <CreditCard className="w-5 h-5 text-orange-600" />
                  </div>
                  <div>
                    <p className="text-sm font-bold text-gray-800">{loan.loanNumber}</p>
                    <p className="text-xs text-gray-400">{loan.months}m · {loan.rate}% p.a.</p>
                  </div>
                </div>
                <span className={statusStyle[loan.status]}>{loan.status}</span>
              </div>
              <div className="grid grid-cols-3 gap-4 mt-4">
                <div>
                  <p className="text-xs text-gray-400">Loan Amount</p>
                  <p className="text-sm font-semibold text-gray-800">{loan.amount.toLocaleString()} ETB</p>
                </div>
                <div>
                  <p className="text-xs text-gray-400">Outstanding</p>
                  <p className={`text-sm font-semibold ${loan.outstanding > 0 ? 'text-orange-600' : 'text-green-600'}`}>
                    {loan.outstanding.toLocaleString()} ETB
                  </p>
                </div>
                <div>
                  <p className="text-xs text-gray-400">Next Due</p>
                  <p className="text-sm font-semibold text-gray-800">{loan.nextDue}</p>
                </div>
              </div>
              {loan.outstanding > 0 && (
                <div className="mt-3">
                  <div className="flex justify-between text-xs text-gray-400 mb-1">
                    <span>Repayment progress</span>
                    <span>{Math.round((1 - loan.outstanding / loan.amount) * 100)}%</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-primary-500 rounded-full transition-all"
                      style={{ width: `${(1 - loan.outstanding / loan.amount) * 100}%` }}
                    />
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Apply modal */}
      <Modal open={open} onClose={() => setOpen(false)} title="Apply for a Loan">
        <form onSubmit={handleApply} className="space-y-4">
          {msg && <div className={`text-sm rounded-xl px-4 py-3 ${msg.includes('✅') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>{msg}</div>}
          <div>
            <label className="label">Loan Amount (ETB)</label>
            <input type="number" value={form.requestedAmount} onChange={set('requestedAmount')} placeholder="e.g. 10000" required className="input" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Tenure (months)</label>
              <select value={form.tenureMonths} onChange={set('tenureMonths')} className="input">
                {[3,6,12,18,24,36].map(m => <option key={m} value={m}>{m} months</option>)}
              </select>
            </div>
            <div>
              <label className="label">Loan Type</label>
              <select value={form.loanType} onChange={set('loanType')} className="input">
                {['PERSONAL','BUSINESS','AGRICULTURE','EDUCATION','EMERGENCY'].map(t => <option key={t}>{t}</option>)}
              </select>
            </div>
          </div>
          <div>
            <label className="label">Purpose</label>
            <input type="text" value={form.purpose} onChange={set('purpose')} placeholder="Describe your loan purpose" required className="input" />
          </div>
          <div className="bg-blue-50 rounded-xl p-3 text-xs text-blue-700">
            <strong>Estimated monthly payment:</strong>{' '}
            {form.requestedAmount
              ? Math.round((parseFloat(form.requestedAmount) * 0.18 / 12 + parseFloat(form.requestedAmount) / parseInt(form.tenureMonths)))
              : 0
            } ETB
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full flex items-center justify-center gap-2">
            {loading && <Spinner size="sm" />} {loading ? 'Submitting…' : 'Submit Application'}
          </button>
        </form>
      </Modal>
    </div>
  )
}
