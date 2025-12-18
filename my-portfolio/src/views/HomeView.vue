<template>
  <section class="windsurf-hero">
    <!-- Windsurf风格抽象波浪背景（左下角） -->
    <div class="windsurf-waves">
      <svg class="waves-svg" viewBox="0 0 1200 800" preserveAspectRatio="none">
        <!-- 多层波浪，从黄色到蓝色渐变 -->
        <path class="wave-layer wave-1" d="M0,800 Q200,600 400,650 T800,700 T1200,750 L1200,800 L0,800 Z" fill="url(#waveGradient1)"></path>
        <path class="wave-layer wave-2" d="M0,800 Q250,550 500,600 T1000,650 T1200,700 L1200,800 L0,800 Z" fill="url(#waveGradient2)"></path>
        <path class="wave-layer wave-3" d="M0,800 Q300,500 600,550 T1200,600 L1200,800 L0,800 Z" fill="url(#waveGradient3)"></path>
        <path class="wave-layer wave-4" d="M0,800 Q350,450 700,500 T1200,550 L1200,800 L0,800 Z" fill="url(#waveGradient4)"></path>
        <!-- 额外的波浪层，让底部更饱满 -->
        <path class="wave-layer wave-5" d="M0,800 Q400,400 800,450 T1200,500 L1200,800 L0,800 Z" fill="url(#waveGradient5)"></path>
        <defs>
          <linearGradient id="waveGradient1" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :stop-color="isDark ? '#FFD700' : '#FFE066'" :stop-opacity="isDark ? 0.8 : 0.3" />
            <stop offset="100%" :stop-color="isDark ? '#FF8C00' : '#FFB84D'" :stop-opacity="isDark ? 0.6 : 0.2" />
          </linearGradient>
          <linearGradient id="waveGradient2" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :stop-color="isDark ? '#FF8C00' : '#FFA366'" :stop-opacity="isDark ? 0.7 : 0.25" />
            <stop offset="100%" :stop-color="isDark ? '#FF69B4' : '#FF99CC'" :stop-opacity="isDark ? 0.5 : 0.15" />
          </linearGradient>
          <linearGradient id="waveGradient3" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :stop-color="isDark ? '#FF69B4' : '#CC99FF'" :stop-opacity="isDark ? 0.6 : 0.2" />
            <stop offset="100%" :stop-color="isDark ? '#9370DB' : '#B399FF'" :stop-opacity="isDark ? 0.4 : 0.1" />
          </linearGradient>
          <linearGradient id="waveGradient4" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :stop-color="isDark ? '#9370DB' : '#9999FF'" :stop-opacity="isDark ? 0.5 : 0.15" />
            <stop offset="100%" :stop-color="isDark ? '#4169E1' : '#6699FF'" :stop-opacity="isDark ? 0.3 : 0.08" />
          </linearGradient>
          <linearGradient id="waveGradient5" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :stop-color="isDark ? '#4169E1' : '#8099FF'" :stop-opacity="isDark ? 0.4 : 0.12" />
            <stop offset="100%" :stop-color="isDark ? '#1E3A8A' : '#4D7FFF'" :stop-opacity="isDark ? 0.25 : 0.06" />
          </linearGradient>
        </defs>
      </svg>
    </div>

    <!-- 右侧虚线波浪图案 -->
    <div class="dotted-waves">
      <svg class="dotted-svg" viewBox="0 0 400 600" preserveAspectRatio="none">
        <path class="dotted-path" :stroke="isDark ? '#4A90E2' : '#667eea'" stroke-width="3" fill="none" stroke-dasharray="8,4" :opacity="isDark ? 0.6 : 0.4" d="M0,100 Q100,50 200,100 T400,100"></path>
        <path class="dotted-path" :stroke="isDark ? '#4A90E2' : '#667eea'" stroke-width="3" fill="none" stroke-dasharray="8,4" :opacity="isDark ? 0.6 : 0.4" d="M0,200 Q100,150 200,200 T400,200"></path>
        <path class="dotted-path" :stroke="isDark ? '#4A90E2' : '#667eea'" stroke-width="3" fill="none" stroke-dasharray="8,4" :opacity="isDark ? 0.6 : 0.4" d="M0,300 Q100,250 200,300 T400,300"></path>
      </svg>
    </div>

    <!-- 装饰性几何图形 -->
    <div class="decorative-elements">
      <!-- 左上角装饰圆 -->
      <div class="decor-circle decor-circle-1"></div>
      <div class="decor-circle decor-circle-2"></div>
      <!-- 右上角装饰 -->
      <div class="decor-square decor-square-1"></div>
      <div class="decor-square decor-square-2"></div>
      <!-- 中间装饰点 -->
      <div class="decor-dot decor-dot-1"></div>
      <div class="decor-dot decor-dot-2"></div>
      <div class="decor-dot decor-dot-3"></div>
    </div>

    <!-- 粒子背景 -->
    <ParticlesBackground id="home-particles" />

    <!-- 主要内容 -->
    <div class="hero-content">
      <h1 class="hero-headline" ref="headlineRef">
        Where developers are doing their best work.
      </h1>

      <p class="hero-tagline" ref="taglineRef">
        <span class="tagline-bullet"></span>
        专注于创造优雅、高效的数字体验。用代码连接创意与现实。
      </p>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import ParticlesBackground from '../components/ParticlesBackground.vue'

