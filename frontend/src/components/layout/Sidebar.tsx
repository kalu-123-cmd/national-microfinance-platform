import { NavLink, useNavigate } from 'react-router-dom'
import { clsx } from 'clsx'
import {
  LayoutDashboard, Wallet, CreditCard, PiggyBank, Users,
  ArrowRightLeft, BarChart3, Settings, LogOut, ShieldCheck,
} from 'lucide-react'
import { useAuthStore } from '../../store/authStore'

const nav = [
  { to: '/dashboard',   icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/wallet',      icon: Wallet,          label: 'Wallet' },
  { to: '/payments',    icon: ArrowRightLeft,  label: 'Payments' },
  { to: '/loans',       icon: CreditCard,      label: 'Loans' },
  { to: '/savings',     icon: PiggyBank,       label: 'Savings' },
  { to: '/cooperative', icon: Users,           label: 'Cooperative' },
  { to: '/analytics',   icon: BarChart3,       label: 'Analytics' },
]
const bottom = [
  { to: '/settings',    icon: Settings,        label: 'Settings' },
  { to: '/kyc',         icon: ShieldCheck,     label: 'KYC / Identity' },
]

export default function Sidebar({ mobile, onClose }: { mobile?: boolean; onClose?: () => void }) {
  const logout  = useAuthStore(s => s.logout)
  const navigate = useNavigate()

  const handleLogout = () => { logout(); navigate('/login') }

  return (
    <aside className={clsx(
      'flex flex-col h-full bg-gray-900 text-white',
      mobile ? 'w-full' : 'w-[var(--sidebar-width)]',
    )}>
      {/* Logo */}
      <div className="flex items-center gap-3 px-5 py-5 border-b border-gray-800">
        <div className="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center flex-shrink-0">
          <span className="text-white font-black text-sm">ET</span>
        </div>
        <div className="min-w-0">
          <p className="font-bold text-sm leading-tight truncate">National Microfinance</p>
          <p className="text-xs text-gray-400 truncate">🇪🇹 Ethiopia</p>
        </div>
      </div>

      {/* Main nav */}
      <nav className="flex-1 px-3 py-4 overflow-y-auto space-y-0.5 scrollbar-hide">
        <p className="text-[10px] uppercase font-semibold text-gray-500 px-3 pb-2 tracking-wider">Main</p>
        {nav.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            onClick={onClose}
            className={({ isActive }) => clsx(
              'flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150',
              isActive
                ? 'bg-primary-600 text-white shadow-lg shadow-primary-900/30'
                : 'text-gray-400 hover:bg-gray-800 hover:text-white',
            )}
          >
            <Icon className="w-4.5 h-4.5 flex-shrink-0" size={18} />
            {label}
          </NavLink>
        ))}

        <p className="text-[10px] uppercase font-semibold text-gray-500 px-3 pb-2 pt-5 tracking-wider">Account</p>
        {bottom.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            onClick={onClose}
            className={({ isActive }) => clsx(
              'flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150',
              isActive
                ? 'bg-primary-600 text-white'
                : 'text-gray-400 hover:bg-gray-800 hover:text-white',
            )}
          >
            <Icon className="w-4.5 h-4.5 flex-shrink-0" size={18} />
            {label}
          </NavLink>
        ))}
      </nav>

      {/* Logout */}
      <div className="px-3 pb-4 border-t border-gray-800 pt-3">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 w-full px-3 py-2.5 rounded-xl text-sm font-medium text-gray-400 hover:bg-red-900/40 hover:text-red-400 transition-all duration-150"
        >
          <LogOut size={18} />
          Sign Out
        </button>
      </div>
    </aside>
  )
}
