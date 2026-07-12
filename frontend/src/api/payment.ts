import { paymentApi, unwrap } from './client'

export interface Payment { id: string; reference: string; type: string; amount: number; fee: number; status: string; description: string; createdAt: string; merchantName?: string; billerName?: string }

export const initiatePayment  = (p:any) => unwrap<Payment>(paymentApi.post('', p))
export const payBill          = (p:any) => unwrap<Payment>(paymentApi.post('/bill', p))
export const payMerchant      = (p:any) => unwrap<Payment>(paymentApi.post('/merchant', p))
export const getHistory       = (page=0) => unwrap<{content:Payment[];totalElements:number}>(paymentApi.get(`/history?page=${page}&size=20`))
export const getPayment       = (id:string) => unwrap<Payment>(paymentApi.get(`/${id}`))