gsap.registerPlugin(ScrollTrigger)

const isDark = useDark({
  storageKey: 'portfolio-theme',
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})
const headlineRef = ref(null)
const taglineRef = ref(null)

// 粒子效果已通过 useParticles composable 处理

// 初始化动画
const initAnimations = () => {
  const tl = gsap.timeline()
  
  tl.from(headlineRef.value, {
    opacity: 0,
    y: 30,
    duration: 1,
    ease: 'power3.out'
  })
  .from(taglineRef.value, {
    opacity: 0,
    y: 15,
    duration: 0.8,
    ease: 'power2.out'
  }, '-=0.3')
}

// 粒子效果已通过 useParticles composable 处理

onMounted(async () => {
  if (headlineRef.value && taglineRef.value) {
    initAnimations()
  }
  
  // 确保主题状态正确同步（防止路由切换时主题被重置）
  await nextTick()
  const html = document.documentElement
  const savedTheme = localStorage.getItem('portfolio-theme')
  
  if (savedTheme) {
    const shouldBeDark = savedTheme === 'dark'
    if (shouldBeDark !== isDark.value) {
      isDark.value = shouldBeDark
    }
    if (shouldBeDark) {
      html.classList.add('dark')
      html.classList.remove('light')
    } else {
      html.classList.add('light')
      html.classList.remove('dark')
    }
  }
})

onUnmounted(() => {
  ScrollTrigger.getAll().forEach(trigger => trigger.kill())
})
</script>

<style scoped>
.windsurf-hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: var(--bg-primary);
  padding: 0 2rem;
  margin: 0;
  transition: background-color 0.3s ease;
}

/* Windsurf波浪背景（占满底部） */
.windsurf-waves {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 70%;
  z-index: 1;
  pointer-events: none;
}

.waves-svg {
  width: 100%;
  height: 100%;
}

.wave-layer {
  animation: waveFloat 8s ease-in-out infinite;
}

.wave-1 {
  animation-delay: 0s;
}

.wave-2 {
  animation-delay: -2s;
}

.wave-3 {
  animation-delay: -4s;
}

.wave-4 {
  animation-delay: -6s;
}

.wave-5 {
  animation-delay: -8s;
}

.wave-5 {
  animation-delay: -8s;
}

@keyframes waveFloat {
  0%, 100% {
    transform: translateY(0) scaleY(1);
  }
  50% {
    transform: translateY(-15px) scaleY(1.05);
  }
}

/* 右侧虚线波浪 */
.dotted-waves {
  position: absolute;
  right: 5%;
  top: 20%;
  width: 300px;
  height: 400px;
  z-index: 1;
  pointer-events: none;
}

