<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2>管理后台</h2>
        <div class="user-info" v-if="authStore.isAuthenticated">
          <span class="user-name">{{ authStore.user?.displayName }}</span>
          <span class="user-role">{{ authStore.roles?.join(', ') }}</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <RouterLink to="/admin" class="nav-item" :class="{ active: $route.path === '/admin' }">
          <span class="nav-icon">📊</span>
          <span>仪表板</span>
        </RouterLink>

        <RouterLink to="/admin/articles" class="nav-item" :class="{ active: $route.path.startsWith('/admin/articles') }">
          <span class="nav-icon">📝</span>
          <span>知识库</span>
        </RouterLink>

        <RouterLink to="/admin/editor" class="nav-item" :class="{ active: $route.path === '/admin/editor' }">
          <span class="nav-icon">✏️</span>
          <span>写文章</span>
        </RouterLink>

        <RouterLink to="/admin/gallery" class="nav-item" :class="{ active: $route.path.startsWith('/admin/gallery') }">
          <span class="nav-icon">🎨</span>
          <span>画廊</span>
        </RouterLink>

        <RouterLink to="/admin/media" class="nav-item" :class="{ active: $route.path.startsWith('/admin/media') }">
          <span class="nav-icon">📁</span>
          <span>媒体管理</span>
        </RouterLink>

        <RouterLink to="/admin/messages" class="nav-item" :class="{ active: $route.path === '/admin/messages' }">
          <span class="nav-icon">💬</span>
          <span>消息</span>
        </RouterLink>

        <RouterLink to="/admin/screen" class="nav-item" :class="{ active: $route.path === '/admin/screen' }">
          <span class="nav-icon">📺</span>
          <span>数据大屏</span>
        </RouterLink>

        <RouterLink to="/admin/project-import" class="nav-item" :class="{ active: $route.path === '/admin/project-import' }">
          <span class="nav-icon">📦</span>
          <span>项目导入</span>
        </RouterLink>

        <RouterLink to="/admin/characters" class="nav-item" :class="{ active: $route.path.startsWith('/admin/characters') }">
          <span class="nav-icon">👤</span>
          <span>角色管理</span>
        </RouterLink>

        <button @click="handleLogout" class="nav-item logout">
          <span class="nav-icon">🚪</span>
          <span>退出登录</span>
        </button>

        <RouterLink to="/" class="nav-item">
          <span class="nav-icon">🏠</span>
          <span>返回首页</span>
        </RouterLink>
      </nav>
    </aside>

    <main class="admin-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  try {
    await authStore.fetchCurrentUser()
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

.admin-sidebar {
  width: 280px;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  padding: 2rem 0;
  position: fixed;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-header {
  padding: 0 1.5rem 2rem;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 1rem;
}

.sidebar-header h2 {
  margin: 0 0 1rem 0;
  font-size: 1.5rem;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  font-size: 0.75rem;
  color: var(--accent-primary);
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0 1rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  text-decoration: none;
  color: var(--text-secondary);
  transition: all 0.3s;
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
  font-size: 1rem;
}

.nav-item:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.nav-item.active {
  background: linear-gradient(90deg, rgba(99, 102, 241, 0.1), rgba(139, 92, 246, 0.1));
  color: var(--accent-primary);
  font-weight: 600;
}

.nav-item.logout:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.nav-icon {
  font-size: 1.2rem;
}

.badge {
  margin-left: auto;
  background: #ef4444;
  color: white;
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
}

.admin-content {
  flex: 1;
  margin-left: 280px;
  padding: 2rem;
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 200px;
  }
  
  .admin-content {
    margin-left: 200px;
  }
}
</style>