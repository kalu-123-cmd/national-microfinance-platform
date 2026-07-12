import { useState } from 'react'
import { ArrowUpRight, ArrowDownLeft, Copy, RefreshCw, Send } from 'lucide-react'
import Modal from '../components/ui/Modal'
import Spinner from '../components/ui/Spinner'
import { sendMoney } from '../api/wallet'

const txHistory = [
  { id:'1', ref:'WAL-001', type:'TRANSFER_OUT', amount:-500,  desc:'Transfer to Tigist',  status:'COMPLETED', date:'2026-07-07 09:00', balance:12840 },
  { id:'2', ref:'WAL-002', type:'DEPOSIT',       amount:4500, desc:'Salary credit',        status:'COMPLETED', date:'2026-07-07 08:00', balance:13340 },
  { id:'3', ref:'WAL-003', type:'BILL_PAYMENT',  amount:-250, desc:'EEPCO electricity',   status:'COMPLETED', date:'2026-07-06 14:30', balance:8840 },
  { id:'4', ref:'WAL-004', type:'TRANSFER_IN',   amount:1000, desc:'From Abiy',            status:'COMPLETED', date:'2026-07-05 16:45', balance:9090 },
  { id:'5', ref:'WAL-005', type:'WITHDRAWAL',    amount:-200, desc:'ATM withdrawal',       status:'COMPLETED', date:'2026-07-04 11:00', balance:8090 },
]

export default function WalletPage() {
  const [sendOpen, setSendOpen] = useState(false)
  const [loading,  setLoading]  = useState(false)
  const [form, setForm] = useState({ recipientWallet:'', amount:'', description:'', pin:'' })
  const [msg, setMsg] = useState('')

  const set = (k:string) => (e:React.ChangeEvent<HTMLInputElement>) => setForm(f=>({...f,[k]:e.target.value}))

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true); setMsg('')
    try {
      await sendMoney({ ...form, amount: parseFloat(form.amount) })
      setMsg('Transfer successful! ✅')
      setTimeout(() => { setSendOpen(false); setMsg(''); setForm({recipientWallet:'',amount:'',description:'',pin:''}) }, 2000)
    } catch (err:any) {
      setMsg(err?.response?.data?.message ?? 'Transfer failed.')
    } finally { setLoading(false) }
  }

  return (
    <div className="space-y-6">
      {/* Wallet card */}
      <div className="rounded-3xl bg-gradient-to-br from-primary-700 to-green-600 p-6 text-white shadow-xl shadow-primary-500/20">
        <p className="text-green-200 text-sm font-medium">Available Balance</p>
        <p className="text-4xl font-black mt-1">12,840 <span className="text-2xl font-bold text-green-200">ETB</span></p>
        <p className="text-green-200 text-xs mt-1">Wallet No: <span className="font-mono font-semibold">WAL-00123456</span></p>
        <div className="flex gap-3 mt-5">
          <button onClick={() => setSendOpen(true)} className="flex items-center gap-2 bg-white/20 hover:bg-white/30 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all">
            <Send className="w-4 h-4" /> Send Money
          </button>
          <button className="flex items-center gap-2 bg-white/20 hover:bg-white/30 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all">
            <ArrowDownLeft className="w-4 h-4" /> Receive
          </button>
          <button className="flex items-center gap-2 bg-white/20 hover:bg-white/30 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all">
            <ArrowUpRight className="w-4 h-4" /> Top Up
          </button>
        </div>
      </div>

      {/* Wallet number to share */}
      <div className="card flex items-center justify-between">
        <div>
          <p className="text-xs text-gray-400 font-medium">Your Wallet Address (share to receive)</p>
          <p className="font-mono font-semibold text-gray-800 mt-0.5">WAL-00123456</p>
        </div>
        <button className="p-2.5 bg-gray-100 hover:bg-gray-200 rounded-xl transition-colors">
          <Copy className="w-4 h-4 text-gray-600" />
        </button>
      </div>

      {/* Transaction history */}
      <div className="card">
        <div className="flex items-center justify-between mb-5">
          <h2 className="section-title mb-0">Transaction History</h2>
          <button className="p-2 hover:bg-gray-100 rounded-xl transition-colors"><RefreshCw className="w-4 h-4 text-gray-500" /></button>
        </div>
        <div className="space-y-1">
          {txHistory.map(tx => (
            <div key={tx.id} className="flex items-center gap-4 px-3 py-3.5 rounded-xl hover:bg-gray-50 transition-colors">
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 text-lg ${
                tx.amount > 0 ? 'bg-green-100' : 'bg-gray-100'
              }`}>
                {tx.amount > 0 ? '📥' : '📤'}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-gray-800 truncate">{tx.desc}</p>
                <p className="text-xs text-gray-400">{tx.date} · {tx.ref}</p>
              </div>
              <div className="text-right flex-shrink-0">
                <p className={`text-sm font-bold ${tx.amount > 0 ? 'text-green-600' : 'text-gray-700'}`}>
                  {tx.amount > 0 ? '+' : ''}{tx.amount.toLocaleString()} ETB
                </p>
                <p className="text-xs text-gray-400">Bal: {tx.balance.toLocaleString()}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Send money modal */}
      <Modal open={sendOpen} onClose={() => setSendOpen(false)} title="Send Money">
        <form onSubmit={handleSend} className="space-y-4">
          {msg && <div className={`text-sm rounded-xl px-4 py-3 ${msg.includes('✅') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>{msg}</div>}
          {[
            { k:'recipientWallet', label:'Recipient Wallet Number', ph:'WAL-00XXXXXX', type:'text' },
            { k:'amount',          label:'Amount (ETB)',            ph:'100.00',       type:'number' },
            { k:'description',     label:'Description',            ph:'Payment for…', type:'text' },
            { k:'pin',             label:'Your 4-digit PIN',       ph:'••••',         type:'password', max:4 },
          ].map(({ k, label, ph, type, max }) => (
            <div key={k}>
              <label className="label">{label}</label>
              <input type={type} value={form[k as keyof typeof form]} onChange={set(k)} placeholder={ph} maxLength={max} required className="input" />
            </div>
          ))}
          <button type="submit" disabled={loading} className="btn-primary w-full flex items-center justify-center gap-2">
            {loading && <Spinner size="sm" />} {loading ? 'Sending…' : 'Send Money'}
          </button>
        </form>
      </Modal>
    </div>
  )
}
