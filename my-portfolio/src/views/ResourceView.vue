<template>
  <div class="resource-page" ref="pageRef">
    <div class="resource-bg">
      <div class="bg-texture"></div>
      <div class="bg-particles"></div>
    </div>
    
    <div class="resource-container">
      <div class="art-divider top-divider">
        <div class="divider-line"></div>
        <div class="divider-ornament left">◆</div>
        <div class="divider-ornament right">◆</div>
      </div>
      
      <div class="page-header">
        <h1 class="page-title">
          <span class="title-char" v-for="(char, i) in '资源共享'" :key="i">{{ char }}</span>
        </h1>
        <p class="page-subtitle">探索 · 下载 · 分享</p>
      </div>
      
      <div class="resource-layout">
        <aside class="sidebar">
          <div class="category-card">
            <h3 class="category-title">资源分类</h3>
            <div class="category-list">
              <div 
                class="category-item"
                :class="{ active: !selectedCategory && !selectedType }"
                @click="selectCategory(null, null)"
              >
                <span class="category-icon">📁</span>
                <span class="category-name">全部资源</span>
                <span class="category-count">{{ totalCount }}</span>
              </div>
              <div 
                v-for="cat in categories" 
                :key="cat.id"
                class="category-item"
                :class="{ active: selectedCategory === cat.id }"
                @click="selectCategory(cat.id, null)"
              >
                <span class="category-icon">{{ cat.icon || '📂' }}</span>
                <span class="category-name">{{ cat.name }}</span>
                <span class="category-count">{{ cat.resourceCount || 0 }}</span>
              </div>
            </div>
            
            <div class="type-filters">
              <h4 class="filter-title">文件类型</h4>
              <div class="type-chips">
                <span 
                  v-for="t in types" 
                  :key="t.value"
                  class="type-chip"
                  :class="{ active: selectedType === t.value }"
                  @click="selectCategory(null, t.value)"
                >
                  {{ t.label }}
                </span>
              </div>
            </div>
          </div>
        </aside>
        
        <main class="main-content">
          <div class="content-header">
            <div class="search-box">
              <input 
                v-model="searchKeyword" 
                type="text" 
                placeholder="搜索资源..."
                @keyup.enter="doSearch"
              />
              <button class="search-btn" @click="doSearch">🔍</button>
            </div>
            <div class="sort-options">
              <select v-model="sortBy" @change="loadResources">
                <option value="newest">最新上传</option>
                <option value="popular">最多下载</option>
                <option value="name">名称排序</option>
              </select>
            </div>
          </div>
          
          <div class="resources-grid" v-if="resources.length > 0">
            <div 
              v-for="resource in resources" 
              :key="resource.id"
              class="resource-card"
              @click="goToDetail(resource.id)"
            >
              <div class="card-thumbnail">
                <img 
                  :src="resource.thumbnailUrl || '/images/resource-default.jpg'" 
                  :alt="resource.name"
                />
                <div class="type-badge">{{ getTypeLabel(resource.type) }}</div>
                <div class="card-overlay">
                  <span class="preview-hint">点击查看详情</span>
                </div>
              </div>
              <div class="card-info">
                <h4 class="card-name">{{ resource.name }}</h4>
                <p class="card-meta">
                  <span class="meta-size">{{ formatSize(resource.fileSize) }}</span>
                  <span class="meta-type">{{ resource.fileType }}</span>
                </p>
                <div class="card-stats">
                  <span>📥 {{ resource.downloadCount || 0 }}</span>
                  <span>👁️ {{ resource.viewCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="empty-state" v-else>
            <div class="empty-icon">📭</div>
            <p>暂无资源</p>
          </div>
          
          <div class="pagination" v-if="totalPages > 1">
            <button 
              class="page-btn" 
              :disabled="currentPage === 0"
              @click="changePage(currentPage - 1)"
            >
              上一页
            </button>
            <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
            <button 
              class="page-btn"
              :disabled="currentPage >= totalPages - 1"
              @click="changePage(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { resourceApi } from '../utils/api'
import { gsap } from 'gsap'

const router = useRouter()
const pageRef = ref(null)
const resources = ref([])
const categories = ref([])
const selectedCategory = ref(null)
const selectedType = ref(null)
const searchKeyword = ref('')
const sortBy = ref('newest')
const currentPage = ref(0)
const pageSize = ref(12)
const totalElements = ref(0)

const types = [
  { value: 'AUDIO', label: '🎵 音频' },
  { value: 'VIDEO', label: '🎬 视频' },
  { value: 'DOCUMENT', label: '📄 文档' },
  { value: 'IMAGE', label: '🖼️ 图片' },
  { value: 'ARCHIVE', label: '📦 压缩包' },
  { value: 'SOFTWARE', label: '💿 软件' }
]

const totalCount = computed(() => totalElements.value)
const totalPages = computed(() => Math.ceil(totalElements.value / pageSize.value))

const loadResources = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      type: selectedType.value,
      categoryId: selectedCategory.value,
      keyword: searchKeyword.value || undefined
    }
    const data = await resourceApi.getAll(params)
    resources.value = data.content
    totalElements.value = data.totalElements
  } catch (error) {
    console.error('Failed to load resources:', error)
  }
}

