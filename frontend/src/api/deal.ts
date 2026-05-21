import request from './request'

export interface Deal {
  id?: number
  customerId: number
  customerName?: string
  opportunityId?: number
  title: string
  amount: number
  dealDate: string
  remark: string
  createdAt?: string
}

export const getDeals = (params?: any) => request.get('/deals', { params })
export const createDeal = (data: Deal) => request.post('/deals', data)
