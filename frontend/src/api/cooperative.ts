import { coopApi, unwrap } from './client'

export interface Cooperative   { id: string; name: string; registrationNumber: string; type: string; memberCount?: number; totalPoolBalance: number; monthlyContribution: number; status: string }
export interface CoopMember    { id: string; userId: string; memberNumber: string; role: string; totalContributed: number; status: string; joinDate: string }
export interface RoscaCycle    { id: string; cycleNumber: number; beneficiaryUserId: string; potAmount: number; status: string; scheduledDate: string }
export interface GroupLoan     { id: string; loanNumber: string; amountRequested: number; amountApproved: number; status: string; outstandingBalance: number; appliedAt: string }

export const getAll        = ()               => unwrap<Cooperative[]>(coopApi.get(''))
export const getOne        = (id:string)      => unwrap<Cooperative>(coopApi.get(`/${id}`))
export const create        = (p:any)          => unwrap<Cooperative>(coopApi.post('', p))
export const getMembers    = (id:string)      => unwrap<CoopMember[]>(coopApi.get(`/${id}/members`))
export const joinCoop      = (id:string,p:any)=> unwrap<CoopMember>(coopApi.post(`/${id}/members`, p))
export const getRosca      = (id:string)      => unwrap<RoscaCycle[]>(coopApi.get(`/${id}/rosca`))
export const getLoans      = (id:string)      => unwrap<GroupLoan[]>(coopApi.get(`/${id}/loans`))
export const applyGroupLoan= (id:string,p:any)=> unwrap<GroupLoan>(coopApi.post(`/${id}/loans`, p))
