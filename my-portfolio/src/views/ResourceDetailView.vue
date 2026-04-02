<template>
  <div class="resource-detail-page" ref="pageRef" v-if="resource">
    <div class="detail-hero" :class="resource.type.toLowerCase()">
      <div class="hero-bg">
        <img 
          v-if="resource.thumbnailUrl" 
          :src="resource.thumbnailUrl" 
          class="bg-image" 
          alt=""
        />
        <div class="bg-overlay"></div>
      </div>
      
      <div class="hero-content">
        <div class="back-indicator" @click="goBack">
          <span class="back-arrow">←</span>
          <span class="back-text">返回</span>
        </div>
        
        <div class="resource-type-badge">{{ getTypeLabel(resource.type) }}</div>
        
        <h1 class="resource-title">{{ resource.name }}</h1>
        
        <div class="resource-meta">
          <span class="meta-item">
            <span class="meta-icon">📦</span>
            {{ formatSize(resource.fileSize) }}
          </span>
          <span class="meta-item">
            <span class="meta-icon">📎</span>
            {{ resource.fileType }}
          </span>
          <span class="meta-item">
            <span class="meta-icon">📥</span>
            {{ resource.downloadCount || 0 }} 次下载
          </span>
          <span class="meta-item">
            <span class="meta-icon">👁️</span>
            {{ resource.viewCount || 0 }} 次查看
          </span>
        </div>
      </div>
    </div>
    
    <div class="detail-container">
      <div class="detail-main">
        <div class="preview-section" v-if="resource.previewUrl">
          <div class="preview-card">
            <div v-if="isImage" class="image-preview">
              <img :src="resource.previewUrl" :alt="resource.name" />
            </div>
            <div v-else-if="isVideo" class="video-preview">
              <video :src="resource.previewUrl" controls></video>
            </div>
            <div v-else-if="isAudio" class="audio-preview">
              <audio :src="resource.previewUrl" controls></audio>
            </div>
            <div v-else class="file-preview">
              <div class="file-icon">📄</div>
              <p>暂不支持预览</p>
            </div>
          </div>
        </div>
        
        <div class="info-section">
          <div class="info-card">
            <h3 class="section-title">资源描述</h3>
            <p class="description" v-if="resource.description">{{ resource.description }}</p>
            <p class="no-desc" v-else>暂无描述信息</p>
          </div>
          
          <div class="info-card" v-if="resource.categoryName">
            <h3 class="section-title">所属分类</h3>
            <span class="category-tag">{{ resource.categoryName }}</span>
          </div>
        </div>
      </div>
      
      <aside class="detail-sidebar">
        <div class="download-card">
          <h3 class="download-title">下载资源</h3>
          
          <div class="download-info">
            <div class="info-row">
              <span class="info-label">文件名</span>
              <span class="info-value">{{ resource.name }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">文件大小</span>
              <span class="info-value">{{ formatSize(resource.fileSize) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">文件格式</span>
              <span class="info-value">{{ resource.fileType }}</span>
            </div>
          </div>
          
          <button class="download-btn" @click="handleDownload">
            <span class="btn-icon">📥</span>
            <span class="btn-text">立即下载</span>
          </button>
          
          <p class="download-hint">登录后可下载</p>
        </div>
        
        <div class="stats-card">
          <h3 class="stats-title">资源统计</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-value">{{ resource.downloadCount || 0 }}</span>
              <span class="stat-label">下载次数</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ resource.viewCount || 0 }}</span>
              <span class="stat-label">浏览次数</span>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
  
  <div class="loading-page" v-else>
    <div class="loading-spinner"></div>
    <p>加载中...</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { resourceApi } from '../utils/api'
import { gsap } from 'gsap'

const router = useRouter()
const route = useRoute()
const pageRef = ref(null)
const resource = ref(null)

const isImage = computed(() => {
  if (!resource.value?.fileType) return false
  return ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'].includes(resource.value.fileType.toLowerCase())
})

const isVideo = computed(() => {
  if (!resource.value?.fileType) return false
  return ['mp4', 'webm', 'mkv', 'avi'].includes(resource.value.fileType.toLowerCase())
})

const isAudio = computed(() => {
  if (!resource.value?.fileType) return false
  return ['mp3', 'wav', 'flac', 'aac', 'ogg', 'm4a'].includes(resource.value.fileType.toLowerCase())
})

const loadResource = async () => {
  try {
    const id = route.params.id
    resource.value = await resourceApi.getById(id)
    
    setTimeout(() => {
      initAnimations()
    }, 100)
  } catch (error) {
    console.error('Failed to load resource:', error)
    router.push('/resources')
  }
}

const initAnimations = () => {
  gsap.from('.hero-content', {
    opacity: 0,
    y: 30,
    duration: 0.8,
    ease: 'power2.out'
  })
  
  gsap.from('.detail-main', {
    opacity: 0,
    y: 20,
    duration: 0.6,
    delay: 0.3
  })
  
  gsap.from('.detail-sidebar', {
    opacity: 0,
    x: 20,
    duration: 0.6,
    delay: 0.4
  })
}

const goBack = () => {
  router.push('/resources')
}

const handleDownload = async () => {
  try {
    const isLoggedIn = localStorage.getItem('token')
    if (!isLoggedIn) {
      alert('请先登录后再下载')
      router.push('/login')
      return
    }
    
    const data = await resourceApi.getDownloadUrl(resource.value.id)
    window.open(data.downloadUrl, '_blank')
    
    await resourceApi.recordDownload(resource.value.id)
  } catch (error) {
    console.error('Download failed:', error)
    alert('下载失败，请稍后重试')
  }
}

const getTypeLabel = (type) => {
  const types = {
    AUDIO: '🎵 音频',
    VIDEO: '🎬 视频',
    DOCUMENT: '📄 文档',
    IMAGE: '🖼️ 图片',
    ARCHIVE: '📦 压缩包',
    SOFTWARE: '💿 软件',
    OTHER: '📋 其他'
  }
  return types[type] || type
}

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  while (bytes >= 1024 && i < units.length - 1) {
    bytes /= 1024
    i++
  }
  return bytes.toFixed(1) + ' ' + units[i]
}

