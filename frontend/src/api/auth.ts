import axios from 'axios'

// В продакшене API_URL пустой - запросы идут на тот же домен
const API_URL = import.meta.env.VITE_API_URL || ''

export const api = axios.create({
  baseURL: API_URL,
  withCredentials: true,
})

// Глобальный обработчик ошибок
api.interceptors.response.use(
  response => response,
  error => {
    const errorMessage = error.response?.data?.message || 
                        error.message || 
                        'Произошла неизвестная ошибка'
    
    console.error('API Error:', {
      status: error.response?.status,
      message: errorMessage,
      data: error.response?.data
    })
    
    return Promise.reject(new Error(errorMessage))
  }
)

export interface User {
  name: string
  email: string
  avatar?: string
  picture?: string
}

export interface LoginCredentials {
  email: string
  password: string
}

export interface RegisterCredentials {
  name: string
  email: string
  password: string
}

export const getUser = async (): Promise<User> => {
  const response = await api.get('/api/me')
  return response.data
}

export const login = async (credentials: LoginCredentials): Promise<void> => {
  await api.post('/api/auth/login', credentials)
}

export const register = async (credentials: RegisterCredentials): Promise<void> => {
  await api.post('/api/auth/register', credentials)
}

export const loginWithGitHub = () => {
  window.location.href = `${API_URL}/oauth2/authorization/github`
}

export const loginWithGoogle = () => {
  window.location.href = `${API_URL}/oauth2/authorization/google`
}

export const logout = async () => {
  try {
    await api.post('/api/logout')
  } finally {
    window.location.href = '/'
  }
}
