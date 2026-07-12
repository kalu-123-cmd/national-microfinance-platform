import { walletApi, unwrap } from './client'

export interface Wallet     { id: string; walletNumber: string; balance: number; currency: string; status: string; createdAt: string }
export interface Transaction{ id: string; type: string; amount: number; description: string; status: string; reference: string; createdAt: string; balanceAfter: number }

export const getMyWallet        = ()                          => unwrap<Wallet>(walletApi.get('/my'))
export const getTransactions    = (page=0, size=20)           => unwrap<{content:Transaction[];totalElements:number}>(walletApi.get(`/my/transactions?page=${page}&size=${size}`))
export const sendMoney          = (p:{recipientWallet:string;amount:number;description:string;pin:string}) => unwrap<Transaction>(walletApi.post('/transfer', p))
export const depositFunds       = (p:{amount:number;channel:string}) => unwrap<Transaction>(walletApi.post('/deposit', p))
