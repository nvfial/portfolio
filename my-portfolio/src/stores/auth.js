import { defineStore } from 'pinia'
import { authApi } from '../utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    isAuthenticated: !!localStorage.getItem('accessToken'),
    permissions: [],
    roles: []
  }),

  getters: {
    isAdmin: (state) => state.roles?.includes('ADMIN'),
    isReviewer: (state) => state.roles?.includes('REVIEWER') || state.roles?.includes('ADMIN'),
    isAuthor: (state) => state.roles?.includes('AUTHOR') || state.roles?.includes('REVIEWER') || state.roles?.includes('ADMIN'),
    hasPermission: (state) => (permission) => state.permissions?.includes(permission),
    currentUser: (state) => state.user
  },

  actions: {
    async login(username, password) {
      try {
        const response = await authApi.login(username, password)
        const { accessToken, refreshToken, user } = response.data

        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.user = user
        this.isAuthenticated = true
        this.permissions = user.permissions || []
        this.roles = user.roles || []

        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)

        return response.data
      } catch (error) {
        this.logout()
        throw error
      }
    },

    async refreshAccessToken() {
      if (!this.refreshToken) {
        throw new Error('No refresh token')
      }

      try {
        const response = await authApi.refresh(this.refreshToken)
        const { accessToken, refreshToken } = response.data

        this.accessToken = accessToken
        this.refreshToken = refreshToken

        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)

        return accessToken
      } catch (error) {
        this.logout()
        throw error
      }
    },

    async fetchCurrentUser() {
      try {
        const response = await authApi.getCurrentUser()
        this.user = response.data
        this.permissions = response.data.permissions || []
        this.roles = response.data.roles || []
        return response.data
      } catch (error) {
        this.logout()
        throw error
      }
    },

    logout() {
      this.user = null
      this.accessToken = null
      this.refreshToken = null
      this.isAuthenticated = false
      this.permissions = []
      this.roles = []

      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
    },

    hasRole(role) {
      return this.roles?.includes(role)
    },

    canAccess(path) {
      if (!this.isAuthenticated) return false

      const rolePermissions = {
        '/admin': ['ADMIN'],
        '/author': ['ADMIN', 'REVIEWER', 'AUTHOR'],
        '/reviewer': ['ADMIN', 'REVIEWER']
      }

      const requiredRoles = rolePermissions[path]
      if (!requiredRoles) return true

      return requiredRoles.some(role => this.roles?.includes(role))
    }
  }
})