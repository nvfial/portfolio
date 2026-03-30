<template>
  <div class="guestbook-page">
    <ParticleField :particle-count="70" :connection-distance="70" />
    
    <header class="page-header">
      <div class="container">
        <h1 class="page-title">留言板</h1>
        <p class="page-subtitle">留下你的足迹，分享你的想法</p>
      </div>
    </header>
    
    <div class="container">
      <div class="guestbook-layout">
        <div class="guestbook-form">
          <h2>发表留言</h2>
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label for="name">昵称 *</label>
              <input 
                id="name"
                v-model="form.name" 
                type="text" 
                placeholder="请输入你的昵称"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="email">邮箱（可选）</label>
              <input 
                id="email"
                v-model="form.email" 
                type="email" 
                placeholder="your@email.com"
              />
            </div>
            
            <div class="form-group">
              <label for="website">网站（可选）</label>
              <input 
                id="website"
                v-model="form.website" 
                type="url" 
                placeholder="https://yourwebsite.com"
              />
            </div>
            
            <div class="form-group">
              <label for="message">留言内容 *</label>
              <textarea 
                id="message"
                v-model="form.message" 
                rows="5"
                placeholder="写下你的想法..."
                required
              ></textarea>
            </div>
            
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '提交中...' : '发表留言' }}
            </button>
          </form>
        </div>
        
        <div class="guestbook-messages">
          <div class="messages-header">
            <h2>全部留言</h2>
            <span class="message-count">{{ messages.length }} 条留言</span>
          </div>
          
          <div class="messages-filter">
            <button 
              class="filter-btn"
              :class="{ active: sortBy === 'newest' }"
              @click="sortBy = 'newest'"
            >
              最新
            </button>
            <button 
              class="filter-btn"
              :class="{ active: sortBy === 'oldest' }"
              @click="sortBy = 'oldest'"
            >
              最早
            </button>
          </div>
          
          <div class="messages-list">
            <div 
              v-for="message in sortedMessages" 
              :key="message.id"
              class="message-card"
            >
              <div class="message-avatar">
                {{ getInitials(message.name) }}
              </div>
              
              <div class="message-content">
                <div class="message-header">
                  <span class="message-author">{{ message.name }}</span>
                  <span class="message-time">{{ formatTime(message.createdAt) }}</span>
                </div>
                
                <p class="message-text">{{ message.message }}</p>
                
                <div class="message-footer">
                  <button class="action-btn" @click="handleReply(message)">
                    💬 回复
                  </button>
                  <button class="action-btn" @click="handleLike(message)">
                    {{ message.liked ? '❤️' : '🤍' }} {{ message.likes }}
                  </button>
                </div>
                
                <div v-if="message.replies?.length" class="message-replies">
                  <div 
                    v-for="reply in message.replies" 
                    :key="reply.id"
                    class="reply-item"
                  >
                    <span class="reply-author">{{ reply.name }}:</span>
                    <span class="reply-text">{{ reply.message }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-if="messages.length === 0" class="empty-messages">
              <p>暂无留言，快来发表第一条留言吧！</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ParticleField from '~/components/art/ParticleField.vue'

const form = ref({
  name: '',
  email: '',
  website: '',
  message: ''
})

const submitting = ref(false)
const sortBy = ref('newest')

const messages = ref([
  {
    id: 1,
    name: '前端开发者',
    email: 'dev@example.com',
    message: '太棒了！这个知识库系统设计得非常精美，尤其是动态粒子效果。期待更多内容！',
    likes: 12,
    liked: false,
    createdAt: '2025-01-20T10:30:00',
    replies: [
      { id: 101, name: '站长', message: '感谢支持！会持续更新更多内容~' }
    ]
  },
  {
    id: 2,
    name: '设计师小王',
    message: '艺术展示部分真的很惊艳，尤其是3D节点网络，让人印象深刻！',
    likes: 8,
    liked: true,
    createdAt: '2025-01-18T15:20:00',
    replies: []
  },
  {
    id: 3,
    name: '技术爱好者',
    message: '想请教一下这个粒子效果是用什么技术实现的？Three.js吗？',
    likes: 5,
    liked: false,
    createdAt: '2025-01-15T09:45:00',
    replies: [
      { id: 102, name: '站长', message: '是的，用的是Three.js配合自定义着色器实现的效果' }
    ]
  }
])

const sortedMessages = computed(() => {
  const sorted = [...messages.value]
  if (sortBy.value === 'newest') {
    return sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  }
  return sorted.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
})

const handleSubmit = async () => {
  if (!form.value.name || !form.value.message) return
  
  submitting.value = true
  
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  messages.value.unshift({
    id: Date.now(),
    name: form.value.name,
    email: form.value.email,
    message: form.value.message,
    likes: 0,
    liked: false,
    createdAt: new Date().toISOString(),
    replies: []
  })
  
  form.value = { name: '', email: '', website: '', message: '' }
  submitting.value = false
}

const handleLike = (message) => {
  message.liked = !message.liked
  message.likes += message.liked ? 1 : -1
}

const handleReply = (message) => {
  console.log('Reply to:', message)
}

const getInitials = (name) => {
  return name.charAt(0).toUpperCase()
}

const formatTime = (date) => {
  const d = new Date(date)
  const now = new Date()
  const diff = now - d
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}
</script>

<style scoped>
.guestbook-page {
  position: relative;
  min-height: 100vh;
  padding-top: 80px;
}

.page-header {
  text-align: center;
  padding: 4rem 0;
}

.page-title {
  font-size: clamp(2.5rem, 6vw, 4rem);
  font-weight: 700;
  background: var(--gradient-cosmic);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 1rem;
}

.page-subtitle {
  font-size: 1.25rem;
  color: var(--text-secondary);
}

.guestbook-layout {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 3rem;
  padding: 2rem 0 4rem;
}

.guestbook-form {
  position: sticky;
  top: 100px;
  height: fit-content;
  padding: 2rem;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.guestbook-form h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  color: var(--text-primary);
  font-size: 1rem;
  transition: all var(--transition-fast);
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

.messages-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.messages-header h2 {
  font-size: 1.5rem;
}

.message-count {
  font-size: 0.9rem;
  color: var(--text-tertiary);
}

.messages-filter {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.filter-btn {
  padding: 0.5rem 1rem;
  border-radius: var(--radius-full);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  font-size: 0.85rem;
  transition: all var(--transition-fast);
}

.filter-btn.active {
  background: var(--accent-primary);
  border-color: var(--accent-primary);
  color: white;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.message-card {
  display: flex;
  gap: 1rem;
  padding: 1.5rem;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.message-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 1.25rem;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.message-author {
  font-weight: 600;
  color: var(--text-primary);
}

.message-time {
  font-size: 0.85rem;
  color: var(--text-tertiary);
}

.message-text {
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 1rem;
}

.message-footer {
  display: flex;
  gap: 1rem;
}

.action-btn {
  font-size: 0.85rem;
  color: var(--text-tertiary);
  transition: color var(--transition-fast);
}

.action-btn:hover {
  color: var(--accent-primary);
}

.message-replies {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.reply-item {
  padding: 0.75rem;
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.reply-author {
  font-weight: 600;
  color: var(--accent-primary);
  margin-right: 0.5rem;
}

.reply-text {
  color: var(--text-secondary);
}

.empty-messages {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--text-tertiary);
}

@media (max-width: 1024px) {
  .guestbook-layout {
    grid-template-columns: 1fr;
  }
  
  .guestbook-form {
    position: static;
  }
}
</style>
