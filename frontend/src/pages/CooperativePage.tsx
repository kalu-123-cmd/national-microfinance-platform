import { useState } from 'react'
import { Users, Coins, RotateCcw, CreditCard } from 'lucide-react'

const mockCoops = [
  { id:'C001', name:'Addis Teachers SACCO', members:45, pool:120000, monthly:500, type:'SACCO', status:'ACTIVE' },
  { id:'C002', name:'Merkato Traders ROSCA', members:12, pool:24000, monthly:2000, type:'ROSCA', status:'ACTIVE' },
]
const mockRosca = [
  { cycle:1, beneficiary:'Tigist A.', amount:24000, date:'2026-02-01', status:'DISBURSED' },
  { cycle:2, beneficiary:'Abebe K.',  amount:24000, date:'2026-03-01', status:'DISBURSED' },
  { cycle:3, beneficiary:'You',       amount:24000, date:'2026-04-01', status:'SCHEDULED' },
  { cycle:4, beneficiary:'Meseret L.',amount:24000, date:'2026-05-01', status:'SCHEDULED' },
]
const tabs = ['Overview', 'ROSCA Cycles', 'Group Loans'] as const
type Tab = typeof tabs[number]

export default function CooperativePage() {
  const [tab, setTab] = useState<Tab>('Overview')
  return (
    <div className="space-y-6">
      <div>
        <h1 className="page-title">Cooperative</h1>
        <p className="text-sm text-gray-500 mt-0.5">Group savings, ROSCA cycles, and cooperative loans</p>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {mockCoops.map(c => (
          <div key={c.id} className="card hover:shadow-md transition-all cursor-pointer border-2 border-transparent hover:border-primary-200">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center">
                <Users className="w-5 h-5 text-primary-600" />
              </div>
              <div>
                <p className="font-semibold text-gray-800 text-sm">{c.name}</p>
                <p className="text-xs text-gray-400">{c.members} members · {c.type}</p>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="bg-gray-50 rounded-xl p-3">
                <p className="text-xs text-gray-400">Pool Balance</p>
                <p className="font-bold text-gray-900 text-sm mt-0.5">{c.pool.toLocaleString()} ETB</p>
              </div>
              <div className="bg-gray-50 rounded-xl p-3">
                <p className="text-xs text-gray-400">Monthly Contrib.</p>
                <p className="font-bold text-gray-900 text-sm mt-0.5">{c.monthly.toLocaleString()} ETB</p>
              </div>
            </div>
          </div>
        ))}
      </div>
      <div className="flex gap-1 bg-gray-100 p-1 rounded-xl w-fit">
        {tabs.map(t => (
          <button key={t} onClick={() => setTab(t)}
            className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all ${tab === t ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}>
            {t}
          </button>
        ))}
      </div>
      {tab === 'Overview' && (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="card"><p className="text-xs text-gray-400">Total Contributed</p><p className="text-2xl font-black text-gray-900 mt-1">12,000 ETB</p></div>
          <div className="card"><p className="text-xs text-gray-400">Cooperatives Joined</p><p className="text-2xl font-black text-gray-900 mt-1">2</p></div>
          <div className="card"><p className="text-xs text-gray-400">ROSCA Turn</p><p className="text-2xl font-black text-gray-900 mt-1">Cycle 3</p><p className="text-xs text-green-500 mt-0.5">April 2026</p></div>
        </div>
      )}
      {tab === 'ROSCA Cycles' && (
        <div className="card">
          <h2 className="section-title">Merkato Traders ROSCA — Cycle Schedule</h2>
          <div className="space-y-2">
            {mockRosca.map(r => (
              <div key={r.cycle} className={`flex items-center gap-4 px-4 py-3.5 rounded-xl border ${r.beneficiary === 'You' ? 'border-primary-300 bg-primary-50' : 'border-gray-100'}`}>
                <div className="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center text-sm font-bold text-gray-600 flex-shrink-0">
                  {r.cycle}
                </div>
                <div className="flex-1 min-w-0">
                  <p className={`text-sm font-semibold ${r.beneficiary === 'You' ? 'text-primary-700' : 'text-gray-800'}`}>
                    {r.beneficiary} {r.beneficiary === 'You' && '← Your turn!'}
                  </p>
                  <p className="text-xs text-gray-400">{r.date}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-bold text-gray-800">{r.amount.toLocaleString()} ETB</p>
                  <span className={r.status === 'DISBURSED' ? 'badge-success' : 'badge-info'}>{r.status}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
      {tab === 'Group Loans' && (
        <div className="card">
          <div className="text-center py-12 text-gray-400">
            <CreditCard className="w-12 h-12 mx-auto mb-3 opacity-30" />
            <p className="font-medium">No active group loans</p>
            <p className="text-sm mt-1">Apply through your cooperative to access group loans</p>
            <button className="btn-primary mt-4 mx-auto">Apply for Group Loan</button>
          </div>
        </div>
      )}
    </div>
  )
}
