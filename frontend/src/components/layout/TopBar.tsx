import { useState } from 'react'
import { Bell, Menu, Search, X } from 'lucide-react'
import { useLocation } from 'react-router-dom'
import Sidebar from './Sidebar'

const routeLabels: Record<string, string> = {
  '/dashboard':   'Dashboard',
  '/wallet':      'Wallet',
  '/payments':    'Payments',
  '/loans':       'Loans',
  '/savings':     'Savings',
  '/cooperative': 'Cooperative',
  '/analytics':   'Analytics',
  '/settings':    'Settings',
  '/kyc':         'KYC / Identity',
}

export default function TopBar() {
  const [mobileOpen, setMobileOpen] = useState(false)
  const { pathname } = useLocation()
  const title = routeLabels[pathname] ?? 'Dashboard'

  return (
    <>
      <header className="sticky top-0 z-30 bg-white/80 backdrop-blur-md border-b border-gray-100 h-[var(--topbar-height)] flex items-center px-4 md:px-6 gap-4">
        {/* Mobile burger */}
        <button
          className="md:hidden p-2 -ml-1 rounded-xl hover:bg-gray-100"
          onClick={() => setMobileOpen(true)}
        >
          <Menu className="w-5 h-5 text-gray-600" />
        </button>

        <h1 className="font-bold text-gray-900 text-lg flex-1">{title}</h1>

        {/* Search bar */}
        <div className="hidden sm:flex items-center gap-2 bg-gray-100 rounded-xl px-3 py-2 w-56">
          <Search className="w-4 h-4 text-gray-400 flex-shrink-0" />
          <input
            placeholder="Search…"
            className="bg-transparent text-sm text-gray-600 placeholder:text-gray-400 outline-none w-full"
          />
        </div>

        {/* Notifications */}
        <button className="relative p-2 rounded-xl hover:bg-gray-100 transition-colors">
          <Bell className="w-5 h-5 text-gray-600" />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full" />
        </button>

        {/* Avatar */}
        <div className="w-8 h-8 rounded-full bg-primary-600 flex items-center justify-center text-white text-sm font-bold flex-shrink-0">
          U
        </div>
      </header>

      {/* Mobile sidebar drawer */}
      {mobileOpen && (
        <div className="fixed inset-0 z-50 md:hidden">
          <div className="absolute inset-0 bg-black/50" onClick={() => setMobileOpen(false)} />
          <div className="absolute inset-y-0 left-0 w-72">
            <Sidebar mobile onClose={() => setMobileOpen(false)} />
          </div>
          <button className="absolute top-4 right-4 p-2 bg-white rounded-xl shadow" onClick={() => setMobileOpen(false)}>
            <X className="w-5 h-5" />
          </button>
        </div>
      )}
    </>
  )
}
