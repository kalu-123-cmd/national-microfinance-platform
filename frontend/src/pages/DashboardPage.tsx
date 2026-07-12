import { Wallet, TrendingUp, CreditCard, PiggyBank, ArrowUpRight, ArrowDownLeft, Send, Plus } from 'lucide-react'
import { Link } from 'react-router-dom'
import StatCard from '../components/ui/StatCard'

const recentTx = [
  { id:1, label:'Salary Credit',      amount:'+4,500 ETB', type:'credit', time:'Today, 09:00',    icon:'💰' },
  { id:2, label:'EEPCO Bill',         amount:'-250 ETB',   type:'debit',  time:'Yesterday, 14:30', icon:'⚡' },
  { id:3, label:'Savings Deposit',    amount:'-500 ETB',   type:'debit',  time:'Yesterday, 10:00', icon:'🏦' },
  { id:4, label:'Transfer from Abiy', amount:'+1,000 ETB', type:'credit', time:'Mon, 16:45',        icon:'📲' },
  { id:5, label:'Tele-birr Top-up',   amount:'-100 ETB',   type:'debit',  time:'Mon, 12:00',        icon:'📱' },
]

const quickActions = [
  { to:'/wallet',   icon: Send,           label:'Send',    color:'bg-primary-600' },
  { to:'/payments', icon: ArrowUpRight,   label:'Pay Bill',color:'bg-blue-600' },
  { to:'/savings',  icon: PiggyBank,      label:'Save',    color:'bg-purple-600' },
  { to:'/loans',    icon: CreditCard,     label:'Borrow',  color:'bg-orange-600' },
]

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      {/* Hero wallet card */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-primary-700 via-primary-600 to-green-500 p-6 md:p-8 text-white shadow-xl shadow-primary-500/30">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full -translate-y-1/3 translate-x-1/3" />
        <div className="absolute bottom-0 left-0 w-40 h-40 bg-white/5 rounded-full translate-y-1/2 -translate-x-1/4" />
        <div className="relative z-10">
          <p className="text-green-200 text-sm font-medium mb-1">Total Wallet Balance</p>
          <p className="text-5xl font-black tracking-tight">12,840 <span className="text-3xl font-bold text-green-200">ETB</span></p>
          <p className="text-green-200 text-xs mt-1">≈ $92.30 USD</p>
          <div className="flex gap-4 mt-6">
            <Link to="/wallet" className="flex items-center gap-2 bg-white/20 hover:bg-white/30 backdrop-blur-sm px-4 py-2.5 rounded-xl text-sm font-semibold transition-all">
              <ArrowUpRight className="w-4 h-4" /> Send
            </Link>
            <Link to="/wallet" className="flex items-center gap-2 bg-white/20 hover:bg-white/30 backdrop-blur-sm px-4 py-2.5 rounded-xl text-sm font-semibold transition-all">
              <ArrowDownLeft className="w-4 h-4" /> Receive
            </Link>
            <Link to="/wallet" className="flex items-center gap-2 bg-white/20 hover:bg-white/30 backdrop-blur-sm px-4 py-2.5 rounded-xl text-sm font-semibold transition-all">
              <Plus className="w-4 h-4" /> Top Up
            </Link>
          </div>
        </div>
      </div>

      {/* Quick actions */}
      <div className="grid grid-cols-4 gap-3">
        {quickActions.map(({ to, icon: Icon, label, color }) => (
          <Link key={to} to={to} className="card flex flex-col items-center gap-3 py-5 hover:shadow-md transition-all group cursor-pointer">
            <div className={`${color} w-12 h-12 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform shadow-lg`}>
              <Icon className="w-5 h-5 text-white" />
            </div>
            <span className="text-xs font-semibold text-gray-600">{label}</span>
          </Link>
        ))}
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Savings Balance" value="5,200 ETB" subtitle="vs last month" trend={12} icon={<PiggyBank className="w-5 h-5" />} color="green" />
        <StatCard title="Active Loans"    value="1"          subtitle="Outstanding: 3,500 ETB" icon={<CreditCard className="w-5 h-5" />} color="orange" />
        <StatCard title="Credit Score"    value="720"        subtitle="Good standing" trend={5}  icon={<TrendingUp className="w-5 h-5" />} color="blue" />
        <StatCard title="Savings Goals"   value="2"          subtitle="75% achieved avg" icon={<Wallet className="w-5 h-5" />} color="purple" />
      </div>

      {/* Recent transactions */}
      <div className="card">
        <div className="flex items-center justify-between mb-5">
          <h2 className="section-title mb-0">Recent Transactions</h2>
          <Link to="/wallet" className="text-sm text-primary-600 font-semibold hover:text-primary-700">View all →</Link>
        </div>
        <div className="space-y-1">
          {recentTx.map(tx => (
            <div key={tx.id} className="flex items-center gap-4 px-3 py-3 rounded-xl hover:bg-gray-50 transition-colors">
              <div className="w-10 h-10 bg-gray-100 rounded-xl flex items-center justify-center text-lg flex-shrink-0">
                {tx.icon}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-gray-800 truncate">{tx.label}</p>
                <p className="text-xs text-gray-400">{tx.time}</p>
              </div>
              <span className={`text-sm font-bold flex-shrink-0 ${tx.type === 'credit' ? 'text-green-600' : 'text-gray-700'}`}>
                {tx.amount}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