.dotted-svg {
  width: 100%;
  height: 100%;
}

.dotted-path {
  animation: dottedMove 10s ease-in-out infinite;
}

.dotted-path:nth-child(2) {
  animation-delay: -3s;
}

.dotted-path:nth-child(3) {
  animation-delay: -6s;
}

@keyframes dottedMove {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 装饰性元素 */
.decorative-elements {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  overflow: hidden;
}

.decor-circle {
  position: absolute;
  border-radius: 50%;
  border: 2px solid;
  animation: float 6s ease-in-out infinite;
}

.decor-circle-1 {
  width: 120px;
  height: 120px;
  top: 15%;
  left: 8%;
  border-color: var(--accent-primary);
  opacity: 0.15;
  animation-delay: 0s;
}

.decor-circle-2 {
  width: 80px;
  height: 80px;
  top: 25%;
  left: 12%;
  border-color: var(--accent-secondary);
  opacity: 0.2;
  animation-delay: -2s;
}

.decor-square {
  position: absolute;
  border: 2px solid;
  transform: rotate(45deg);
  animation: rotate 8s linear infinite;
}

.decor-square-1 {
  width: 60px;
  height: 60px;
  top: 12%;
  right: 15%;
  border-color: var(--accent-tertiary);
  opacity: 0.12;
}

.decor-square-2 {
  width: 40px;
  height: 40px;
  top: 18%;
  right: 20%;
  border-color: var(--accent-primary);
  opacity: 0.15;
  animation-duration: 6s;
}

.decor-dot {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--accent-primary);
  animation: pulse 3s ease-in-out infinite;
}

.decor-dot-1 {
  top: 30%;
  left: 20%;
  opacity: 0.4;
  animation-delay: 0s;
}

.decor-dot-2 {
  top: 50%;
  right: 25%;
  opacity: 0.3;
  animation-delay: -1s;
}

.decor-dot-3 {
  top: 70%;
  left: 30%;
  opacity: 0.35;
  animation-delay: -2s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) translateX(0);
  }
  33% {
    transform: translateY(-20px) translateX(10px);
  }
  66% {
    transform: translateY(10px) translateX(-10px);
  }
}

@keyframes rotate {
  0% {
    transform: rotate(45deg);
  }
  100% {
    transform: rotate(405deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.6;
  }
}

/* 粒子容器 */
.particles-container {
  position: absolute;
  inset: 0;
  z-index: 2;
  width: 100%;
  height: 100%;
  pointer-events: none;
  opacity: 1;
  overflow: hidden;
}

.particles-container canvas {
  width: 100% !important;
  height: 100% !important;
  display: block;
  position: absolute;
  top: 0;
  left: 0;
}

/* 主要内容 */
.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  max-width: 1200px;
  width: 100%;
  padding: 0 2rem;
  margin-top: 80px; /* 为导航栏留出空间 */
  margin-left: auto;
  margin-right: auto;
}

.hero-headline {
  font-size: clamp(3rem, 8vw, 5.5rem);
  font-weight: 700;
  line-height: 1.1;
  color: var(--text-primary);
  margin-bottom: 2.5rem;
  letter-spacing: -0.02em;
  transition: color 0.3s ease;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1.75rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: none;
  position: relative;
  z-index: 100;
  pointer-events: auto;
}

.btn-download {
  background: #10b981;
  color: #ffffff;
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.3);
}

.btn-download:hover {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.btn-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.btn-explore {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.btn-explore:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.hero-tagline {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  font-size: 1.125rem;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
  transition: color 0.3s ease;
}

.tagline-bullet {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .windsurf-hero {
    padding: 0 1rem;
  }

  .hero-content {
    padding-left: 0;
    text-align: center;
  }

  .windsurf-waves {
    width: 80%;
    height: 50%;
  }

  .dotted-waves {
    display: none;
  }

  .hero-buttons {
    justify-content: center;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
