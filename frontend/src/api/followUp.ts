import request from './request'

export interface FollowUp {
  id?: number
  customerId: number
  content: string
  type: string
  nextTime?: string
  createdAt?: string
}

export const getFollowUps = (customerId: number) => request.get('/follow-ups', { params: { customerId } })
export const createFollowUp = (data: FollowUp) => request.post('/follow-ups', data)
