import { loanApi, unwrap } from './client'

export interface Loan         { id: string; loanNumber: string; requestedAmount: number; approvedAmount: number; interestRate: number; tenureMonths: number; status: string; outstandingBalance: number; totalRepayable: number; firstRepaymentDate: string; createdAt: string }
export interface Schedule     { id: string; installmentNumber: number; dueDate: string; totalAmount: number; principalAmount: number; interestAmount: number; paidAmount: number; status: string }
export interface ApplyPayload { userId: string; requestedAmount: number; tenureMonths: number; purpose: string; walletId: string; loanType: string }
export interface RepayPayload { loanId: string; amount: number; paymentReference: string; channel: string }

export const getMyLoans      = (userId: string)  => unwrap<Loan[]>(loanApi.get(`/user/${userId}`))
export const getLoan         = (id: string)      => unwrap<Loan>(loanApi.get(`/${id}`))
export const applyLoan       = (p: ApplyPayload) => unwrap<Loan>(loanApi.post('', p))
export const getSchedule     = (id: string)      => unwrap<Schedule[]>(loanApi.get(`/${id}/schedule`))
export const makeRepayment   = (p: RepayPayload) => unwrap<any>(loanApi.post('/repay', p))
