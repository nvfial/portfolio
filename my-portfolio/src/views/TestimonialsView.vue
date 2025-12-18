<template>
  <section class="testimonials-page">
    <ParticlesBackground id="testimonials-particles" />
    <div class="container">
      <div class="page-header">
        <div class="header-left">
          <div class="section-indicator">
            <span class="indicator-dot"></span>
            <span class="indicator-label">TESTIMONIALS</span>
          </div>
        </div>
        <div class="header-right">
          <h1 class="page-title">Trusted by the community</h1>
        </div>
      </div>

      <!-- 留言表单 -->
      <div class="testimonial-form-section" ref="formRef">
        <div class="form-card">
          <h2 class="form-title">留下你的评价</h2>
          <form @submit.prevent="submitTestimonial" class="testimonial-form">
            <div class="form-group">
              <label for="name">姓名</label>
              <input 
                type="text" 
                id="name" 
                v-model="form.name" 
                placeholder="你的姓名"
                required
              />
            </div>
            <div class="form-group">
              <label for="username">用户名</label>
              <input 
                type="text" 
                id="username" 
                v-model="form.username" 
                placeholder="@username"
                required
              />
            </div>
            <div class="form-group">
              <label for="message">评价内容</label>
              <textarea 
                id="message" 
                v-model="form.message" 
                placeholder="分享你的想法..."
                rows="5"
                required
              ></textarea>
            </div>
            <button type="submit" class="submit-btn" :disabled="submitting">
              <span v-if="!submitting">提交评价</span>
              <span v-else>提交中...</span>
            </button>
          </form>
        </div>
      </div>

      <!-- 留言轮播容器 - 两排布局 -->
      <div class="testimonials-carousel-wrapper">
        <div class="carousel-fade-left"></div>
        <div class="carousel-fade-right"></div>
        <!-- 第一排 -->
        <div class="carousel-track" ref="carouselTrackRef1">
          <!-- 第一组留言 -->
          <div 
            v-for="(testimonial, index) in firstRowTestimonials" 
            :key="`first-row-1-${testimonial.id}-${index}`"
            class="testimonial-card"
            :data-index="index"
            @mouseenter="handleCardHover($event)"
            @mouseleave="handleCardLeave($event)"
          >
            <div class="testimonial-header">
              <div class="testimonial-avatar">
                <img 
                  :src="testimonial.avatar" 
                  :alt="testimonial.name"
                  @error="handleAvatarError"
                />
              </div>
              <div class="testimonial-info">
                <h4 class="testimonial-name">{{ testimonial.name }}</h4>
                <p class="testimonial-username">{{ testimonial.username }}</p>
              </div>
              <div class="testimonial-platform">𝕏</div>
            </div>
            <div class="testimonial-separator"></div>
            <p class="testimonial-text">{{ testimonial.text }}</p>
          </div>
          <!-- 第二组留言（用于无缝循环） -->
          <div 
            v-for="(testimonial, index) in firstRowTestimonials" 
            :key="`first-row-2-${testimonial.id}-${index}`"
            class="testimonial-card"
            :data-index="index + firstRowTestimonials.length"
            @mouseenter="handleCardHover($event)"
            @mouseleave="handleCardLeave($event)"
          >
            <div class="testimonial-header">
              <div class="testimonial-avatar">
                <img 
                  :src="testimonial.avatar" 
                  :alt="testimonial.name"
                  @error="handleAvatarError"
                />
              </div>
              <div class="testimonial-info">
                <h4 class="testimonial-name">{{ testimonial.name }}</h4>
                <p class="testimonial-username">{{ testimonial.username }}</p>
              </div>
              <div class="testimonial-platform">𝕏</div>
            </div>
            <div class="testimonial-separator"></div>
            <p class="testimonial-text">{{ testimonial.text }}</p>
          </div>
        </div>
        <!-- 第二排 -->
        <div class="carousel-track carousel-track-row2" ref="carouselTrackRef2">
          <!-- 第一组留言 -->
          <div 
            v-for="(testimonial, index) in secondRowTestimonials" 
            :key="`second-row-1-${testimonial.id}-${index}`"
            class="testimonial-card"
            :data-index="index"
            @mouseenter="handleCardHover($event)"
            @mouseleave="handleCardLeave($event)"
          >
            <div class="testimonial-header">
              <div class="testimonial-avatar">
                <img 
                  :src="testimonial.avatar" 
                  :alt="testimonial.name"
                  @error="handleAvatarError"
                />
              </div>
              <div class="testimonial-info">
                <h4 class="testimonial-name">{{ testimonial.name }}</h4>
                <p class="testimonial-username">{{ testimonial.username }}</p>
              </div>
              <div class="testimonial-platform">𝕏</div>
            </div>
            <div class="testimonial-separator"></div>
            <p class="testimonial-text">{{ testimonial.text }}</p>
          </div>
          <!-- 第二组留言（用于无缝循环） -->
          <div 
            v-for="(testimonial, index) in secondRowTestimonials" 
            :key="`second-row-2-${testimonial.id}-${index}`"
            class="testimonial-card"
            :data-index="index + secondRowTestimonials.length"
            @mouseenter="handleCardHover($event)"
            @mouseleave="handleCardLeave($event)"
          >
            <div class="testimonial-header">
              <div class="testimonial-avatar">
                <img 
                  :src="testimonial.avatar" 
                  :alt="testimonial.name"
                  @error="handleAvatarError"
                />
              </div>
              <div class="testimonial-info">
                <h4 class="testimonial-name">{{ testimonial.name }}</h4>
                <p class="testimonial-username">{{ testimonial.username }}</p>
              </div>
              <div class="testimonial-platform">𝕏</div>
            </div>
            <div class="testimonial-separator"></div>
            <p class="testimonial-text">{{ testimonial.text }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import ParticlesBackground from '../components/ParticlesBackground.vue'
import { testimonialApi } from '../utils/api'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const formRef = ref(null)
const carouselTrackRef1 = ref(null)
const carouselTrackRef2 = ref(null)
const animationInstance1 = ref(null)
const animationInstance2 = ref(null)
let hoveredCard = null
let hoveredCardAnimation = null
let hoverTimeout = null
let isProcessingHover = false
let lastHoverTime = 0
const HOVER_THROTTLE = 100 // 节流时间100ms

const submitting = ref(false)
const isPaused = ref(false)

const form = ref({
  name: '',
  username: '',
  message: ''
})

// 初始推荐数据
const allTestimonials = ref([
  {
    id: 1,
    name: '李开发',
    username: '@likaifa',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=John',
    text: '这个作品集网站的设计非常出色，交互体验流畅，代码质量也很高。'
  },
  {
    id: 2,
    name: '王设计师',
    username: '@wangdesigner',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jane',
    text: '从用户体验到视觉效果，都体现了专业水准。特别是暗黑模式和粒子效果，非常棒！'
  },
  {
    id: 3,
    name: '张全栈',
    username: '@zhangfullstack',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Bob',
    text: '作为一个全栈开发者，我特别欣赏这个项目的技术栈选择和实现方式。'
  },
  {
    id: 4,
    name: '刘前端',
    username: '@liufrontend',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Alice',
    text: 'Vue 3 + GSAP 的组合使用得非常巧妙，动画效果自然流畅。'
  },
  {
    id: 5,
    name: '陈创意',
    username: '@chencreative',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Charlie',
    text: '创意和技术的完美结合，这个作品集展示了现代Web开发的最佳实践。'
  },
  {
    id: 6,
    name: '周工程师',
    username: '@zhouengineer',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=David',
    text: '响应式设计做得很好，在不同设备上都能完美展示。代码结构清晰，易于维护。'
  }
])

// 将留言分为两排
const firstRowTestimonials = computed(() => {
  return allTestimonials.value.filter((_, index) => index % 2 === 0)
})

const secondRowTestimonials = computed(() => {
  return allTestimonials.value.filter((_, index) => index % 2 === 1)
})

// 从API加载留言
const loadTestimonials = async () => {
  try {
    const data = await testimonialApi.getAll()
    // 转换数据格式
    allTestimonials.value = data.map(t => ({
      id: t.id,
      name: t.author.split(' ')[0] || t.author,
      username: t.author.includes('@') ? t.author.split('@')[1] : t.author,
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${t.author}`,
      text: t.content
    }))
  } catch (error) {
    console.error('加载留言失败:', error)
    // 从localStorage加载备用数据
    const saved = localStorage.getItem('testimonials')
    if (saved) {
      try {
        const parsed = JSON.parse(saved)
        allTestimonials.value = [...allTestimonials.value, ...parsed]
      } catch (e) {
        console.error('加载本地留言失败:', e)
      }
    }
  }
}

// 保存留言到localStorage
const saveTestimonials = () => {
  const userTestimonials = allTestimonials.value.filter(t => t.id > 6)
  if (userTestimonials.length > 0) {
    localStorage.setItem('testimonials', JSON.stringify(userTestimonials))
  }
}

// 提交评价
const submitTestimonial = async () => {
  if (!form.value.name || !form.value.username || !form.value.message) {
    return
  }

  submitting.value = true

  try {
    // 提交到后端
    const result = await testimonialApi.create({
      author: `${form.value.name} (@${form.value.username})`,
      content: form.value.message
    })

    // 添加到列表
    const newTestimonial = {
      id: result.id,
      name: form.value.name,
      username: form.value.username,
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${form.value.name}`,
      text: form.value.message
    }

    allTestimonials.value.unshift(newTestimonial)

    // 重置表单
    form.value = {
      name: '',
      username: '',
      message: ''
    }

    // 重新启动动画
    await nextTick()
    restartAnimation()
  } catch (error) {
    console.error('提交失败:', error)
    alert('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 头像加载错误处理
const handleAvatarError = (event) => {
  event.target.src = 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'
}

// 处理卡片悬停（带防抖和节流）
const handleCardHover = (event) => {
  const card = event.currentTarget
  const now = Date.now()
  
  // 如果正在处理悬停，直接忽略
  if (isProcessingHover) {
    return
  }
  
  // 如果是同一个卡片，直接返回
  if (hoveredCard === card) {
    return
  }
  
  // 节流：如果距离上次处理时间太短，延迟处理
  const timeSinceLastHover = now - lastHoverTime
  if (timeSinceLastHover < HOVER_THROTTLE && hoveredCard) {
    // 清除之前的延迟
    if (hoverTimeout) {
      clearTimeout(hoverTimeout)
      hoverTimeout = null
    }
    
    // 延迟处理，确保不会太快切换
    hoverTimeout = setTimeout(() => {
      handleCardHover(event)
    }, HOVER_THROTTLE - timeSinceLastHover)
    return
  }
  
  // 清除之前的延迟
  if (hoverTimeout) {
    clearTimeout(hoverTimeout)
    hoverTimeout = null
  }
  
  // 如果已经有卡片被悬停且不是当前卡片
  if (hoveredCard && hoveredCard !== card) {
    // 先恢复之前的卡片
    handleCardLeave({ currentTarget: hoveredCard }, true)
    // 增加延迟时间，确保前一个卡片完全恢复
    hoverTimeout = setTimeout(() => {
      if (!isProcessingHover) {
        lastHoverTime = Date.now()
        processCardHover(card)
      }
    }, 400) // 增加到400ms，确保恢复动画完全完成
    return
  }
  
  // 处理新卡片悬停（添加延迟，避免快速移动时的割裂）
  hoverTimeout = setTimeout(() => {
    if (!isProcessingHover && hoveredCard !== card) {
      lastHoverTime = Date.now()
      processCardHover(card)
    }
  }, 100) // 100ms延迟，平滑快速移动
}

// 实际处理卡片悬停
const processCardHover = (card) => {
  if (isProcessingHover) return
  
  isProcessingHover = true
  hoveredCard = card
  
  // 先暂停动画，但保持当前位置
  if (animationInstance1.value) {
    animationInstance1.value.pause()
  }
  if (animationInstance2.value) {
    animationInstance2.value.pause()
  }
  isPaused.value = true
  
  // 等待一帧确保动画已暂停，并且位置稳定
  requestAnimationFrame(() => {
    // 获取卡片当前的transform值
    const currentX = gsap.getProperty(card, 'x') || 0
    const currentY = gsap.getProperty(card, 'y') || 0
    const currentScale = gsap.getProperty(card, 'scale') || 1
    
    // 从当前位置开始放大，避免回退
    gsap.set(card, {
      x: currentX,
      y: currentY,
      scale: currentScale
    })
    
    // 放大当前卡片并添加阴影
    hoveredCardAnimation = gsap.to(card, {
      scale: 1.15,
      y: currentY - 15,
      duration: 0.4,
      ease: 'power2.out',
      onUpdate: function() {
        // 动态更新阴影
        const scale = this.targets()[0].style.transform.match(/scale\(([^)]+)\)/)?.[1] || 1
        const shadowIntensity = (scale - 1) * 20
        card.style.boxShadow = `0 ${10 + shadowIntensity}px ${30 + shadowIntensity}px rgba(0, 0, 0, ${0.2 + shadowIntensity / 100})`
        card.style.borderColor = 'var(--accent-primary)'
      },
      onComplete: () => {
        isProcessingHover = false
      }
    })
    
    // 提升卡片层级
    gsap.set(card, { zIndex: 50 })
  })
}

// 处理卡片离开
const handleCardLeave = (event, immediate = false) => {
  const card = event.currentTarget
  
  // 清除悬停延迟
  if (hoverTimeout) {
    clearTimeout(hoverTimeout)
    hoverTimeout = null
  }
  
  // 如果不是当前悬停的卡片，忽略
  if (hoveredCard !== card) {
    return
  }
  
  if (hoveredCard === card) {
    // 恢复卡片大小和样式
    if (hoveredCardAnimation) {
      hoveredCardAnimation.kill()
      hoveredCardAnimation = null
    }
    
    // 如果立即恢复（切换卡片时），使用更快的动画
    const duration = immediate ? 0.2 : 0.4
    
    // 获取当前transform值，确保从当前位置恢复
    const currentY = gsap.getProperty(card, 'y') || 0
    
    gsap.to(card, {
      scale: 1,
      y: 0, // 恢复到原始位置
      duration: duration,
      ease: 'power2.out',
      onUpdate: function() {
        // 动态恢复阴影
        const scale = this.targets()[0].style.transform.match(/scale\(([^)]+)\)/)?.[1] || 1
        const shadowIntensity = (scale - 1) * 20
        if (scale <= 1) {
          card.style.boxShadow = 'var(--shadow-sm)'
          card.style.borderColor = 'var(--border-color)'
        } else {
          card.style.boxShadow = `0 ${10 + shadowIntensity}px ${30 + shadowIntensity}px rgba(0, 0, 0, ${0.2 + shadowIntensity / 100})`
        }
      },
      onComplete: () => {
        // 恢复z-index
        gsap.set(card, { zIndex: 'auto' })
        card.style.boxShadow = 'var(--shadow-sm)'
        card.style.borderColor = 'var(--border-color)'
        
        // 如果不是立即恢复，等待动画完成后再恢复轮播
        if (!immediate) {
          requestAnimationFrame(() => {
            if (isPaused.value && !hoveredCard) {
              if (animationInstance1.value) {
                animationInstance1.value.resume()
              }
              if (animationInstance2.value) {
                animationInstance2.value.resume()
              }
              isPaused.value = false
            }
          })
        }
        
        isProcessingHover = false
      }
    })
    
    hoveredCard = null
  }
}

// 启动单排轮播动画
const startRowAnimation = (trackRef, testimonials, animationRef, delay = 0) => {
  if (!trackRef.value) return

  const cards = trackRef.value.querySelectorAll('.testimonial-card')
  if (cards.length === 0) return

  setTimeout(() => {
    const firstCard = cards[0]
    if (!firstCard) return
    
    const cardWidth = firstCard.offsetWidth || 420
    const gap = 24
    const cardTotalWidth = cardWidth + gap
    const totalWidth = testimonials.length * cardTotalWidth

    // 重置位置
    gsap.set(trackRef.value, { x: 0 })

    // 创建无限循环动画
    animationRef.value = gsap.to(trackRef.value, {
      x: -totalWidth,
      duration: testimonials.length * 4,
      ease: 'none',
      repeat: -1,
      onRepeat: () => {
        // 当第一组完全移出视野时，重置位置以实现无缝循环
        gsap.set(trackRef.value, { x: 0 })
      }
    })
  }, 100 + delay)
}

// 启动轮播动画
const startCarouselAnimation = () => {
  // 两排同时开始，第二排延迟2秒形成错位效果
  startRowAnimation(carouselTrackRef1, firstRowTestimonials.value, animationInstance1, 0)
  startRowAnimation(carouselTrackRef2, secondRowTestimonials.value, animationInstance2, 2000)
}

// 重新启动动画
const restartAnimation = () => {
  if (animationInstance1.value) {
    animationInstance1.value.kill()
  }
  if (animationInstance2.value) {
    animationInstance2.value.kill()
  }
  nextTick(() => {
    startCarouselAnimation()
  })
}

// 初始化动画
const initAnimations = async () => {
  await nextTick()
  
  // 表单动画
  if (formRef.value) {
    gsap.from(formRef.value, {
      opacity: 0,
      y: 30,
      duration: 0.8,
      ease: 'power2.out'
    })
  }

  // 等待DOM完全渲染后再启动轮播
  await nextTick()
  setTimeout(() => {
    startCarouselAnimation()
  }, 300)
}

// 监听留言数组变化
watch([firstRowTestimonials, secondRowTestimonials], () => {
  restartAnimation()
}, { deep: true })

onMounted(() => {
  loadTestimonials()
  initAnimations()
})

onUnmounted(() => {
  // 清除延迟
  if (hoverTimeout) {
    clearTimeout(hoverTimeout)
  }
  
  // 清理悬停状态
  if (hoveredCard) {
    handleCardLeave({ currentTarget: hoveredCard })
  }
  
  // 清理动画
  if (animationInstance1.value) {
    animationInstance1.value.kill()
  }
  if (animationInstance2.value) {
    animationInstance2.value.kill()
  }
  
  if (hoveredCardAnimation) {
    hoveredCardAnimation.kill()
  }
  
  ScrollTrigger.getAll().forEach(trigger => trigger.kill())
})
</script>

<style scoped>
.testimonials-page {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  padding: 8rem 0 4rem;
  background: var(--bg-primary);
}

.page-header {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 4rem;
  margin-bottom: 6rem;
  align-items: start;
}

.header-left {
  display: flex;
  align-items: center;
}

.section-indicator {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--accent-primary);
  flex-shrink: 0;
}

.indicator-label {
  font-size: 0.875rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.header-right {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: clamp(2.5rem, 6vw, 4rem);
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  margin: 0;
}

/* 表单部分 */
.testimonial-form-section {
  margin-bottom: 4rem;
}

.form-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  padding: 2rem;
  max-width: 500px;
  margin: 0 auto;
  box-shadow: var(--shadow-sm);
}

.form-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  text-align: center;
}

.testimonial-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.form-group input,
.form-group textarea {
  padding: 0.875rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background: var(--bg-primary);
  color: var(--text-primary);
  font-size: 1rem;
  font-family: inherit;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.submit-btn {
  padding: 1rem 2rem;
  background: var(--gradient-primary);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 0.5rem;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 留言轮播容器 */
.testimonials-carousel-wrapper {
  position: relative;
  width: 100%;
  overflow: hidden;
  margin-bottom: 3rem;
  padding: 2rem 0;
  min-height: 500px;
}

.carousel-fade-left,
.carousel-fade-right {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 150px;
  z-index: 10;
  pointer-events: none;
}

.carousel-fade-left {
  left: 0;
  background: linear-gradient(to right, var(--bg-primary), transparent);
}

.carousel-fade-right {
  right: 0;
  background: linear-gradient(to left, var(--bg-primary), transparent);
}

.carousel-track {
  display: flex;
  gap: 1.5rem;
  will-change: transform;
  margin-bottom: 1.5rem;
  position: relative;
  /* 确保轨道有正确的层级 */
  z-index: 1;
}

.carousel-track-row2 {
  margin-bottom: 0;
  z-index: 1;
}

.testimonial-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 1.5rem;
  position: relative;
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
  width: 420px;
  min-width: 420px;
  cursor: pointer;
  transform-origin: center center;
  will-change: transform;
  /* 确保放大时不影响其他卡片位置 */
  isolation: isolate;
  /* 确保层级管理 */
  z-index: 1;
  /* 使用backface-visibility优化性能 */
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
}

.testimonial-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.testimonial-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid var(--border-color);
  background: var(--bg-tertiary);
}

.testimonial-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.testimonial-info {
  flex: 1;
  min-width: 0;
}

.testimonial-name {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 0.25rem 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.testimonial-username {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.testimonial-platform {
  font-size: 1.25rem;
  color: var(--text-secondary);
  opacity: 0.7;
  flex-shrink: 0;
}

.testimonial-separator {
  height: 1px;
  background: var(--border-color);
  margin: 1rem 0;
  border: none;
  background-image: repeating-linear-gradient(
    to right,
    var(--border-color) 0,
    var(--border-color) 8px,
    transparent 8px,
    transparent 16px
  );
}

.testimonial-text {
  color: var(--text-primary);
  line-height: 1.6;
  margin: 0;
  font-size: 0.95rem;
}


@media (max-width: 968px) {
  .page-header {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .testimonial-card {
    width: 100%;
    min-width: 100%;
  }

  .form-card {
    padding: 2rem;
  }
}

@media (max-width: 768px) {
  .testimonials-page {
    padding: 6rem 0 3rem;
  }

  .page-header {
    margin-bottom: 4rem;
  }

  .testimonial-form-section {
    margin-bottom: 4rem;
  }

  .form-card {
    padding: 1.5rem;
  }

  .testimonial-card {
    width: 350px;
    min-width: 350px;
  }

  .carousel-fade-left,
  .carousel-fade-right {
    width: 80px;
  }
}
</style>

