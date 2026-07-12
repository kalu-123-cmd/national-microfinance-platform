import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import AppShell from './components/layout/AppShell'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import WalletPage from './pages/WalletPage'
import PaymentsPage from './pages/PaymentsPage'
import LoansPage from './pages/LoansPage'
import SavingsPage from './pages/SavingsPage'
import CooperativePage from './pages/CooperativePage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route path="/login"    element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected (AppShell checks auth) */}
        <Route element={<AppShell />}>
          <Route path="/dashboard"   element={<DashboardPage />} />
          <Route path="/wallet"      element={<WalletPage />} />
          <Route path="/payments"    element={<PaymentsPage />} />
          <Route path="/loans"       element={<LoansPage />} />
          <Route path="/savings"     element={<SavingsPage />} />
          <Route path="/cooperative" element={<CooperativePage />} />
          <Route path="/analytics"   element={<DashboardPage />} />
          <Route path="/settings"    element={<DashboardPage />} />
          <Route path="/kyc"         element={<DashboardPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
