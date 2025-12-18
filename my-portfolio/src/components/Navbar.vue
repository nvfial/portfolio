<template>
  <nav class="navbar" :class="{ scrolled: isScrolled }">
    <div class="nav-container container">
      <RouterLink to="/" class="nav-logo" @click="closeMenu">
        <span class="logo-text">Portfolio</span>
        <span class="logo-dot">.</span>
      </RouterLink>
      
      <ul class="nav-menu" :class="{ active: isMenuOpen }">
        <li v-for="item in navItems" :key="item.path">
          <RouterLink 
            @click="closeMenu" 
            :to="item.path" 
            class="nav-link"
            :class="{ 'router-link-active': $route.path === item.path, 'admin-link': item.admin }"
          >
            <span class="nav-link-text">{{ item.label }}</span>
            <span class="nav-link-underline"></span>
          </RouterLink>
        </li>
      </ul>
      
      <div class="nav-actions">
        <ParticleToggle />
        <DarkModeToggle />
      </div>
      
      <button 
        class="nav-toggle" 
        @click="toggleMenu"
        :class="{ active: isMenuOpen }"
        aria-label="切换菜单"
      >
        <span class="bar"></span>
        <span class="bar"></span>
        <span class="bar"></span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import DarkModeToggle from './DarkModeToggle.vue'
import ParticleToggle from './ParticleToggle.vue'

const isMenuOpen = ref(false)
const isScrolled = ref(false)

const navItems = [
  { label: '首页', path: '/' },
  { label: '项目', path: '/projects' },
  { label: '关于我', path: '/about' },
  { label: '留言板', path: '/testimonials' },
  { label: '联系', path: '/contact' },
  { label: '管理后台', path: '/admin', admin: true }
]

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const closeMenu = () => {
  isMenuOpen.value = false
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dark .navbar {
  background: rgba(10, 14, 39, 0.8);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  gap: 2rem;
}

.nav-logo {
  font-size: 1.5rem;
  font-weight: 700;
  text-decoration: none;
  color: var(--text-primary);
  display: flex;
  align-items: baseline;
  gap: 2px;
  transition: transform 0.3s;
}

.nav-logo:hover {
  transform: scale(1.05);
}

.logo-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-dot {
  color: var(--accent-primary);
  font-size: 1.2em;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: 2rem;
  align-items: center;
}

.nav-link {
  position: relative;
  text-decoration: none;
  color: var(--text-primary);
  font-weight: 500;
  font-size: 0.95rem;
  padding: 0.5rem 0;
  transition: color 0.3s;
  overflow: visible;
  display: inline-block;
}

.nav-link-text {
  position: relative;
  z-index: 2;
  display: inline-block;
  background: transparent;
  transition: color 0.3s;
}

.nav-link-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--gradient-primary);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 2px;
  z-index: 1;
}

.nav-link:hover .nav-link-underline,
.nav-link.router-link-active .nav-link-underline {
  width: 100%;
}

.nav-link:hover .nav-link-text,
.nav-link.router-link-active .nav-link-text {
  color: var(--accent-primary);
}

.nav-link.admin-link {
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 600;
}

.nav-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.nav-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  z-index: 1001;
}

.bar {
  width: 25px;
  height: 3px;
  background: var(--text-primary);
  border-radius: 2px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-toggle.active .bar:nth-child(1) {
  transform: rotate(45deg) translate(8px, 8px);
}

.nav-toggle.active .bar:nth-child(2) {
  opacity: 0;
}

.nav-toggle.active .bar:nth-child(3) {
  transform: rotate(-45deg) translate(7px, -7px);
}

/* 移动端样式 */
@media (max-width: 768px) {
  .nav-container {
    padding: 1rem;
  }
  
  .nav-menu {
    position: fixed;
    left: -100%;
    top: 70px;
    flex-direction: column;
    background: var(--bg-primary);
    width: 100%;
    padding: 2rem 0;
    box-shadow: var(--shadow-lg);
    transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    gap: 0;
    border-top: 1px solid var(--border-color);
  }

  .nav-menu.active {
    left: 0;
  }

  .nav-link {
    display: block;
    padding: 1rem 2rem;
    width: 100%;
    border-bottom: 1px solid var(--border-color);
  }

  .nav-link-underline {
    display: none;
  }

  .nav-actions {
    gap: 0.5rem;
  }

  .nav-toggle {
    display: flex;
  }
  
  .nav-logo {
    font-size: 1.25rem;
  }
}

@media (max-width: 480px) {
  .nav-actions {
    gap: 0.25rem;
  }
}
</style>
