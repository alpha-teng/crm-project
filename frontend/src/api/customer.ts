import request from './request'

export interface Customer {
  id?: number
  name: string
  company: string
  phone: string
  email: string
  address: string
  longitude?: number
  latitude?: number
  source: string
  level: string
  remark: string
  createdAt?: string
}

export const getCustomers = (params?: any) => request.get('/customers', { params })
export const getCustomer = (id: number) => request.get(`/customers/${id}`)
export const createCustomer = (data: Customer) => request.post('/customers', data)
export const updateCustomer = (id: number, data: Customer) => request.put(`/customers/${id}`, data)
export const deleteCustomer = (id: number) => request.delete(`/customers/${id}`)

export const getDashboardStats = () => request.get('/dashboard/stats')
