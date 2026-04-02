<template>
  <div class="knowledge-base">
    <div class="kb-header">
      <h1>知识库</h1>
      <div class="kb-search">
        <input 
          v-model="searchQuery" 
          @input="handleSearch"
          type="text" 
          placeholder="搜索文章、标签... (Ctrl+K)"
        />
        <span class="search-icon">🔍</span>
      </div>
    </div>

    <div class="kb-layout">
      <aside class="kb-sidebar">
        <div class="sidebar-section">
          <h3>领域分类</h3>
          <div class="domain-tree">
            <div 
              v-for="domain in domains" 
              :key="domain.id" 
              class="domain-item"
            >
              <div 
                class="domain-header"
                @click="toggleDomain(domain.id)"
              >
                <span class="folder-icon">{{ expandedDomains.includes(domain.id) ? '📂' : '📁' }}</span>
                <span class="domain-name">{{ domain.name }}</span>
              </div>
              <div 
                v-if="expandedDomains.includes(domain.id)"
                class="category-list"
              >
                <div 
                  v-for="cat in domain.categories" 
                  :key="cat.id"
                  class="category-item"
                  :class="{ active: selectedCategory === cat.id }"
                  @click="selectCategory(cat.id)"
                >
                  <span class="doc-icon">📄</span>
                  <span>{{ cat.name }}</span>
                  <span class="count">({{ cat.articleCount }})</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="sidebar-section">
          <h3>标签</h3>
          <div class="tag-cloud">
            <span 
              v-for="tag in allTags" 
              :key="tag"
              class="tag-item"
              :class="{ active: selectedTags.includes(tag) }"
              @click="toggleTag(tag)"
            >
              #{{ tag }}
            </span>
          </div>
        </div>

        <div class="sidebar-section" v-if="isAuthenticated">
          <h3>收藏夹</h3>
          <div class="favorites">
            <div class="favorite-item">⭐ 我的收藏</div>
          </div>
        </div>
      </aside>

      <main class="kb-main">
        <div class="kb-toolbar">
          <div class="view-toggle">
            <button 
              :class="{ active: viewMode === 'grid' }"
              @click="viewMode = 'grid'"
            >
              <span class="icon">▦</span> 网格
            </button>
            <button 
              :class="{ active: viewMode === 'list' }"
              @click="viewMode = 'list'"
            >
              <span class="icon">☰</span> 列表
            </button>
            <button 
              :class="{ active: viewMode === 'gallery' }"
              @click="viewMode = 'gallery'"
            >
              <span class="icon">⊞</span> 画廊
            </button>
          </div>
          <div class="sort-select">
            <select v-model="sortBy">
              <option value="latest">最新发布</option>
              <option value="popular">最多阅读</option>
              <option value="updated">最近更新</option>
            </select>
          </div>
        </div>

        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="articles.length === 0" class="empty-state">
          <div class="empty-icon">📚</div>
          <p>暂无文章</p>
          <p class="hint">点击左上角的分类开始浏览，或发表第一篇文章</p>
        </div>

        <div v-else :class="['article-container', viewMode]">
          <div 
            v-for="article in articles" 
            :key="article.id"
            class="article-card"
            @click="goToArticle(article.slug)"
          >
            <div class="card-cover" v-if="article.coverImage">
              <img :src="article.coverImage" :alt="article.title" />
            </div>
            <div class="card-content">
              <div class="card-category" v-if="article.category">
                {{ article.category.name }}
              </div>
              <h3 class="card-title">
                <span v-html="article._highlightedTitle || article.title"></span>
                <span class="featured-badge" v-if="article.isFeatured">⭐</span>
              </h3>
              <p class="card-summary" v-html="article._highlightedSummary || article.summary"></p>
              <div class="card-tags" v-if="article.tags && article.tags.length">
                <span 
                  v-for="tag in article.tags.slice(0, 3)" 
                  :key="tag"
                  class="tag"
                >
                  #{{ tag }}
                </span>
              </div>
              <div class="card-meta">
                <span class="views">👁️ {{ article.viewCount }}</span>
                <span class="likes">❤️ {{ article.likeCount }}</span>
                <span class="read-time">📖 {{ article.readTime }}分钟</span>
              </div>
            </div>
            <div class="card-overlay">
              <div class="overlay-content">
                <h4 v-html="article._highlightedTitle || article.title"></h4>
                <p v-html="article._highlightedSummary || article.summary"></p>
                <span class="read-more">阅读全文 →</span>
              </div>
            </div>
          </div>
        </div>

        <div class="kb-pagination" v-if="totalPages > 1">
          <button 
            :disabled="currentPage === 0"
            @click="changePage(currentPage - 1)"
          >
            上一页
          </button>
          <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
          <button 
            :disabled="currentPage >= totalPages - 1"
            @click="changePage(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { knowledgeApi, knowledgeCategoryApi, knowledgeDomainApi } from '../utils/api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isAuthenticated = computed(() => authStore.isAuthenticated)

