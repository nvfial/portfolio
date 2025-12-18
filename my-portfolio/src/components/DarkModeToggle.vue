<template>
  <button 
    class="dark-mode-toggle" 
    @click="toggleDark"
    :title="isDark ? '切换到亮色模式' : '切换到暗黑模式'"
    aria-label="切换暗黑模式"
  >
    <transition name="icon" mode="out-in">
      <span v-if="isDark" key="moon" class="icon">🌙</span>
      <span v-else key="sun" class="icon">☀️</span>
    </transition>
  </button>
</template>

<script setup>
import { useDark, useToggle } from '@vueuse/core'
import { watch, onMounted, nextTick } from 'vue'

const isDark = useDark({
  storageKey: 'portfolio-theme',
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})

// 手动实现toggle，确保正确工作
const toggleDark = () => {
  isDark.value = !isDark.value
  updateTheme()
}

// 确保HTML元素正确应用class
const updateTheme = async () => {
  await nextTick()
  const html = document.documentElement
  if (isDark.value) {
    html.classList.remove('light')
    html.classList.add('dark')
    localStorage.setItem('portfolio-theme', 'dark')
  } else {
    html.classList.remove('dark')
    html.classList.add('light')
    localStorage.setItem('portfolio-theme', 'light')
  }
  // 强制触发CSS变量更新
  document.body.style.setProperty('--force-update', Date.now().toString())
  // 触发自定义事件，通知其他组件
  window.dispatchEvent(new CustomEvent('theme-change', { 
    detail: isDark.value ? 'dark' : 'light' 
  }))
}

// 监听变化
watch(isDark, () => {
  updateTheme()
}, { immediate: true })

// 初始化
onMounted(() => {
  updateTheme()
})
</script>

<style scoped>
.dark-mode-toggle {
  background: var(--bg-secondary, rgba(255, 255, 255, 0.1));
  border: 1px solid var(--border-color, rgba(255, 255, 255, 0.2));
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.dark-mode-toggle::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
}

.dark-mode-toggle:hover {
  transform: scale(1.1) rotate(15deg);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.dark-mode-toggle:hover::before {
  opacity: 1;
}

.dark-mode-toggle:active {
  transform: scale(0.95);
}

.icon {
  font-size: 1.2rem;
  display: block;
  transition: transform 0.3s;
}

.dark-mode-toggle:hover .icon {
  transform: rotate(180deg);
}

.icon-enter-active,
.icon-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.icon-enter-from {
  opacity: 0;
  transform: rotate(-90deg) scale(0.5);
}

.icon-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.5);
}
</style>

