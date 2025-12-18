<template>
  <div class="admin-messages">
    <div class="messages-header">
      <h1>消息管理</h1>
      <div class="tabs">
        <button 
          :class="{ active: activeTab === 'contacts' }"
          @click="activeTab = 'contacts'"
        >
          联系消息 ({{ contacts.length }})
        </button>
        <button 
          :class="{ active: activeTab === 'testimonials' }"
          @click="activeTab = 'testimonials'"
        >
          推荐信 ({{ testimonials.length }})
        </button>
      </div>
    </div>

    <!-- 联系消息列表 -->
    <div v-if="activeTab === 'contacts'" class="messages-list">
      <div v-for="contact in contacts" :key="contact.id" class="message-card">
        <div class="message-header">
          <div>
            <h3>{{ contact.name }}</h3>
            <p class="email">{{ contact.email }}</p>
          </div>
          <div class="message-actions">
            <span class="time">{{ formatDate(contact.createdAt) }}</span>
            <button @click="deleteContact(contact.id)" class="btn-delete">删除</button>
          </div>
        </div>
        <div v-if="contact.subject" class="subject">
          <strong>主题：</strong>{{ contact.subject }}
        </div>
        <div class="message-content">
          {{ contact.message }}
        </div>
      </div>
    </div>

    <!-- 推荐信列表 -->
    <div v-if="activeTab === 'testimonials'" class="messages-list">
      <div v-for="testimonial in testimonials" :key="testimonial.id" class="message-card">
        <div class="message-header">
          <div>
            <h3>{{ testimonial.author }}</h3>
          </div>
          <div class="message-actions">
            <span class="time">{{ formatDate(testimonial.createdAt) }}</span>
            <button @click="deleteTestimonial(testimonial.id)" class="btn-delete">删除</button>
          </div>
        </div>
        <div class="message-content">
          {{ testimonial.content }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { contactApi, testimonialApi } from '../../utils/api'

const activeTab = ref('contacts')
const contacts = ref([])
const testimonials = ref([])

const loadContacts = async () => {
  try {
    contacts.value = await contactApi.getAll()
  } catch (error) {
    console.error('加载联系消息失败:', error)
  }
}

const loadTestimonials = async () => {
  try {
    testimonials.value = await testimonialApi.getAll()
  } catch (error) {
    console.error('加载推荐信失败:', error)
  }
}

const deleteContact = async (id) => {
  if (confirm('确定要删除这条消息吗？')) {
    try {
      await contactApi.delete(id)
      await loadContacts()
    } catch (error) {
      alert('删除失败')
    }
  }
}

const deleteTestimonial = async (id) => {
  if (confirm('确定要删除这条推荐信吗？')) {
    try {
      await testimonialApi.delete(id)
      await loadTestimonials()
    } catch (error) {
      alert('删除失败')
    }
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadContacts()
  loadTestimonials()
})
</script>

<style scoped>
.admin-messages {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.messages-header {
  margin-bottom: 2rem;
}

.messages-header h1 {
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.tabs {
  display: flex;
  gap: 1rem;
}

.tabs button {
  padding: 0.5rem 1.5rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  cursor: pointer;
  color: var(--text-secondary);
}

.tabs button.active {
  background: var(--accent-primary);
  color: white;
  border-color: var(--accent-primary);
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message-card {
  background: var(--bg-secondary);
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: var(--shadow-sm);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 1rem;
}

.message-header h3 {
  margin: 0 0 0.25rem 0;
  color: var(--text-primary);
}

.email {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.message-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.time {
  color: var(--text-secondary);
  font-size: 0.85rem;
}

.btn-delete {
  padding: 0.25rem 0.75rem;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 0.25rem;
  cursor: pointer;
  font-size: 0.85rem;
}

.subject {
  margin-bottom: 1rem;
  color: var(--text-secondary);
}

.message-content {
  color: var(--text-primary);
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>






