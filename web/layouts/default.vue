<template>
  <div class="default-layout">
    <nav class="navbar" :class="{ scrolled: isScrolled }">
      <div class="nav-container">
        <NuxtLink to="/" class="nav-logo">
          <span class="logo-text">Portfolio</span>
          <span class="logo-dot">.</span>
        </NuxtLink>
        
        <div class="nav-menu" :class="{ open: menuOpen }">
          <NuxtLink to="/knowledge" class="nav-link">知识库</NuxtLink>
          <NuxtLink to="/gallery" class="nav-link">画廊</NuxtLink>
          <NuxtLink to="/mindmap" class="nav-link">图谱</NuxtLink>
          <NuxtLink to="/guestbook" class="nav-link">留言</NuxtLink>
        </div>
        
        <div class="nav-actions">
          <button class="theme-toggle" @click="toggleTheme">
            {{ isDark ? '☀️' : '🌙' }}
          </button>
        </div>
        
        <button class="nav-toggle" @click="menuOpen = !menuOpen">
          <span></span>
          <span></span>
          <span></span>
        </button>
      </div>
    </nav>
    
    <main class="main-content">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isDark = ref(false)
const isScrolled = ref(false)
const menuOpen = ref(false)

const toggleTheme = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
  localStorage.setItem('portfolio-theme', isDark.value ? 'dark' : 'light')
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  
  const savedTheme = localStorage.getItem('portfolio-theme')
  if (savedTheme === 'dark') {
    isDark.value = true
    document.documentElement.classList.add('dark')
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.default-layout {
  min-height: 100vh;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all var(--transition-base);
}

.dark .navbar {
  background: rgba(10, 14, 39, 0.85);
  border-bottom-color: rgba(255, 255, 255, 0.1);
}

.navbar.scrolled {
  box-shadow: var(--shadow-md);
  background: rgba(255, 255, 255, 0.95);
}

.dark .navbar.scrolled {
  background: rgba(10, 14, 39, 0.95);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-logo {
  font-size: 1.5rem;
  font-weight: 700;
  display: flex;
  align-items: baseline;
  color: var(--text-primary);
}

.logo-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.logo-dot {
  color: var(--accent-primary);
  font-size: 1.2em;
}

.nav-menu {
  display: flex;
  gap: 2rem;
}

.nav-link {
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: color var(--transition-fast);
  position: relative;
}

.nav-link:hover {
  color: var(--accent-primary);
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--accent-primary);
  transition: width var(--transition-fast);
}

.nav-link:hover::after {
  width: 100%;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.theme-toggle {
  font-size: 1.25rem;
  padding: 0.5rem;
  border-radius: 50%;
  background: var(--bg-secondary);
  transition: all var(--transition-fast);
}

.theme-toggle:hover {
  transform: scale(1.1);
}

.nav-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: 0.5rem;
}

.nav-toggle span {
  width: 24px;
  height: 2px;
  background: var(--text-primary);
  transition: all var(--transition-fast);
}

@media (max-width: 768px) {
  .nav-toggle {
    display: flex;
  }
  
  .nav-menu {
    position: fixed;
    top: 70px;
    left: 0;
    right: 0;
    background: var(--bg-secondary);
    flex-direction: column;
    padding: 2rem;
    gap: 1.5rem;
    transform: translateY(-100%);
    opacity: 0;
    transition: all var(--transition-base);
    pointer-events: none;
  }
  
  .nav-menu.open {
    transform: translateY(0);
    opacity: 1;
    pointer-events: all;
  }
}

.main-content {
  min-height: 100vh;
}
</style>
