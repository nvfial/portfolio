import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import { loadSlim } from '@tsparticles/slim'
import { tsParticles } from '@tsparticles/engine'

export function useParticles(containerRef) {
  const isDark = useDark({
    storageKey: 'portfolio-theme',
    selector: 'html',
    attribute: 'class',
    valueDark: 'dark',
    valueLight: 'light'
  })

  const showParticles = ref(true)
  const particlesInitialized = ref(false)
  let particlesInstance = null

  // 增强的粒子配置 - 白天黑夜不同，更明显
  const particleOptions = computed(() => {
    // 暗黑模式：紫色、粉色系
    // 亮色模式：蓝色、紫色系
    const colors = isDark.value 
      ? ['#818cf8', '#a78bfa', '#ec4899', '#f472b6', '#c084fc', '#fbbf24', '#ffffff']
      : ['#667eea', '#764ba2', '#4A90E2', '#5BA3F5', '#6BB6FF', '#818cf8', '#a78bfa']
    
    return {
      background: {
        color: {
          value: 'transparent'
        }
      },
      fpsLimit: 120,
      particles: {
        number: {
          value: isDark.value ? 200 : 180, // 更多粒子
          density: {
            enable: true,
            area: 500 // 更密集
          }
        },
        color: {
          value: colors
        },
        shape: {
          type: 'circle'
        },
        opacity: {
          value: isDark.value 
            ? { min: 0.7, max: 1.0 }  // 暗黑模式更亮
            : { min: 0.8, max: 1.0 }, // 亮色模式更明显
          animation: {
            enable: true,
            speed: 1,
            sync: false,
            destroy: 'none'
          }
        },
        size: {
          value: isDark.value 
            ? { min: 5, max: 10 }  // 暗黑模式更大
            : { min: 4, max: 9 },  // 亮色模式稍小但明显
          animation: {
            enable: true,
            speed: 2,
            sync: false,
            destroy: 'none'
          }
        },
        move: {
          enable: true,
          speed: { min: 1.5, max: 3 },
          direction: 'none',
          random: true,
          straight: false,
          outModes: {
            default: 'bounce'
          },
          attract: {
            enable: false
          }
        },
        links: {
          enable: true,
          distance: isDark.value ? 180 : 160, // 暗黑模式连线更远
          color: isDark.value ? '#818cf8' : '#667eea',
          opacity: isDark.value ? 0.5 : 0.4,  // 更明显的连线
          width: 2, // 更粗的连线
          triangles: {
            enable: true,
            opacity: 0.1
          }
        },
        collisions: {
          enable: true,
          mode: 'bounce'
        }
      },
      interactivity: {
        detectsOn: 'window',
        events: {
          onHover: {
            enable: true,
            mode: ['grab', 'bubble'] // 抓取和气泡效果
          },
          onClick: {
            enable: true,
            mode: ['push', 'repulse'] // 推动和排斥
          },
          resize: true
        },
        modes: {
          grab: {
            distance: 250,
            links: {
              opacity: 1,
              blink: false
            }
          },
          bubble: {
            distance: 250,
            size: 8,
            duration: 2,
            opacity: 0.8
          },
          push: {
            quantity: 8
          },
          repulse: {
            distance: 200,
            duration: 0.4
          }
        }
      },
      detectRetina: true,
      pauseOnBlur: false,
      pauseOnOutsideViewport: false
    }
  })

  // 初始化粒子效果
  const initParticles = async () => {
    if (!showParticles.value) {
      return
    }
    
    if (!containerRef.value) {
      await nextTick()
      if (!containerRef.value) {
        setTimeout(() => initParticles(), 100)
        return
      }
    }
    
    try {
      if (!tsParticles.dom) {
        await loadSlim(tsParticles)
      }
      
      if (particlesInstance) {
        particlesInstance.destroy()
        particlesInstance = null
      }
      
      particlesInstance = await tsParticles.load({
        id: `tsparticles-${Date.now()}`,
        element: containerRef.value,
        options: particleOptions.value
      })
      
      particlesInitialized.value = true
    } catch (error) {
      console.error('粒子初始化失败:', error)
      particlesInitialized.value = false
    }
  }

  // 销毁粒子效果
  const destroyParticles = () => {
    if (particlesInstance) {
      particlesInstance.destroy()
      particlesInstance = null
      particlesInitialized.value = false
    }
  }

  // 监听粒子开关事件
  const handleParticleToggle = async (e) => {
    showParticles.value = e.detail
    
    if (e.detail) {
      if (!particlesInitialized.value) {
        await nextTick()
        await initParticles()
      }
    } else {
      destroyParticles()
    }
  }

  // 监听主题变化，重新初始化粒子
  const handleThemeChange = async () => {
    if (showParticles.value && particlesInitialized.value) {
      destroyParticles()
      await nextTick()
      await initParticles()
    }
  }

  onMounted(async () => {
    const particlesEnabled = localStorage.getItem('particles-enabled')
    if (particlesEnabled === null) {
      showParticles.value = true
      localStorage.setItem('particles-enabled', 'true')
    } else {
      showParticles.value = particlesEnabled !== 'false'
    }
    
    window.addEventListener('particles-toggle', handleParticleToggle)
    window.addEventListener('theme-change', handleThemeChange)
    
    await nextTick()
    await nextTick()
    
    if (showParticles.value) {
      setTimeout(() => {
        initParticles()
      }, 200)
    }
  })

  onUnmounted(() => {
    window.removeEventListener('particles-toggle', handleParticleToggle)
    window.removeEventListener('theme-change', handleThemeChange)
    destroyParticles()
  })

  return {
    showParticles,
    particlesInitialized,
    particleOptions
  }
}






