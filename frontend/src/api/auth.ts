import request from './request'

export const login = (data: {username: string, password: string}) => 
  request.post('/api/auth/login', data)

export const register = (data: {username: string, password: string, realName: string}) => 
  request.post('/api/auth/register', data)

export const getMe = () => 
  request.get('/api/auth/me')
