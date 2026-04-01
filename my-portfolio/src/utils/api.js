import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加token等
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// API 方法
export const projectApi = {
  getAll: () => api.get('/projects'),
  getById: (id) => api.get(`/projects/${id}`),
  getByCategory: (category) => api.get(`/projects/category/${category}`),
  create: (data) => api.post('/projects', data),
  update: (id, data) => api.put(`/projects/${id}`, data),
  delete: (id) => api.delete(`/projects/${id}`)
}

export const testimonialApi = {
  getAll: () => api.get('/testimonials'),
  getById: (id) => api.get(`/testimonials/${id}`),
  create: (data) => api.post('/testimonials', data),
  update: (id, data) => api.put(`/testimonials/${id}`, data),
  delete: (id) => api.delete(`/testimonials/${id}`)
}

export const contactApi = {
  getAll: () => api.get('/contacts'),
  getById: (id) => api.get(`/contacts/${id}`),
  create: (data) => api.post('/contacts', data),
  update: (id, data) => api.put(`/contacts/${id}`, data),
  delete: (id) => api.delete(`/contacts/${id}`)
}

export const statsApi = {
  getVisitStats: (params) => api.get('/admin/stats/visits', { params }),
  getIpStats: () => api.get('/admin/stats/ips'),
  getMessageStats: () => api.get('/admin/stats/messages'),
  getProjectStats: () => api.get('/admin/stats/projects'),
  getDashboardData: () => api.get('/admin/dashboard')
}

export const knowledgeApi = {
  getArticles: (params) => api.get('/knowledge/articles', { params }),
  getArticleById: (id) => api.get(`/knowledge/articles/${id}`),
  getArticleBySlug: (slug) => api.get(`/knowledge/articles/slug/${slug}`),
  getArticlesByCategory: (categoryId) => api.get(`/knowledge/articles/category/${categoryId}`),
  getRecentArticles: (limit = 10) => api.get('/knowledge/articles/recent', { params: { limit } }),
  getFeaturedArticles: () => api.get('/knowledge/articles/featured'),
  searchArticles: (keyword, page = 0, size = 20) => api.get('/knowledge/articles/search', { params: { keyword, page, size } }),
  incrementView: (id) => api.post(`/knowledge/articles/${id}/view`),
  toggleLike: (id) => api.post(`/knowledge/articles/${id}/like`)
}

export const knowledgeCategoryApi = {
  getAll: () => api.get('/knowledge/categories'),
  getByDomain: (domainId) => api.get(`/knowledge/categories/domain/${domainId}`),
  getById: (id) => api.get(`/knowledge/categories/${id}`),
  getBySlug: (domainSlug, categorySlug) => api.get(`/knowledge/categories/domain/${domainSlug}/${categorySlug}`)
}

export const knowledgeDomainApi = {
  getAll: () => api.get('/knowledge/domains'),
  getBySlug: (slug) => api.get(`/knowledge/domains/${slug}`)
}

export default api

