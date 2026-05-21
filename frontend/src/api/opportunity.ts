import request from './request'

export interface Opportunity {
  id?: number
  customerId: number
  customerName?: string
  title: string
  amount: number
  stage: string
  remark: string
  createdAt?: string
}

export const getOpportunities = (params?: any) => request.get('/opportunities', { params })
export const createOpportunity = (data: Opportunity) => request.post('/opportunities', data)
export const updateOpportunityStage = (id: number, stage: string) => request.put(`/opportunities/${id}/stage`, { stage })