const loadCategories = async () => {
  try {
    const data = await resourceApi.getCategories()
    categories.value = data
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

const selectCategory = (catId, type) => {
  selectedCategory.value = catId
  selectedType.value = type
  currentPage.value = 0
  loadResources()
}

const doSearch = () => {
  currentPage.value = 0
  loadResources()
}

const changePage = (page) => {
  currentPage.value = page
  loadResources()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToDetail = (id) => {
  router.push(`/resources/${id}`)
}

const getTypeLabel = (type) => {
  const t = types.find(x => x.value === type)
  return t ? t.label.split(' ')[0] : type
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
  loadCategories()
  loadResources()
  
  setTimeout(() => {
    gsap.from('.title-char', {
      opacity: 0,
      y: 20,
      stagger: 0.05,
      duration: 0.5,
      ease: 'power2.out'
    })
    gsap.from('.category-card', {
      opacity: 0,
      x: -30,
      duration: 0.6,
      delay: 0.3
    })
    gsap.from('.resource-card', {
      opacity: 0,
      y: 20,
      stagger: 0.05,
      duration: 0.4,
      delay: 0.5
    })
  }, 100)
})
</script>

<style scoped>
.resource-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: #0a0a0f;
}

.resource-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.bg-texture {
  position: absolute;
  inset: 0;
  background: 
    repeating-linear-gradient(90deg, transparent, transparent 100px, rgba(255,255,255,0.01) 100px, rgba(255,255,255,0.01) 101px),
    repeating-linear-gradient(0deg, transparent, transparent 100px, rgba(255,255,255,0.01) 100px, rgba(255,255,255,0.01) 101px);
}

.bg-particles {
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(2px 2px at 50px 100px, rgba(201, 169, 106, 0.3), transparent),
    radial-gradient(2px 2px at 150px 200px, rgba(201, 169, 106, 0.2), transparent),
    radial-gradient(1px 1px at 200px 50px, rgba(201, 169, 106, 0.2), transparent);
  background-size: 300px 300px;
  animation: twinkle 6s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

.resource-container {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.art-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
}

.divider-line {
  width: 200px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(201, 169, 106, 0.5), transparent);
}

.divider-ornament {
  color: rgba(201, 169, 106, 0.6);
  font-size: 0.8rem;
  margin: 0 1rem;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 300;
  letter-spacing: 0.5em;
  color: #c9a96a;
  margin-bottom: 0.5rem;
}

.title-char {
  display: inline-block;
}

.page-subtitle {
  color: rgba(201, 169, 106, 0.5);
  font-size: 0.9rem;
  letter-spacing: 0.3em;
}

.resource-layout {
  display: flex;
  gap: 2rem;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.category-card {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 12px;
  padding: 1.5rem;
  position: sticky;
  top: 2rem;
}

.category-title {
  font-size: 1rem;
  color: #c9a96a;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid rgba(201, 169, 106, 0.2);
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: rgba(255, 255, 255, 0.7);
}

.category-item:hover {
  background: rgba(201, 169, 106, 0.1);
}

.category-item.active {
  background: rgba(201, 169, 106, 0.2);
  color: #c9a96a;
}

.category-icon {
  font-size: 1.2rem;
}

.category-name {
  flex: 1;
  font-size: 0.9rem;
}

.category-count {
  font-size: 0.8rem;
  color: rgba(201, 169, 106, 0.5);
}

.type-filters {
  border-top: 1px solid rgba(201, 169, 106, 0.2);
  padding-top: 1rem;
}

.filter-title {
  font-size: 0.85rem;
  color: rgba(201, 169, 106, 0.7);
  margin-bottom: 0.75rem;
}

.type-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.type-chip {
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  font-size: 0.75rem;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s;
}

.type-chip:hover {
  background: rgba(201, 169, 106, 0.1);
}

.type-chip.active {
  background: rgba(201, 169, 106, 0.3);
  color: #c9a96a;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.content-header {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-box {
  flex: 1;
  display: flex;
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 8px;
  overflow: hidden;
}

.search-box input {
  flex: 1;
  padding: 0.75rem 1rem;
  background: transparent;
  border: none;
  color: #fff;
  font-size: 0.9rem;
}

.search-box input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.search-box input:focus {
  outline: none;
}

.search-btn {
  padding: 0 1rem;
  background: transparent;
  border: none;
  color: rgba(201, 169, 106, 0.6);
  cursor: pointer;
}

.sort-options select {
  padding: 0.75rem 1rem;
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem;
}

.resource-card {
  background: rgba(20, 20, 30, 0.8);
  border: 1px solid rgba(201, 169, 106, 0.2);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.resource-card:hover {
  transform: translateY(-4px);
  border-color: rgba(201, 169, 106, 0.4);
  box-shadow: 0 8px 30px rgba(201, 169, 106, 0.1);
}

.card-thumbnail {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.card-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.resource-card:hover .card-thumbnail img {
  transform: scale(1.05);
}

.type-badge {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  padding: 0.3rem 0.6rem;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 4px;
  font-size: 0.7rem;
  color: #c9a96a;
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.resource-card:hover .card-overlay {
  opacity: 1;
}

.preview-hint {
  padding: 0.5rem 1rem;
  background: rgba(201, 169, 106, 0.9);
  border-radius: 20px;
  font-size: 0.8rem;
  color: #0a0a0f;
}

.card-info {
  padding: 1rem;
}

.card-name {
  font-size: 0.95rem;
  color: #fff;
  margin-bottom: 0.5rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-meta {
  display: flex;
  gap: 0.75rem;
  font-size: 0.8rem;
  color: rgba(201, 169, 106, 0.6);
  margin-bottom: 0.5rem;
}

.card-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.4);
}

.empty-state {
  text-align: center;
  padding: 4rem;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state p {
  color: rgba(255, 255, 255, 0.5);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-btn {
  padding: 0.5rem 1.5rem;
  background: rgba(201, 169, 106, 0.1);
  border: 1px solid rgba(201, 169, 106, 0.3);
  border-radius: 8px;
  color: #c9a96a;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background: rgba(201, 169, 106, 0.2);
}

.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-info {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .resource-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .category-card {
    position: static;
  }
  
  .page-title {
    font-size: 1.8rem;
    letter-spacing: 0.3em;
  }
}
</style>