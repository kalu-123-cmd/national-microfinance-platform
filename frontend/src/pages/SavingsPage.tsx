import { useState } from 'react'
import { PiggyBank, Target, Lock, Plus, TrendingUp } from 'lucide-react'
import Modal from '../components/ui/Modal'
import Spinner from '../components/ui/Spinner'
import { createAccount, createGoal, createFD, contributeGoal } from '../api/savings'

const mockAccounts = [
  { id:'SA001', number:'SAV123456', name:'Regular Savings', type:'REGULAR', balance:5200, rate:5, status:'ACTIVE' },
  { id:'SA002', number:'SAV654321', name:'School Fees Goal', type:'SAVINGS_GOAL', balance:3750, rate:6, status:'ACTIVE' },
]
const mockFDs = [
  { id:'FD001', number:'FD001', principal:10000, rate:12, months:12, maturity:11200, maturityDate:'2027-01-01', status:'ACTIVE' },
]
const mockGoals = [
  { id:'G001', name:'School Fees 2027', target:15000, current:3750, date:'2027-06-01', status:'ACTIVE', category:'EDUCATION' },
  { id:'G002', name:'House Renovation',  target:50000, current:8000, date:'2028-01-01', status:'ACTIVE', category:'HOME' },
]

const tabs = ['Accounts', 'Fixed Deposits', 'Goals'] as const
type Tab = typeof tabs[number]

