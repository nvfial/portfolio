<template>
  <div class="media-manager">
    <div class="media-header">
      <h2>媒体管理</h2>
      <div class="header-actions">
        <div class="search-box">
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索媒体文件..."
            @input="handleSearch"
          />
        </div>
        <div class="filter-tabs">
          <button 
            v-for="type in mediaTypes" 
            :key="type.value"
            @click="activeType = type.value"
            :class="['filter-btn', { active: activeType === type.value }]"
          >
            {{ type.label }}
          </button>
        </div>
        <label class="upload-btn">
          <input type="file" ref="fileInput" @change="handleUpload" accept="image/*,video/*" multiple hidden />
          <span>+ 上传文件</span>
        </label>
      </div>
    </div>

    <div class="media-stats">
      <div class="stat-item">
        <span class="stat-value">{{ stats.images }}</span>
        <span class="stat-label">图片</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.videos }}</span>
        <span class="stat-label">视频</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.total }}</span>
        <span class="stat-label">总文件数</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ formatSize(stats.storage) }}</span>
        <span class="stat-label">存储使用</span>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    
    <div v-else-if="filteredMedia.length === 0" class="empty-state">
      <div class="empty-icon">📁</div>
      <p>暂无媒体文件</p>
      <p class="empty-hint">点击上方"上传文件"按钮添加</p>
    </div>

    <div v-else class="media-grid" :class="viewMode">
      <div 
        v-for="item in filteredMedia" 
        :key="item.id" 
        class="media-item"
        @click="selectMedia(item)"
      >
        <div class="media-preview">
          <img v-if="item.type === 'image'" :src="item.url" :alt="item.name" />
          <video v-else-if="item.type === 'video'" :src="item.url"></video>
          <div v-else class="file-icon">{{ getFileIcon(item.name) }}</div>
          <div class="media-overlay">
            <button @click.stop="previewMedia(item)" class="preview-btn">预览</button>
            <button @click.stop="deleteMedia(item)" class="delete-btn">删除</button>
          </div>
        </div>
        <div class="media-info">
          <div class="media-name" :title="item.name">{{ item.name }}</div>
          <div class="media-meta">
            <span>{{ formatSize(item.size) }}</span>
            <span>{{ formatDate(item.uploadDate) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showPreview" class="preview-modal" @click="showPreview = false">
      <div class="preview-content" @click.stop>
        <button class="close-btn" @click="showPreview = false">×</button>
        <img v-if="previewItem?.type === 'image'" :src="previewItem.url" :alt="previewItem.name" />
        <video v-else-if="previewItem?.type === 'video'" :src="previewItem.url" controls></video>
        <div class="preview-info">
          <h3>{{ previewItem?.name }}</h3>
          <p>{{ formatSize(previewItem?.size) }} · {{ formatDate(previewItem?.uploadDate) }}</p>
          <div class="preview-actions">
            <a :href="previewItem?.url" download class="download-btn">下载</a>
            <button @click="copyUrl(previewItem?.url)" class="copy-btn">复制链接</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { mediaApi } from '../../utils/api'

const mediaItems = ref([])
const loading = ref(true)
const searchQuery = ref('')
const activeType = ref('all')
const viewMode = ref('grid')
const showPreview = ref(false)
const previewItem = ref(null)
const fileInput = ref(null)

const mediaTypes = [
  { value: 'all', label: '全部' },
  { value: 'image', label: '图片' },
  { value: 'video', label: '视频' }
]

const stats = computed(() => {
  const images = mediaItems.value.filter(m => m.type === 'image').length
  const videos = mediaItems.value.filter(m => m.type === 'video').length
  const total = mediaItems.value.length
  const storage = mediaItems.value.reduce((sum, m) => sum + (m.size || 0), 0)
  return { images, videos, total, storage }
})

const filteredMedia = computed(() => {
  let result = [...mediaItems.value]
  
  if (activeType.value !== 'all') {
    result = result.filter(m => m.type === activeType.value)
  }
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(m => m.name.toLowerCase().includes(query))
  }
  
  return result
})

const loadMedia = async () => {
  loading.value = true
  try {
    const data = await mediaApi.getMediaList()
    mediaItems.value = data.content || []
  } catch (error) {
    console.error('加载媒体文件失败:', error)
    mediaItems.value = []
  } finally {
    loading.value = false
  }
}

const handleUpload = async (event) => {
  const files = event.target.files
  if (!files || files.length === 0) return

  for (const file of files) {
    try {
      const formData = new FormData()
      formData.append('file', file)
      const result = await mediaApi.uploadMedia(formData)
      mediaItems.value.unshift(result)
    } catch (error) {
      console.error('上传文件失败:', error)
    }
  }
  
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const deleteMedia = async (item) => {
  if (!confirm(`确定要删除 "${item.name}" 吗？`)) return
  
  try {
    await mediaApi.deleteMedia(item.id)
    mediaItems.value = mediaItems.value.filter(m => m.id !== item.id)
  } catch (error) {
    console.error('删除文件失败:', error)
  }
}

const selectMedia = (item) => {
  // 可用于多选等场景
}

const previewMedia = (item) => {
  previewItem.value = item
  showPreview.value = true
}

const handleSearch = () => {
  // 搜索在 computed 中自动处理
}

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let size = bytes
  let unitIndex = 0
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024
    unitIndex++
  }
  return `${size.toFixed(1)} ${units[unitIndex]}`
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const getFileIcon = (filename) => {
  const ext = filename.split('.').pop()?.toLowerCase()
  const icons = {
    pdf: '📄',
    doc: '📝',
    docx: '📝',
    txt: '📃',
    zip: '📦',
    rar: '📦'
  }
  return icons[ext] || '📁'
}

const copyUrl = async (url) => {
  try {
    await navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
  }
}

onMounted(() => {
  loadMedia()
})
</script>

<style scoped>
.media-manager {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.media-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.media-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-box input {
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  width: 200px;
  font-size: 0.95rem;
}

.search-box input:focus {
  outline: none;
  border-color: #667eea;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.filter-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-btn.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.upload-btn {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s;
  display: inline-block;
}

.upload-btn:hover {
  background: #5568d3;
  transform: translateY(-2px);
}

.media-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  background: white;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.stat-value {
  display: block;
  font-size: 1.8rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 4px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

.loading, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.empty-hint {
  font-size: 0.9rem;
  margin-top: 8px;
}

.media-grid {
  display: grid;
  gap: 20px;
}

.media-grid.grid {
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
}

.media-grid.list {
  grid-template-columns: 1fr;
}

.media-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.media-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.media-preview {
  position: relative;
  aspect-ratio: 16/10;
  background: #f5f5f5;
  overflow: hidden;
}

.media-preview img,
.media-preview video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: 3rem;
}

.media-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  opacity: 0;
  transition: opacity 0.3s;
}

.media-item:hover .media-overlay {
  opacity: 1;
}

.preview-btn, .delete-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s;
}

.preview-btn {
  background: #667eea;
  color: white;
}

.preview-btn:hover {
  background: #5568d3;
}

.delete-btn {
  background: #fee;
  color: #e83e8c;
}

.delete-btn:hover {
  background: #fdd;
}

.media-info {
  padding: 12px;
}

.media-name {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.media-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #999;
}

.preview-modal {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.preview-content {
  max-width: 90vw;
  max-height: 90vh;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.preview-content img,
.preview-content video {
  max-width: 100%;
  max-height: 70vh;
  display: block;
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(0,0,0,0.5);
  color: white;
  font-size: 1.5rem;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-info {
  padding: 20px;
}

.preview-info h3 {
  margin: 0 0 8px;
  color: #333;
}

.preview-info p {
  color: #666;
  margin: 0 0 16px;
}

.preview-actions {
  display: flex;
  gap: 12px;
}

.download-btn, .copy-btn {
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  text-decoration: none;
  transition: all 0.3s;
}

.download-btn {
  background: #667eea;
  color: white;
  border: none;
}

.copy-btn {
  background: white;
  border: 1px solid #667eea;
  color: #667eea;
}

.download-btn:hover {
  background: #5568d3;
}

.copy-btn:hover {
  background: #f0f0ff;
}
</style>
