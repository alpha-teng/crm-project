import request from './request'

export interface SystemSettings {
  embeddingUrl: string
  embeddingModel: string
  chatUrl: string
  chatModel: string
  apiKey: string
  topK: number
  similarityThreshold: number
  embeddingDimension: number
  maxSearchResults: number
}

export const getSettings = () => request.get('/settings') as Promise<SystemSettings>

export const updateSettings = (data: Partial<SystemSettings>) => request.put('/settings', data)