const articles = ref([])
const domains = ref([])
const allTags = ref([])
const loading = ref(false)
const searchQuery = ref('')
const viewMode = ref('grid')
const sortBy = ref('latest')
const currentPage = ref(0)
const pageSize = ref(12)
const totalPages = ref(0)

const selectedCategory = ref(null)
const selectedTags = ref([])
const expandedDomains = ref([])

const fetchArticles = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      published: true
    }
    
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    if (selectedTags.value.length) {
      params.tags = selectedTags.value.join(',')
    }
    if (searchQuery.value) {
      const result = await knowledgeApi.searchArticles(searchQuery.value, currentPage.value, pageSize.value)
      articles.value = result.content || []
      totalPages.value = result.totalPages || 1
    } else {
      const result = await knowledgeApi.getArticles(params)
      articles.value = result.content || []
      totalPages.value = result.totalPages || 1
    }
  } catch (error) {
    console.error('获取文章失败:', error)
    articles.value = []
  } finally {
    loading.value = false
  }
}

const fetchDomains = async () => {
  try {
    const result = await knowledgeDomainApi.getAll()
    domains.value = result || []
    if (domains.value.length > 0) {
      expandedDomains.value = [domains.value[0].id]
    }
  } catch (error) {
    console.error('获取领域失败:', error)
    domains.value = []
  }
}