onMounted(() => {
  loadResource()
})
</script>

<style scoped>
.resource-detail-page {
  min-height: 100vh;
  background: #0a0a0f;
}

.detail-hero {
  position: relative;
  height: 60vh;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.4) saturate(0.8);
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(10, 10, 15, 0.3) 0%,
    rgba(10, 10, 15, 0.7) 50%,
    rgba(10, 10, 15, 1) 100%
  );
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 2rem;
  max-width: 800px;
}

.back-indicator {
  position: absolute;
  top: 2rem;
  left: 2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 8px;
  transition: all 0.3s;
}

.back-indicator:hover {
  background: rgba(201, 169, 106, 0.1);
  border-color: rgba(201, 169, 106, 0.4);
}

.back-arrow {
  color: #c9a96a;
}

.back-text {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.resource-type-badge {
  display: inline-block;
  padding: 0.5rem 1rem;
  background: rgba(201, 169, 106, 0.2);
  border: 1px solid rgba(201, 169, 106, 0.4);
  border-radius: 20px;
  color: #c9a96a;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.resource-title {
  font-size: 2.5rem;
  color: #fff;
  font-weight: 300;
  letter-spacing: 0.2em;
  margin-bottom: 1.5rem;
}

.resource-meta {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1.5rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
}

.meta-icon {
  color: #c9a96a;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 2rem;
}

.detail-main {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.preview-section {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 12px;
  overflow: hidden;
}

.preview-card {
  padding: 1rem;
}

.image-preview img {
  width: 100%;
  border-radius: 8px;
}

.video-preview video,
.audio-preview audio {
  width: 100%;
  border-radius: 8px;
}

.file-preview {
  padding: 3rem;
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
}

.file-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.info-card {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
}

.section-title {
  font-size: 1rem;
  color: #c9a96a;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid rgba(201, 169, 106, 0.2);
}

.description {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.8;
}

.no-desc {
  color: rgba(255, 255, 255, 0.4);
  font-style: italic;
}

.category-tag {
  display: inline-block;
  padding: 0.4rem 1rem;
  background: rgba(201, 169, 106, 0.1);
  border-radius: 20px;
  color: #c9a96a;
  font-size: 0.9rem;
}

.detail-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.download-card {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
}

.download-title {
  font-size: 1rem;
  color: #c9a96a;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid rgba(201, 169, 106, 0.2);
}

.download-info {
  margin-bottom: 1.5rem;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.info-label {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.85rem;
}

.info-value {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.85rem;
}

.download-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #c9a96a, #8b7355);
  border: none;
  border-radius: 8px;
  color: #0a0a0f;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s;
}

.download-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(201, 169, 106, 0.3);
}

.btn-icon {
  font-size: 1.2rem;
}

.download-hint {
  text-align: center;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 0.75rem;
}

.stats-card {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
}

.stats-title {
  font-size: 1rem;
  color: #c9a96a;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid rgba(201, 169, 106, 0.2);
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.stat-item {
  text-align: center;
  padding: 1rem;
  background: rgba(201, 169, 106, 0.05);
  border-radius: 8px;
}

.stat-value {
  display: block;
  font-size: 1.5rem;
  color: #c9a96a;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.5);
}

.loading-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c9a96a;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(201, 169, 106, 0.2);
  border-top-color: #c9a96a;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 900px) {
  .detail-container {
    grid-template-columns: 1fr;
  }
  
  .detail-sidebar {
    order: -1;
  }
  
  .resource-title {
    font-size: 1.8rem;
  }
}
</style>