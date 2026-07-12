import { savingsApi, unwrap } from './client'

export interface SavingsAccount { id: string; accountNumber: string; accountName: string; accountType: string; balance: number; interestRate: number; status: string }
export interface FixedDeposit   { id: string; depositNumber: string; principalAmount: number; interestRate: number; tenureMonths: number; maturityAmount: number; maturityDate: string; status: string; autoRenew: boolean }
export interface SavingsGoal    { id: string; goalName: string; targetAmount: number; currentAmount: number; targetDate: string; status: string; category: string }

export const getAccounts      = ()                => unwrap<SavingsAccount[]>(savingsApi.get('/accounts'))
export const createAccount    = (p:any)           => unwrap<SavingsAccount>(savingsApi.post('/accounts', p))
export const deposit          = (p:any)           => unwrap<any>(savingsApi.post('/accounts/deposit', p))
export const withdraw         = (p:any)           => unwrap<any>(savingsApi.post('/accounts/withdraw', p))
export const getFixedDeposits = ()                => unwrap<FixedDeposit[]>(savingsApi.get('/fixed-deposits'))
export const createFD         = (p:any)           => unwrap<FixedDeposit>(savingsApi.post('/fixed-deposits', p))
export const getGoals         = ()                => unwrap<SavingsGoal[]>(savingsApi.get('/goals'))
export const createGoal       = (p:any)           => unwrap<SavingsGoal>(savingsApi.post('/goals', p))
export const contributeGoal   = (id:string,amt:number) => unwrap<SavingsGoal>(savingsApi.post(`/goals/${id}/contribute?amount=${amt}`))
