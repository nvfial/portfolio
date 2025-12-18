<template>
  <section class="contact">
    <ParticlesBackground id="contact-particles" />
    <div class="container">
      <h2 class="section-title">联系我</h2>
      <p class="section-subtitle">有项目想法或想聊聊？我很乐意听到你的消息！</p>
      
      <div class="contact-content">
        <!-- 联系信息卡片 -->
        <div class="contact-info">
          <div class="info-card" v-for="info in contactInfo" :key="info.type">
            <div class="info-icon">{{ info.icon }}</div>
            <h3>{{ info.title }}</h3>
            <p>{{ info.value }}</p>
            <a v-if="info.link" :href="info.link" target="_blank" rel="noopener" class="info-link">
              查看详情 →
            </a>
          </div>
          
          <!-- 社交链接 -->
          <div class="social-links">
            <h3>关注我</h3>
            <div class="social-grid">
              <a 
                v-for="social in socialLinks" 
                :key="social.name"
                :href="social.url" 
                target="_blank"
                rel="noopener"
                class="social-link"
                :style="{ '--social-color': social.color }"
              >
                <span class="social-icon">{{ social.icon }}</span>
                <span class="social-name">{{ social.name }}</span>
              </a>
            </div>
          </div>
        </div>
        
        <!-- 联系表单 -->
        <form class="contact-form" @submit.prevent="handleSubmit" ref="formRef">
          <div class="form-group">
            <label for="name">姓名 *</label>
            <input 
              id="name"
              v-model="form.name" 
              type="text" 
              placeholder="你的名字"
              :class="{ error: errors.name }"
              @blur="validateField('name')"
            />
            <span v-if="errors.name" class="error-message">{{ errors.name }}</span>
          </div>
          
          <div class="form-group">
            <label for="email">邮箱 *</label>
            <input 
              id="email"
              v-model="form.email" 
              type="email" 
              placeholder="your.email@example.com"
              :class="{ error: errors.email }"
              @blur="validateField('email')"
            />
            <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
          </div>
          
          <div class="form-group">
            <label for="subject">主题</label>
            <input 
              id="subject"
              v-model="form.subject" 
              type="text" 
              placeholder="项目合作 / 咨询 / 其他"
            />
          </div>
          
          <div class="form-group">
            <label for="message">消息 *</label>
            <textarea 
              id="message"
              v-model="form.message" 
              rows="6" 
              placeholder="告诉我你的想法..."
              :class="{ error: errors.message }"
              @blur="validateField('message')"
            ></textarea>
            <span v-if="errors.message" class="error-message">{{ errors.message }}</span>
          </div>
          
          <button type="submit" class="btn btn-submit" :disabled="isSubmitting">
            <span v-if="!isSubmitting">发送消息</span>
            <span v-else class="loading">
              <span class="spinner"></span>
              发送中...
            </span>
          </button>
          
          <div v-if="submitStatus" class="submit-status" :class="submitStatus.type">
            {{ submitStatus.message }}
          </div>
        </form>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import ParticlesBackground from '../components/ParticlesBackground.vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const formRef = ref(null)
const isSubmitting = ref(false)
const submitStatus = ref(null)

const form = reactive({
  name: '',
  email: '',
  subject: '',
  message: ''
})

const errors = reactive({
  name: '',
  email: '',
  message: ''
})

const contactInfo = [
  {
    type: 'email',
    icon: '📧',
    title: '邮箱',
    value: 'zhangxiaoming@example.com',
    link: 'mailto:zhangxiaoming@example.com'
  },
  {
    type: 'phone',
    icon: '📱',
    title: '电话',
    value: '+86 138-0000-0000',
    link: 'tel:+8613800000000'
  },
  {
    type: 'location',
    icon: '📍',
    title: '位置',
    value: '中国，北京',
    link: null
  }
]

const socialLinks = [
  { name: 'GitHub', icon: '💻', url: 'https://github.com', color: '#24292e' },
  { name: 'LinkedIn', icon: '💼', url: 'https://linkedin.com', color: '#0077b5' },
  { name: 'Twitter', icon: '🐦', url: 'https://twitter.com', color: '#1da1f2' },
  { name: 'Dribbble', icon: '🎨', url: 'https://dribbble.com', color: '#ea4c89' }
]