const fetchCategories = async () => {
  try {
    const result = await knowledgeCategoryApi.getAll()
    const categories = result || []
    
    domains.value = domains.value.map(domain => ({
      ...domain,
      categories: categories.filter(c => c.domainId === domain.id)
    }))
    
    domains.value = domains.value.map(domain => ({
      ...domain,
      categories: domain.categories.map(cat => ({
        ...cat,
        articleCount: cat.articleCount || 0
      }))
    }))
    
    const tagSet = new Set()
    categories.forEach(cat => {
      if (cat.tags) {
        cat.tags.forEach(tag => tagSet.add(tag))
      }
    })
    allTags.value = Array.from(tagSet)
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const toggleDomain = (domainId) => {
  const index = expandedDomains.value.indexOf(domainId)
  if (index > -1) {
    expandedDomains.value.splice(index, 1)
  } else {
    expandedDomains.value.push(domainId)
  }
}

const selectCategory = (categoryId) => {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
  currentPage.value = 0
  fetchArticles()
}

const toggleTag = (tag) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
  currentPage.value = 0
  fetchArticles()
}

const handleSearch = () => {
  currentPage.value = 0
  fetchArticles().then(() => {
    highlightSearchResults()
  })
}

const changePage = (page) => {
  currentPage.value = page
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToArticle = (slug) => {
  router.push(`/knowledge/${slug}`)
}

const highlightText = (text, query) => {
  if (!query || !text) return text
  const regex = new RegExp(`(${query})`, 'gi')
  return text.replace(regex, '<mark>$1</mark>')
}

const highlightSearchResults = () => {
  articles.value.forEach(article => {
    article._highlightedTitle = highlightText(article.title, searchQuery.value)
    article._highlightedSummary = highlightText(article.summary, searchQuery.value)
  })
}

const handleKeydown = (e) => {
  if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
    e.preventDefault()
    document.querySelector('.kb-search input')?.focus()
  }
}

onMounted(async () => {
  await Promise.all([fetchArticles(), fetchDomains(), fetchCategories()])
  highlightSearchResults()
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.knowledge-base {
  min-height: 100vh;
  background: #f8f9fa;
}

.kb-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 60px;
  color: white;
}

.kb-header h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.kb-search {
  position: relative;
  max-width: 600px;
}

.kb-search input {
  width: 100%;
  padding: 16px 24px 16px 50px;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.kb-search input::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.kb-search input:focus {
  background: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  outline: none;
}

.kb-search .search-icon {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.2rem;
}

.kb-layout {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px;
  gap: 30px;
}

.kb-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.sidebar-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.sidebar-section h3 {
  font-size: 0.9rem;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.domain-tree {
  font-size: 0.95rem;
}

.domain-header {
  display: flex;
  align-items: center;
  padding: 10px 8px;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}

.domain-header:hover {
  background: #f5f5f5;
}

.folder-icon {
  margin-right: 8px;
  font-size: 1.1rem;
}

.domain-name {
  font-weight: 600;
  color: #333;
}

.category-list {
  margin-left: 20px;
  padding-left: 12px;
  border-left: 2px solid #eee;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 0.9rem;
  color: #666;
}

.category-item:hover {
  background: #f5f5f5;
  color: #333;
}

.category-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.category-item .count {
  margin-left: auto;
  font-size: 0.8rem;
  opacity: 0.7;
}

.doc-icon {
  margin-right: 8px;
  font-size: 0.9rem;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 0.85rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-item:hover {
  background: #e8e8e8;
}

.tag-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.kb-main {
  flex: 1;
  min-width: 0;
}

.kb-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.view-toggle {
  display: flex;
  gap: 8px;
}

.view-toggle button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border: none;
  background: #f5f5f5;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  color: #666;
  transition: all 0.2s;
}

.view-toggle button:hover {
  background: #e8e8e8;
}

.view-toggle button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.sort-select select {
  padding: 10px 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #666;
  cursor: pointer;
  background: white;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #999;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 80px 40px;
  background: white;
  border-radius: 16px;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 8px;
}

.empty-state .hint {
  font-size: 0.9rem;
  color: #999;
}

.article-container {
  display: grid;
  gap: 24px;
}

.article-container.grid {
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
}

.article-container.list {
  grid-template-columns: 1fr;
}

.article-container.gallery {
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
}

.article-container.gallery .article-card {
  min-height: 380px;
}

.article-container.gallery .card-cover {
  height: 200px;
}

.article-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.article-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.article-card:hover .card-title::after {
  transform: scaleX(1);
}

.article-card:hover .card-overlay {
  opacity: 1;
}

.card-cover {
  height: 180px;
  overflow: hidden;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-card:hover .card-cover img {
  transform: scale(1.05);
}

.card-content {
  padding: 20px;
}

.card-category {
  font-size: 0.75rem;
  color: #667eea;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
  position: relative;
  display: inline-block;
}

.card-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.featured-badge {
  margin-left: 8px;
}

.card-summary {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-summary :deep(mark),
.card-title :deep(mark) {
  background: #ffeb3b;
  color: #333;
  padding: 0 2px;
  border-radius: 2px;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.card-tags .tag {
  padding: 4px 10px;
  background: #f5f5f5;
  border-radius: 12px;
  font-size: 0.75rem;
  color: #666;
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 0.8rem;
  color: #999;
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.95) 0%, rgba(118, 75, 162, 0.95));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  padding: 24px;
}

.overlay-content {
  text-align: center;
  color: white;
}

.overlay-content h4 {
  font-size: 1.3rem;
  margin-bottom: 12px;
}

.overlay-content p {
  font-size: 0.9rem;
  opacity: 0.9;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more {
  font-weight: 600;
  font-size: 0.9rem;
}

.kb-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 40px;
  padding: 20px;
  background: white;
  border-radius: 12px;
}

.kb-pagination button {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.kb-pagination button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.kb-pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 0.9rem;
}

@media (max-width: 1024px) {
  .kb-layout {
    flex-direction: column;
  }
  
  .kb-sidebar {
    width: 100%;
  }
  
  .domain-tree {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .domain-header {
    background: white;
    padding: 12px 16px;
    border-radius: 8px;
  }
  
  .category-list {
    display: none;
  }
}

@media (max-width: 768px) {
  .kb-header {
    padding: 30px 20px;
  }
  
  .kb-header h1 {
    font-size: 1.8rem;
  }
  
  .kb-layout {
    padding: 16px;
  }
  
  .kb-toolbar {
    flex-direction: column;
    gap: 12px;
  }
  
  .view-toggle {
    width: 100%;
    justify-content: center;
  }
}
</style>