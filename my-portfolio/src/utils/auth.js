import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
            refreshToken
          })

          const { accessToken, refreshToken: newRefreshToken } = response.data
          localStorage.setItem('accessToken', accessToken)
          localStorage.setItem('refreshToken', newRefreshToken)

          originalRequest.headers.Authorization = `Bearer ${accessToken}`
          return api(originalRequest)
        } catch (refreshError) {
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
          window.location.href = '/login'
          return Promise.reject(refreshError)
        }
      }
    }

    return Promise.reject(error)
  }
)

export default api

export const authApi = {
  login: (username, password) =>
    api.post('/auth/login', { username, password }),

  refresh: (refreshToken) =>
    api.post('/auth/refresh', { refreshToken }),

  logout: () =>
    api.post('/auth/logout'),

  getCurrentUser: () =>
    api.get('/auth/me')
}

export const articleApi = {
  getAll: (params) =>
    api.get('/knowledge/articles', { params }),

  getById: (id) =>
    api.get(`/knowledge/articles/${id}`),

  getBySlug: (slug) =>
    api.get(`/knowledge/articles/slug/${slug}`),

  create: (data) =>
    api.post('/knowledge/articles', data),

  update: (id, data) =>
    api.put(`/knowledge/articles/${id}`, data),

  delete: (id) =>
    api.delete(`/knowledge/articles/${id}`),

  getWorkflowState: (id) =>
    api.get(`/author/articles/${id}/workflow`),

  submitForReview: (id, comment) =>
    api.post(`/author/articles/${id}/submit`, { comment }),

  returnToDraft: (id, comment) =>
    api.post(`/author/articles/${id}/return-to-draft`, { comment }),

  saveVersion: (id, content, reason) =>
    api.post(`/author/articles/${id}/save-version`, { content, reason }),

  approve: (id, comment) =>
    api.post(`/reviewer/articles/${id}/approve`, { comment }),

  reject: (id, comment) =>
    api.post(`/reviewer/articles/${id}/reject`, { comment }),

  offline: (id) =>
    api.post(`/reviewer/articles/${id}/offline`),

  publish: (id) =>
    api.post(`/reviewer/articles/${id}/publish`)
}

export const categoryApi = {
  getAll: () =>
    api.get('/knowledge/categories'),

  getByDomain: (domainId) =>
    api.get(`/knowledge/categories/domain/${domainId}`),

  getById: (id) =>
    api.get(`/knowledge/categories/${id}`),

  create: (data) =>
    api.post('/knowledge/categories', data),

  update: (id, data) =>
    api.put(`/knowledge/categories/${id}`, data),

  delete: (id) =>
    api.delete(`/knowledge/categories/${id}`)
}

export const uploadApi = {
  uploadImage: (file, onProgress) => {
    const formData = new FormData()
    formData.append('file', file)

    return api.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (progressEvent) => {
        if (onProgress) {
          const percent = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          )
          onProgress(percent)
        }
      }
    })
  }
}