const validateField = (field) => {
  errors[field] = ''
  
  switch (field) {
    case 'name':
      if (!form.name.trim()) {
        errors.name = '姓名不能为空'
      } else if (form.name.trim().length < 2) {
        errors.name = '姓名至少需要2个字符'
      }
      break
    case 'email':
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!form.email.trim()) {
        errors.email = '邮箱不能为空'
      } else if (!emailRegex.test(form.email)) {
        errors.email = '请输入有效的邮箱地址'
      }
      break
    case 'message':
      if (!form.message.trim()) {
        errors.message = '消息不能为空'
      } else if (form.message.trim().length < 10) {
        errors.message = '消息至少需要10个字符'
      }
      break
  }
}

const validateForm = () => {
  validateField('name')
  validateField('email')
  validateField('message')
  
  return !errors.name && !errors.email && !errors.message
}

import { contactApi } from '../utils/api'

const handleSubmit = async () => {
  if (!validateForm()) {
    submitStatus.value = {
      type: 'error',
      message: '请检查表单并修正错误'
    }
    setTimeout(() => {
      submitStatus.value = null
    }, 3000)
    return
  }
  
  isSubmitting.value = true
  submitStatus.value = null
  
  try {
    // 提交到后端
    await contactApi.create({
      name: form.name,
      email: form.email,
      subject: form.subject,
      message: form.message
    })
    
    submitStatus.value = {
      type: 'success',
      message: `感谢 ${form.name}！你的消息已成功发送，我会尽快回复你。`
    }
    
    // 重置表单
    Object.assign(form, {
      name: '',
      email: '',
      subject: '',
      message: ''
    })
    
    Object.assign(errors, {
      name: '',
      email: '',
      message: ''
    })
    
    setTimeout(() => {
      submitStatus.value = null
    }, 5000)
  } catch (error) {
    console.error('提交失败:', error)
    submitStatus.value = {
      type: 'error',
      message: '发送失败，请稍后重试'
    }
    setTimeout(() => {
      submitStatus.value = null
    }, 3000)
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  gsap.from('.info-card', {
    opacity: 0,
    y: 30,
    duration: 0.8,
    stagger: 0.1,
    scrollTrigger: {
      trigger: '.contact-info',
      start: 'top 80%'
    }
  })
  
  gsap.from('.contact-form', {
    opacity: 0,
    x: 50,
    duration: 0.8,
    scrollTrigger: {
      trigger: '.contact-form',
      start: 'top 80%'
    }
  })
})
</script>

<style scoped>
.contact {
  position: relative;
  z-index: 1;
  padding: 8rem 0;
  background: var(--bg-primary);
  min-height: 100vh;
}

.section-subtitle {
  text-align: center;
  color: var(--text-secondary);
  font-size: 1.1rem;
  margin-bottom: 4rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.contact-content {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 4rem;
  max-width: 1200px;
  margin: 0 auto;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.info-card {
  background: var(--bg-secondary);
  padding: 2rem;
  border-radius: 16px;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.info-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  display: block;
}

.info-card h3 {
  font-size: 1.25rem;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.info-card p {
  color: var(--text-secondary);
  margin-bottom: 1rem;
}

.info-link {
  color: var(--accent-primary);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
  display: inline-block;
}

.info-link:hover {
  transform: translateX(5px);
}

.social-links {
  background: var(--bg-secondary);
  padding: 2rem;
  border-radius: 16px;
  border: 1px solid var(--border-color);
}

.social-links h3 {
  font-size: 1.25rem;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.social-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.social-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: var(--bg-tertiary);
  border-radius: 12px;
  text-decoration: none;
  color: var(--text-primary);
  transition: all 0.3s;
  border: 2px solid transparent;
}

.social-link:hover {
  border-color: var(--social-color, var(--accent-primary));
  transform: translateY(-3px);
  box-shadow: var(--shadow-sm);
}

.social-icon {
  font-size: 1.5rem;
}

.social-name {
  font-weight: 500;
}

.contact-form {
  background: var(--bg-secondary);
  padding: 2.5rem;
  border-radius: 16px;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-md);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
  font-weight: 500;
  font-size: 0.95rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 2px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-primary);
  color: var(--text-primary);
  font-family: inherit;
  font-size: 1rem;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group input.error,
.form-group textarea.error {
  border-color: #ef4444;
}

.error-message {
  display: block;
  color: #ef4444;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.btn-submit {
  width: 100%;
  margin-top: 1rem;
  padding: 1rem 2rem;
  font-size: 1rem;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.submit-status {
  margin-top: 1rem;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
}

.submit-status.success {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
  border: 1px solid rgba(34, 197, 94, 0.3);
}

.submit-status.error {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

@media (max-width: 968px) {
  .contact-content {
    grid-template-columns: 1fr;
    gap: 3rem;
  }
  
  .social-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .contact {
    padding: 5rem 0;
  }
  
  .contact-form {
    padding: 1.5rem;
  }
}
</style>
