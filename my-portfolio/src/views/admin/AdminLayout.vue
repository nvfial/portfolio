<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2>管理后台</h2>
      </div>
      <nav class="sidebar-nav">
        <RouterLink to="/admin" class="nav-item" :class="{ active: $route.path === '/admin' }">
          <span class="nav-icon">📊</span>
          <span>仪表板</span>
        </RouterLink>
        <RouterLink to="/admin/messages" class="nav-item" :class="{ active: $route.path === '/admin/messages' }">
          <span class="nav-icon">💬</span>
          <span>消息管理</span>
        </RouterLink>
        <RouterLink to="/admin/screen" class="nav-item" :class="{ active: $route.path === '/admin/screen' }">
          <span class="nav-icon">📺</span>
          <span>数据大屏</span>
        </RouterLink>
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
import { RouterLink, RouterView } from 'vue-router'
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

.admin-sidebar {
  width: 250px;
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
  margin: 0;
  font-size: 1.5rem;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
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

.nav-icon {
  font-size: 1.2rem;
}

.admin-content {
  flex: 1;
  margin-left: 250px;
  padding: 0;
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






