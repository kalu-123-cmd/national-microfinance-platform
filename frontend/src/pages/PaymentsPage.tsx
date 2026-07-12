import { useState } from 'react'
import { Zap, Wifi, GraduationCap, ShoppingBag, Building2, Phone } from 'lucide-react'
import Modal from '../components/ui/Modal'
import Spinner from '../components/ui/Spinner'
import { payBill } from '../api/payment'

const billers = [
  { id:'EEPCO',  name:'EEPCO Electricity', icon: <Zap className="w-5 h-5 text-yellow-600" />,  bg:'bg-yellow-100' },
  { id:'ETHIO',  name:'Ethio Telecom',     icon: <Phone className="w-5 h-5 text-blue-600" />,   bg:'bg-blue-100'   },
  { id:'AWASH',  name:'Awash Internet',    icon: <Wifi className="w-5 h-5 text-purple-600" />,  bg:'bg-purple-100' },
  { id:'SCHOOL', name:'School Fees',       icon: <GraduationCap className="w-5 h-5 text-green-600" />, bg:'bg-green-100' },
  { id:'CBE',    name:'CBE Birr',          icon: <Building2 className="w-5 h-5 text-red-600" />,bg:'bg-red-100'    },
  { id:'SHOP',   name:'Merchant Payment',  icon: <ShoppingBag className="w-5 h-5 text-orange-600" />,bg:'bg-orange-100'},
]

const recentPayments = [
  { id:1, biller:'EEPCO Electricity', amount:250,  date:'2026-07-06', ref:'BP-001', status:'COMPLETED' },
  { id:2, biller:'Ethio Telecom',     amount:100,  date:'2026-07-05', ref:'BP-002', status:'COMPLETED' },
  { id:3, biller:'School Fees',       amount:2500, date:'2026-07-01', ref:'BP-003', status:'COMPLETED' },
]

export default function PaymentsPage() {
  const [selected, setSelected] = useState<typeof billers[0]|null>(null)
  const [loading, setLoading] = useState(false)
  const [msg, setMsg] = useState('')
  const [form, setForm] = useState({ amount:'', billAccountNumber:'', walletId:'WAL-00123456', description:'' })
  const set = (k:string) => (e:React.ChangeEvent<HTMLInputElement>) => setForm(f=>({...f,[k]:e.target.value}))

  const handlePay = async (e: React.FormEvent) => {
    e.preventDefault(); setLoading(true); setMsg('')
    try {
      await payBill({ ...form, amount: parseFloat(form.amount), billerId: selected!.id, billerName: selected!.name, billerCode: selected!.id, channel: 'MOBILE_APP' })
      setMsg(`Payment to ${selected!.name} successful! ✅`)
      setTimeout(() => { setSelected(null); setMsg(''); setForm({ amount:'', billAccountNumber:'', walletId:'WAL-00123456', description:'' }) }, 2500)
    } catch (err:any) { setMsg(err?.response?.data?.message ?? 'Payment failed.') }
    finally { setLoading(false) }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="page-title">Payments</h1>
        <p className="text-sm text-gray-500 mt-0.5">Pay bills, utilities, and merchants instantly</p>
      </div>

      {/* Biller grid */}
      <div className="card">
        <h2 className="section-title">Pay a Bill</h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          {billers.map(b => (
            <button
              key={b.id}
              onClick={() => setSelected(b)}
              className="flex items-center gap-3 p-4 border border-gray-100 rounded-2xl hover:border-primary-300 hover:bg-primary-50 transition-all text-left"
            >
              <div className={`w-10 h-10 ${b.bg} rounded-xl flex items-center justify-center flex-shrink-0`}>
                {b.icon}
              </div>
              <span className="text-sm font-semibold text-gray-700">{b.name}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Recent payments */}
      <div className="card">
        <h2 className="section-title">Recent Payments</h2>
        <div className="space-y-1">
          {recentPayments.map(p => (
            <div key={p.id} className="flex items-center gap-4 px-3 py-3 rounded-xl hover:bg-gray-50 transition-colors">
              <div className="w-10 h-10 bg-gray-100 rounded-xl flex items-center justify-center flex-shrink-0 text-base">💳</div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-gray-800 truncate">{p.biller}</p>
                <p className="text-xs text-gray-400">{p.date} · {p.ref}</p>
              </div>
              <div className="text-right flex-shrink-0">
                <p className="text-sm font-bold text-gray-700">-{p.amount} ETB</p>
                <span className="badge-success text-xs">{p.status}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Payment modal */}
      <Modal open={!!selected} onClose={() => setSelected(null)} title={`Pay — ${selected?.name}`}>
        <form onSubmit={handlePay} className="space-y-4">
          {msg && <div className={`text-sm rounded-xl px-4 py-3 ${msg.includes('✅') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>{msg}</div>}
          <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-xl">
            <div className={`w-10 h-10 ${selected?.bg} rounded-xl flex items-center justify-center`}>{selected?.icon}</div>
            <div><p className="font-semibold text-gray-800 text-sm">{selected?.name}</p><p className="text-xs text-gray-400">Bill Payment</p></div>
          </div>
          <div>
            <label className="label">Account / Meter Number</label>
            <input type="text" value={form.billAccountNumber} onChange={set('billAccountNumber')} placeholder="e.g. 0123456789" required className="input" />
          </div>
          <div>
            <label className="label">Amount (ETB)</label>
            <input type="number" value={form.amount} onChange={set('amount')} placeholder="0.00" required className="input" />
          </div>
          <div>
            <label className="label">Description (optional)</label>
            <input type="text" value={form.description} onChange={set('description')} placeholder="e.g. July bill" className="input" />
          </div>
          <div className="flex items-center justify-between text-xs text-gray-400 bg-gray-50 rounded-xl px-3 py-2">
            <span>Service fee</span><span className="font-semibold text-gray-600">{form.amount ? Math.round(parseFloat(form.amount||'0')*0.005) : 0} ETB (0.5%)</span>
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full flex items-center justify-center gap-2">
            {loading && <Spinner size="sm" />} {loading ? 'Processing…' : 'Pay Now'}
          </button>
        </form>
      </Modal>
    </div>
  )
}