export default function SavingsPage() {
  const [tab, setTab] = useState<Tab>('Accounts')
  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [msg, setMsg] = useState('')
  const [form, setForm] = useState({ accountType:'REGULAR', accountName:'', initialDeposit:'', currency:'ETB' })
  const set = (k:string) => (e:React.ChangeEvent<HTMLInputElement|HTMLSelectElement>) => setForm(f=>({...f,[k]:e.target.value}))

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault(); setLoading(true); setMsg('')
    try {
      await createAccount({ ...form, initialDeposit: parseFloat(form.initialDeposit || '0') })
      setMsg('Account created! ✅')
      setTimeout(() => { setOpen(false); setMsg('') }, 2000)
    } catch (err:any) { setMsg(err?.response?.data?.message ?? 'Failed.') }
    finally { setLoading(false) }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="page-title">Savings</h1>
          <p className="text-sm text-gray-500 mt-0.5">Grow your money with competitive interest rates</p>
        </div>
        <button onClick={() => setOpen(true)} className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" /> New Account
        </button>
      </div>

      {/* Summary */}
      <div className="grid grid-cols-3 gap-4">
        <div className="card bg-gradient-to-br from-primary-600 to-primary-700 text-white">
          <p className="text-primary-200 text-xs font-medium">Total Savings</p>
          <p className="text-2xl font-black mt-1">8,950 ETB</p>
          <p className="text-primary-200 text-xs mt-0.5 flex items-center gap-1"><TrendingUp className="w-3 h-3" /> +12% this month</p>
        </div>
        <div className="card">
          <p className="text-xs text-gray-400 font-medium">Fixed Deposits</p>
          <p className="text-2xl font-black text-gray-900 mt-1">10,000 ETB</p>
          <p className="text-xs text-gray-400 mt-0.5">Matures Jan 2027</p>
        </div>
        <div className="card">
          <p className="text-xs text-gray-400 font-medium">Active Goals</p>
          <p className="text-2xl font-black text-gray-900 mt-1">2</p>
          <p className="text-xs text-gray-400 mt-0.5">Avg 37% reached</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 bg-gray-100 p-1 rounded-xl w-fit">
        {tabs.map(t => (
          <button key={t} onClick={() => setTab(t)}
            className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all ${tab === t ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}>
            {t}
          </button>
        ))}
      </div>

      {/* Accounts tab */}
      {tab === 'Accounts' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {mockAccounts.map(acc => (
            <div key={acc.id} className="card hover:shadow-md transition-all cursor-pointer border-2 border-transparent hover:border-primary-200">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center">
                  <PiggyBank className="w-5 h-5 text-primary-600" />
                </div>
                <div>
                  <p className="font-semibold text-gray-800 text-sm">{acc.name}</p>
                  <p className="text-xs text-gray-400 font-mono">{acc.number}</p>
                </div>
              </div>
              <p className="text-2xl font-black text-gray-900">{acc.balance.toLocaleString()} <span className="text-base font-semibold text-gray-400">ETB</span></p>
              <div className="flex items-center justify-between mt-3">
                <span className="badge-success">{acc.type.replace('_',' ')}</span>
                <span className="text-xs text-gray-400">{acc.rate}% p.a.</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Fixed Deposits tab */}
      {tab === 'Fixed Deposits' && (
        <div className="space-y-3">
          {mockFDs.map(fd => (
            <div key={fd.id} className="card">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center">
                    <Lock className="w-5 h-5 text-orange-600" />
                  </div>
                  <div>
                    <p className="font-semibold text-gray-800 text-sm">{fd.number}</p>
                    <p className="text-xs text-gray-400">{fd.months} months · {fd.rate}% p.a.</p>
                  </div>
                </div>
                <span className="badge-success">{fd.status}</span>
              </div>
              <div className="grid grid-cols-3 gap-4 mt-4">
                <div><p className="text-xs text-gray-400">Principal</p><p className="font-bold text-gray-800">{fd.principal.toLocaleString()} ETB</p></div>
                <div><p className="text-xs text-gray-400">At Maturity</p><p className="font-bold text-green-600">{fd.maturity.toLocaleString()} ETB</p></div>
                <div><p className="text-xs text-gray-400">Matures</p><p className="font-bold text-gray-800">{fd.maturityDate}</p></div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Goals tab */}
      {tab === 'Goals' && (
        <div className="space-y-4">
          {mockGoals.map(goal => {
            const pct = Math.round((goal.current / goal.target) * 100)
            return (
              <div key={goal.id} className="card">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center">
                      <Target className="w-5 h-5 text-purple-600" />
                    </div>
                    <div>
                      <p className="font-semibold text-gray-800 text-sm">{goal.name}</p>
                      <p className="text-xs text-gray-400">{goal.category} · Due {goal.date}</p>
                    </div>
                  </div>
                  <span className="text-sm font-bold text-primary-600">{pct}%</span>
                </div>
                <div className="h-2.5 bg-gray-100 rounded-full overflow-hidden mb-2">
                  <div className="h-full bg-gradient-to-r from-primary-500 to-green-400 rounded-full transition-all" style={{ width: `${pct}%` }} />
                </div>
                <div className="flex justify-between text-xs text-gray-400">
                  <span>Saved: {goal.current.toLocaleString()} ETB</span>
                  <span>Target: {goal.target.toLocaleString()} ETB</span>
                </div>
                <button className="mt-3 btn-secondary w-full text-sm py-2">+ Contribute</button>
              </div>
            )
          })}
        </div>
      )}

      <Modal open={open} onClose={() => setOpen(false)} title="Open Savings Account">
        <form onSubmit={handleCreate} className="space-y-4">
          {msg && <div className={`text-sm rounded-xl px-4 py-3 ${msg.includes('✅') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>{msg}</div>}
          <div>
            <label className="label">Account Type</label>
            <select value={form.accountType} onChange={set('accountType')} className="input">
              {['REGULAR','SAVINGS_GOAL','CHILDREN','PENSION','FIXED_DEPOSIT'].map(t => <option key={t}>{t.replace('_',' ')}</option>)}
            </select>
          </div>
          <div>
            <label className="label">Account Name (optional)</label>
            <input type="text" value={form.accountName} onChange={set('accountName')} placeholder="e.g. My Emergency Fund" className="input" />
          </div>
          <div>
            <label className="label">Initial Deposit (ETB)</label>
            <input type="number" value={form.initialDeposit} onChange={set('initialDeposit')} placeholder="100.00" className="input" />
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full flex items-center justify-center gap-2">
            {loading && <Spinner size="sm" />} {loading ? 'Creating…' : 'Open Account'}
          </button>
        </form>
      </Modal>
    </div>
  )
}
