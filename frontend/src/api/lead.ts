import request from './request'

export interface Lead {
  id?: number
  name: string
  company: string
  phone: string
  email: string
  source: string
  status: string
  remark: string
  createdAt?: string
}

export const getLeads = (params?: any) => request.get('/leads', { params })
export const createLead = (data: Lead) => request.post('/leads', data)
export const convertLead = (id: number) => request.put(`/leads/${id}/convert`)
