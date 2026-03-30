<template>
  <div class="knowledge-page">
    <ParticleField :particle-count="80" :connection-distance="80" />
    
    <header class="page-header">
      <div class="container">
        <h1 class="page-title">知识库</h1>
        <p class="page-subtitle">在知识的海洋中探索与发现</p>
      </div>
    </header>
    
    <div class="container">
      <div class="knowledge-layout">
        <aside class="sidebar">
          <DomainTree 
            :domains="domains" 
            @select-domain="handleDomainSelect"
            @select-category="handleCategorySelect"
          />
        </aside>
        
        <main class="main-content">
          <div v-if="!selectedDomain" class="domains-overview">
            <h2>选择知识领域开始探索</h2>
            <NodeNetwork 
              :data="graphData" 
              :show-labels="true"
              @node-click="handleGraphNodeClick"
            />
          </div>
          
          <div v-else-if="!selectedCategory" class="category-view">
            <div class="category-header">
              <NuxtLink to="/knowledge" class="back-link">← 返回知识库</NuxtLink>
              <h2>{{ selectedDomain.name }}</h2>
              <p>{{ selectedDomain.description }}</p>
            </div>
            
            <CategoryGrid 
              :categories="selectedDomain.children || []"
              @select="handleCategorySelect"
            />
          </div>
          
          <div v-else class="articles-view">
            <div class="articles-header">
              <NuxtLink :to="`/knowledge/${selectedDomain.slug}`" class="back-link">
                ← {{ selectedDomain.name }}
              </NuxtLink>
              <h2>{{ selectedCategory.name }}</h2>
              <p>{{ selectedCategory.description }}</p>
            </div>
            
            <div class="articles-list">
              <div 
                v-for="article in articles" 
                :key="article.id"
                class="article-card"
                @click="openArticle(article)"
              >
                <div class="article-cover" v-if="article.coverImage">
                  <img :src="article.coverImage" :alt="article.title" />
                </div>
                <div class="article-info">
                  <h3>{{ article.title }}</h3>
                  <p>{{ article.summary }}</p>
                  <div class="article-meta">
                    <span>{{ article.viewCount }} 阅读</span>
                    <span>{{ formatDate(article.createdAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ParticleField from '~/components/art/ParticleField.vue'
import DomainTree from '~/components/knowledge/DomainTree.vue'
import CategoryGrid from '~/components/knowledge/CategoryGrid.vue'
import NodeNetwork from '~/components/graph/NodeNetwork.vue'

const selectedDomain = ref(null)
const selectedCategory = ref(null)
const articles = ref([])

const domains = ref([
  {
    id: 1,
    name: '前端开发',
    slug: 'frontend',
    icon: '⚛️',
    description: '探索现代前端技术的无限可能',
    color: '#667eea',
    children: [
      { id: 11, name: 'Vue.js', icon: '💚', articleCount: 15, description: '渐进式JavaScript框架' },
      { id: 12, name: 'React', icon: '⚛️', articleCount: 12, description: '用于构建用户界面的JavaScript库' },
      { id: 13, name: 'TypeScript', icon: '🔷', articleCount: 8, description: 'JavaScript的超集' },
      { id: 14, name: 'CSS动画', icon: '🎨', articleCount: 7, description: '让界面动起来' }
    ]
  },
  {
    id: 2,
    name: '后端架构',
    slug: 'backend',
    icon: '🚀',
    description: '构建高性能的后端系统',
    color: '#764ba2',
    children: [
      { id: 21, name: 'Spring Boot', icon: '🍃', articleCount: 10, description: '简化Spring应用开发' },
      { id: 22, name: 'Node.js', icon: '🟢', articleCount: 9, description: 'JavaScript运行时' },
      { id: 23, name: '微服务', icon: '🏗️', articleCount: 6, description: '分布式系统架构' }
    ]
  },
  {
    id: 3,
    name: '人工智能',
    slug: 'ai',
    icon: '🤖',
    description: '探索AI的边界与可能',
    color: '#f093fb',
    children: [
      { id: 31, name: '机器学习', icon: '📊', articleCount: 8, description: '从数据中学习' },
      { id: 32, name: 'LLM应用', icon: '💬', articleCount: 5, description: '大语言模型实践' }
    ]
  },
  {
    id: 4,
    name: '设计艺术',
    slug: 'design',
    icon: '🎭',
    description: '视觉设计与创意表达',
    color: '#4facfe',
    children: [
      { id: 41, name: 'UI设计', icon: '🎯', articleCount: 6, description: '用户界面设计原则' },
      { id: 42, name: '3D艺术', icon: '🎲', articleCount: 4, description: 'Three.js创意应用' }
    ]
  }
])

const graphData = computed(() => {
  const nodes = []
  const links = []
  
  domains.value.forEach((domain, di) => {
    nodes.push({
      id: `domain-${domain.id}`,
      label: domain.name,
      type: 'domain',
      x: Math.cos(di * Math.PI / 2) * 200,
      y: Math.sin(di * Math.PI / 2) * 200
    })
    
    domain.children?.forEach((cat, ci) => {
      nodes.push({
        id: `cat-${cat.id}`,
        label: cat.name,
        type: 'category',
        x: Math.cos(di * Math.PI / 2) * 200 + Math.cos((di * Math.PI / 2) + (ci - 2) * 0.3) * 80,
        y: Math.sin(di * Math.PI / 2) * 200 + Math.sin((di * Math.PI / 2) + (ci - 2) * 0.3) * 80
      })
      
      links.push({
        source: `domain-${domain.id}`,
        target: `cat-${cat.id}`
      })
    })
  })
  
  return { nodes, links }
})

const handleDomainSelect = (domain) => {
  selectedDomain.value = domain
  selectedCategory.value = null
}

const handleCategorySelect = (category, domain) => {
  if (domain) selectedDomain.value = domain
  selectedCategory.value = category
  articles.value = [
    {
      id: 1,
      title: `${category.name} 入门指南`,
      summary: '深入了解' + category.name + '的核心概念与实践',
      coverImage: null,
      viewCount: 120,
      createdAt: '2025-01-15'
    },
    {
      id: 2,
      title: `${category.name} 进阶技巧`,
      summary: '掌握更高级的' + category.name + '技术',
      coverImage: null,
      viewCount: 85,
      createdAt: '2025-01-10'
    }
  ]
}

const handleGraphNodeClick = (node) => {
  if (node.type === 'domain') {
    const domain = domains.value.find(d => d.name === node.label)
    if (domain) handleDomainSelect(domain)
  }
}

const openArticle = (article) => {
  console.log('Open article:', article)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}
</script>

<style scoped>
.knowledge-page {
  position: relative;
  min-height: 100vh;
  padding-top: 80px;
}

.page-header {
  text-align: center;
  padding: 4rem 0;
  position: relative;
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

.knowledge-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 2rem;
  padding: 2rem 0 4rem;
}

.sidebar {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.main-content {
  min-height: 600px;
}

.domains-overview {
  text-align: center;
}

.domains-overview h2 {
  font-size: 1.5rem;
  color: var(--text-secondary);
  margin-bottom: 2rem;
}

.category-view,
.articles-view {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.category-header,
.articles-header {
  margin-bottom: 2rem;
}

.back-link {
  display: inline-block;
  color: var(--accent-primary);
  font-size: 0.9rem;
  margin-bottom: 1rem;
  transition: opacity var(--transition-fast);
}

.back-link:hover {
  opacity: 0.8;
}

.category-header h2,
.articles-header h2 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.category-header p,
.articles-header p {
  color: var(--text-secondary);
}

.articles-list {
  display: grid;
  gap: 1.5rem;
}

.article-card {
  display: flex;
  gap: 1.5rem;
  padding: 1.5rem;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all var(--transition-base);
}

.article-card:hover {
  transform: translateX(8px);
  border-color: var(--accent-primary);
}

.article-cover {
  width: 200px;
  height: 120px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-info {
  flex: 1;
}

.article-info h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.article-info p {
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin-bottom: 1rem;
}

.article-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: var(--text-tertiary);
}

@media (max-width: 1024px) {
  .knowledge-layout {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    position: static;
  }
}
</style>
