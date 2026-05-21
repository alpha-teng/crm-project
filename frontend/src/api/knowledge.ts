import request from './request'

export const addToKnowledge = (data: { customerId: number; content: string }) =>
  request.post('/knowledge/add', data)

export const searchKnowledge = (data: { query: string; topK?: number }) =>
  request.post('/knowledge/search', data)

export const aiQuery = (data: { question: string }) =>
  request.post('/ai/query', data)
