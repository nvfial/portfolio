<template>
  <button 
    class="particle-toggle" 
    @click="toggle"
    :class="{ active: enabled }"
    :title="`粒子效果: ${enabled ? '开' : '关'}`"
    aria-label="切换粒子效果"
  >
    <span class="icon-wrapper">
      <transition name="particle" mode="out-in">
        <span v-if="enabled" key="on" class="icon">✨</span>
        <span v-else key="off" class="icon">💤</span>
      </transition>
    </span>
    <span class="ripple" v-if="enabled"></span>
  </button>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const enabled = ref(localStorage.getItem('particles-enabled') !== 'false')

const toggle = () => {
  enabled.value = !enabled.value
  localStorage.setItem('particles-enabled', enabled.value.toString())
  window.dispatchEvent(new CustomEvent('particles-toggle', { 
    detail: enabled.value 
  }))
}

onMounted(() => {
  // 初始化时触发事件
  window.dispatchEvent(new CustomEvent('particles-toggle', { 
    detail: enabled.value 
  }))
})
</script>

<style scoped>
.particle-toggle {
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
  overflow: visible;
}

.particle-toggle.active {
  background: rgba(138, 43, 226, 0.2);
  border-color: rgba(138, 43, 226, 0.5);
  box-shadow: 0 0 20px rgba(138, 43, 226, 0.3);
}

.particle-toggle:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.particle-toggle:active {
  transform: scale(0.95);
}

.icon-wrapper {
  position: relative;
  z-index: 1;
}

.icon {
  font-size: 1.2rem;
  display: block;
  transition: transform 0.3s;
}

.particle-toggle:hover .icon {
  transform: scale(1.2);
}

.ripple {
  position: absolute;
  inset: -10px;
  border-radius: 50%;
  border: 2px solid rgba(138, 43, 226, 0.5);
  animation: ripple 2s infinite;
}

@keyframes ripple {
  0% {
    transform: scale(0.8);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

.particle-enter-active,
.particle-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.particle-enter-from {
  opacity: 0;
  transform: scale(0) rotate(-180deg);
}

.particle-leave-to {
  opacity: 0;
  transform: scale(0) rotate(180deg);
}
</style>

