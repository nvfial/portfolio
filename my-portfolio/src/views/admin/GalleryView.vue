<template>
  <div class="gallery-page">
    <div class="page-header">
      <h1>作品集管理</h1>
      <button @click="showUploadModal = true" class="btn btn-primary">
        上传作品
      </button>
    </div>

    <div class="gallery-grid">
      <div v-for="item in galleryItems" :key="item.id" class="gallery-item">
        <div class="item-preview">
          <img v-if="item.type === 'image'" :src="item.url" :alt="item.title" />
          <div v-else class="video-preview">
            <video :src="item.url" controls></video>
          </div>
        </div>
        <div class="item-info">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
          <div class="item-tags">
            <span v-for="tag in item.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
        <div class="item-actions">
          <button @click="editItem(item)" class="btn btn-sm">编辑</button>
          <button @click="deleteItem(item)" class="btn btn-sm btn-danger">删除</button>
        </div>
      </div>
    </div>

    <div v-if="showUploadModal" class="modal-overlay" @click="showUploadModal = false">
      <div class="modal" @click.stop>
        <h2>上传作品</h2>
        <form @submit.prevent="uploadItem">
          <div class="form-group">
            <label>标题</label>
            <input v-model="newItem.title" type="text" required />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="newItem.description"></textarea>
          </div>
          <div class="form-group">
            <label>类型</label>
            <select v-model="newItem.type">
              <option value="image">图片</option>
              <option value="video">视频</option>
            </select>
          </div>
          <div class="form-group">
            <label>URL</label>
            <input v-model="newItem.url" type="url" required />
          </div>
          <div class="form-group">
            <label>标签 (逗号分隔)</label>
            <input v-model="tagsInput" type="text" placeholder="前端, Vue, 作品" />
          </div>
          <div class="modal-actions">
            <button type="button" @click="showUploadModal = false" class="btn">取消</button>
            <button type="submit" class="btn btn-primary">上传</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const galleryItems = ref([
  { id: 1, title: '项目一', description: '一个很棒的项目', type: 'image', url: 'https://picsum.photos/400/300', tags: ['Vue', '前端'] },
  { id: 2, title: '项目二', description: '另一个项目', type: 'image', url: 'https://picsum.photos/400/301', tags: ['React'] }
])

const showUploadModal = ref(false)
const newItem = ref({ title: '', description: '', type: 'image', url: '' })
const tagsInput = ref('')

const uploadItem = () => {
  const item = {
    ...newItem.value,
    tags: tagsInput.value.split(',').map(t => t.trim()).filter(t => t)
  }
  galleryItems.value.push({ ...item, id: Date.now() })
  showUploadModal.value = false
  newItem.value = { title: '', description: '', type: 'image', url: '' }
  tagsInput.value = ''
}

const editItem = (item) => {
  console.log('Edit:', item)
}

const deleteItem = (item) => {
  if (confirm('确定要删除这个作品吗？')) {
    galleryItems.value = galleryItems.value.filter(i => i.id !== item.id)
  }
}
</script>

<style scoped>
.gallery-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 1.8rem;
  color: var(--text-primary);
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.gallery-item {
  background: var(--bg-secondary);
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.gallery-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.item-preview img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.item-info {
  padding: 1rem;
}

.item-info h3 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  color: var(--text-primary);
}

.item-info p {
  margin: 0 0 0.75rem;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.item-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  padding: 0.25rem 0.5rem;
  background: var(--primary);
  color: white;
  border-radius: 4px;
  font-size: 0.8rem;
}

.item-actions {
  padding: 1rem;
  display: flex;
  gap: 0.5rem;
  border-top: 1px solid var(--border);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--bg-secondary);
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
}

.modal h2 {
  margin: 0 0 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: opacity 0.2s;
}

.btn:hover {
  opacity: 0.8;
}

.btn-primary {
  background: var(--primary);
  color: white;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
}

.btn-danger {
  background: #dc3545;
  color: white;
}
</style>