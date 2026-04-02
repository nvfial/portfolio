<template>
  <div class="article-detail">
    <div class="article-layout">
      <main class="article-main">
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="!article" class="not-found">
          <div class="not-found-icon">🔍</div>
          <h2>文章不存在</h2>
          <p>您访问的文章可能已被删除或不存在</p>
          <button @click="goBack">返回知识库</button>
        </div>

        <article v-else class="article-content">
          <header class="article-header">
            <div class="breadcrumb">
              <span @click="goToKnowledge">知识库</span>
              <span class="separator">/</span>
              <span v-if="article.category" @click="goToCategory(article.category.id)">
                {{ article.category.name }}
              </span>
            </div>
            
            <h1 class="title">
              {{ article.title }}
              <span v-if="article.isFeatured" class="featured-badge">⭐ 精选</span>
            </h1>
            
            <div class="meta">
              <div class="author" v-if="article.authorName">
                <span class="avatar">👤</span>
                {{ article.authorName }}
              </div>
              <span class="date">📅 {{ formatDate(article.createdAt) }}</span>
              <span class="views">👁️ {{ article.viewCount }} 阅读</span>
              <span class="read-time">📖 {{ article.readTime }} 分钟</span>
            </div>

            <div class="tags" v-if="article.tags && article.tags.length">
              <span 
                v-for="tag in article.tags" 
                :key="tag"
                class="tag"
                @click="goToTag(tag)"
              >
                #{{ tag }}
              </span>
            </div>
          </header>

          <div v-if="article.coverImage" class="cover-image">
            <img :src="article.coverImage" :alt="article.title" />
          </div>

          <div class="markdown-body" v-html="renderedContent"></div>

          <footer class="article-footer">
            <div class="actions">
              <button 
                class="action-btn like-btn"
                :class="{ liked: hasLiked }"
                @click="toggleLike"
              >
                <span class="icon">{{ hasLiked ? '❤️' : '🤍' }}</span>
                {{ hasLiked ? '已赞' : '点赞' }} ({{ article.likeCount }})
              </button>
              <button 
                class="action-btn favorite-btn"
                :class="{ favorited: hasFavorited }"
                @click="toggleFavorite"
              >
                <span class="icon">{{ hasFavorited ? '⭐' : '☆' }}</span>
                {{ hasFavorited ? '已收藏' : '收藏' }}
              </button>
              <button class="action-btn" @click="shareArticle">
                <span class="icon">📤</span> 分享
              </button>
            </div>
          </footer>
        </article>

        <div v-if="article" class="comments-section">
          <h3>评论 ({{ comments.length }})</h3>
          <div class="comment-form" v-if="isAuthenticated">
            <textarea 
              v-model="newComment" 
              placeholder="写下你的评论..."
              rows="3"
            ></textarea>
            <button @click="submitComment" :disabled="!newComment.trim()">
              提交评论
            </button>
          </div>
          <div v-else class="login-prompt">
            <p>登录后参与评论</p>
          </div>
          <div class="comments-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-avatar">{{ comment.authorName?.charAt(0) || '?' }}</div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.authorName }}</span>
                  <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
              </div>
            </div>
            <div v-if="comments.length === 0" class="no-comments">
              暂无评论，快来发表第一条评论吧！
            </div>
          </div>
        </div>

        <div v-if="article" class="related-articles">
          <h3>相关文章</h3>
          <div class="related-list">
            <div 
              v-for="related in relatedArticles" 
              :key="related.id"
              class="related-card"
              @click="goToArticle(related.slug)"
            >
              <div class="related-cover" v-if="related.coverImage">
                <img :src="related.coverImage" :alt="related.title" />
              </div>
              <div class="related-info">
                <h4>{{ related.title }}</h4>
                <span>{{ related.viewCount }} 阅读</span>
              </div>
            </div>
          </div>
        </div>
      </main>

      <aside class="article-sidebar">
        <div class="toc-container" v-if="toc.length > 0">
          <div class="toc-header">
            <span class="icon">📋</span>
            <span>目录</span>
          </div>
          <nav class="toc">
            <a 
              v-for="item in toc" 
              :key="item.id"
              :href="`#${item.id}`"
              :class="['toc-item', `level-${item.level}`, { active: activeHeading === item.id }]"
              @click.prevent="scrollToHeading(item.id)"
            >
              {{ item.text }}
            </a>
          </nav>
        </div>

        <div class="sidebar-widget" v-if="article">
          <div class="widget-header">文章信息</div>
          <div class="widget-content">
            <div class="info-item">
              <span class="label">阅读:</span>
              <span class="value">{{ article.viewCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">点赞:</span>
              <span class="value">{{ article.likeCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ formatDate(article.createdAt) }}</span>
            </div>
            <div class="info-item" v-if="article.updatedAt !== article.createdAt">
              <span class="label">更新时间:</span>
              <span class="value">{{ formatDate(article.updatedAt) }}</span>
            </div>
          </div>
        </div>

        <button 
          class="back-to-top"
          :class="{ visible: showBackToTop }"
          @click="scrollToTop"
        >
          ↑
        </button>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { knowledgeApi } from '../utils/api'
import { marked } from 'marked'
import hljs from 'highlight.js'

marked.setOptions({
  gfm: true,
  breaks: true,
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
})

const route = useRoute()
const router = useRouter()

const article = ref(null)
const relatedArticles = ref([])
const loading = ref(true)
const toc = ref([])
const activeHeading = ref('')
const showBackToTop = ref(false)
const hasLiked = ref(false)
const hasFavorited = ref(false)
const comments = ref([])
const newComment = ref('')
const isAuthenticated = ref(false)

const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return marked.parse(article.value.content)
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const fetchArticle = async () => {
  loading.value = true
  try {
    const slug = route.params.slug
    const result = await knowledgeApi.getArticleBySlug(slug)
    article.value = result
    
    if (result) {
      knowledgeApi.incrementView(result.id)
      generateToc(result.content)
      fetchRelatedArticles(result.category?.id, result.id)
      fetchComments()
    }
  } catch (error) {
    console.error('获取文章失败:', error)
    article.value = null
  } finally {
    loading.value = false
  }
}

const fetchRelatedArticles = async (categoryId, currentId) => {
  if (!categoryId) return
  try {
    const result = await knowledgeApi.getArticlesByCategory(categoryId)
    relatedArticles.value = (result || [])
      .filter(a => a.id !== currentId)
      .slice(0, 4)
  } catch (error) {
    console.error('获取相关文章失败:', error)
    relatedArticles.value = []
  }
}

const generateToc = (content) => {
  const headingRegex = /^(#{1,3})\s+(.+)$/gm
  const headings = []
  let match
  
  while ((match = headingRegex.exec(content)) !== null) {
    const level = match[1].length
    const text = match[2].trim()
    const id = text.toLowerCase().replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    headings.push({ level, text, id })
  }
  
  toc.value = headings
}

const scrollToHeading = (id) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    activeHeading.value = id
  }
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const toggleLike = async () => {
  if (!article.value) return
  
  try {
    await knowledgeApi.toggleLike(article.value.id)
    hasLiked.value = !hasLiked.value
    article.value.likeCount += hasLiked.value ? 1 : -1
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const toggleFavorite = async () => {
  if (!article.value) return
  
  try {
    await knowledgeApi.toggleFavorite(article.value.id)
    hasFavorited.value = !hasFavorited.value
  } catch (error) {
    console.error('收藏失败:', error)
  }
}

const fetchComments = async () => {
  if (!article.value) return
  
  try {
    const data = await knowledgeApi.getComments(article.value.id)
    comments.value = data.content || []
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

const submitComment = async () => {
  if (!article.value || !newComment.value.trim()) return
  
  try {
    await knowledgeApi.addComment(article.value.id, newComment.value)
    newComment.value = ''
    await fetchComments()
  } catch (error) {
    console.error('提交评论失败:', error)
  }
}

const shareArticle = async () => {
  if (navigator.share) {
    try {
      await navigator.share({
        title: article.value.title,
        text: article.value.summary,
        url: window.location.href
      })
    } catch (error) {
      console.log('分享取消')
    }
  } else {
    const url = window.location.href
    await navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  }
}

const goToKnowledge = () => {
  router.push('/knowledge')
}

const goToCategory = (categoryId) => {
  router.push({ path: '/knowledge', query: { category: categoryId } })
}

const goToTag = (tag) => {
  router.push({ path: '/knowledge', query: { tag } })
}

const goToArticle = (slug) => {
  router.push(`/knowledge/${slug}`)
}

const handleScroll = () => {
  showBackToTop.value = window.scrollY > 300
  
  const headings = toc.value.map(h => document.getElementById(h.id)).filter(Boolean)
  for (let i = headings.length - 1; i >= 0; i--) {
    const heading = headings[i]
    const rect = heading.getBoundingClientRect()
    if (rect.top <= 100) {
      activeHeading.value = toc.value[i].id
      break
    }
  }
}

onMounted(() => {
  fetchArticle()
  window.addEventListener('scroll', handleScroll)
})

watch(() => route.params.slug, () => {
  fetchArticle()
  window.scrollTo({ top: 0, behavior: 'smooth' })
})
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background: #f8f9fa;
}

.article-layout {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px;
  gap: 40px;
}

.article-main {
  flex: 1;
  min-width: 0;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px;
  color: #999;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.not-found {
  text-align: center;
  padding: 100px 40px;
  background: white;
  border-radius: 16px;
}

.not-found-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.not-found h2 {
  margin-bottom: 10px;
  color: #333;
}

.not-found p {
  color: #666;
  margin-bottom: 30px;
}

.not-found button {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
}

.article-content {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.article-header {
  padding: 40px 40px 20px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: #999;
  margin-bottom: 16px;
}

.breadcrumb span:first-child {
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb span:first-child:hover {
  color: #667eea;
}

.breadcrumb span:last-child {
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb span:last-child:hover {
  color: #667eea;
}

.separator {
  color: #ddd;
}

.title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #1a1a1a;
  line-height: 1.3;
  margin-bottom: 20px;
}

.featured-badge {
  display: inline-block;
  margin-left: 12px;
  padding: 4px 12px;
  background: linear-gradient(135deg, #ffd700 0%, #ffaa00 100%);
  border-radius: 20px;
  font-size: 0.8rem;
  vertical-align: middle;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 20px;
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 16px;
}

.meta > div, .meta > span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.author .avatar {
  font-size: 1.1rem;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag {
  padding: 6px 14px;
  background: #f0f0f5;
  border-radius: 20px;
  font-size: 0.85rem;
  color: #667eea;
  cursor: pointer;
  transition: all 0.2s;
}

.tag:hover {
  background: #667eea;
  color: white;
}

.cover-image {
  width: 100%;
  max-height: 450px;
  overflow: hidden;
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.markdown-body {
  padding: 20px 40px 40px;
  font-size: 1.05rem;
  line-height: 1.8;
  color: #333;
}

.markdown-body :deep(h1) {
  font-size: 1.8rem;
  font-weight: 800;
  margin: 40px 0 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eee;
}

.markdown-body :deep(h2) {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 35px 0 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.markdown-body :deep(h3) {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 25px 0 12px;
}

.markdown-body :deep(p) {
  margin: 16px 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin: 16px 0;
  padding-left: 24px;
}

.markdown-body :deep(li) {
  margin: 8px 0;
}

.markdown-body :deep(code) {
  padding: 2px 8px;
  background: #f5f5f5;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
  font-size: 0.9em;
  color: #e83e8c;
}

.markdown-body :deep(pre) {
  background: #282c34;
  color: #abb2bf;
  padding: 20px;
  border-radius: 12px;
  overflow-x: auto;
  margin: 20px 0;
}

.markdown-body :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
}

.markdown-body :deep(blockquote) {
  margin: 20px 0;
  padding: 16px 20px;
  border-left: 4px solid #667eea;
  background: #f8f9fa;
  border-radius: 0 8px 8px 0;
}

.markdown-body :deep(a) {
  color: #667eea;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.2s;
}

.markdown-body :deep(a:hover) {
  border-bottom-color: #667eea;
}

.markdown-body :deep(img) {
  max-width: 100%;
  border-radius: 12px;
  margin: 20px 0;
}

.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  padding: 12px 16px;
  border: 1px solid #eee;
  text-align: left;
}

.markdown-body :deep(th) {
  background: #f8f9fa;
  font-weight: 600;
}

.article-footer {
  padding: 30px 40px;
  border-top: 1px solid #eee;
}

.actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #f5f5f5;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.95rem;
  color: #666;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e8e8e8;
  transform: translateY(-2px);
}

.action-btn.like-btn.liked {
  background: #ffeef0;
  color: #e83e8c;
}

.action-btn.favorite-btn {
  background: #fff8e1;
  color: #ffc107;
}

.action-btn.favorite-btn.favorited {
  background: #fff3cd;
  color: #ff9800;
}

.comments-section {
  margin-top: 40px;
  padding: 30px;
  background: white;
  border-radius: 16px;
}

.comments-section h3 {
  font-size: 1.3rem;
  margin-bottom: 20px;
  color: #333;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-form textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  resize: vertical;
  font-family: inherit;
}

.comment-form textarea:focus {
  outline: none;
  border-color: #667eea;
}

.comment-form button {
  margin-top: 10px;
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s;
}

.comment-form button:hover:not(:disabled) {
  background: #5568d3;
  transform: translateY(-2px);
}

.comment-form button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-prompt {
  padding: 20px;
  text-align: center;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.login-prompt p {
  color: #666;
  margin: 0;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 12px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.comment-author {
  font-weight: 600;
  color: #333;
}

.comment-date {
  font-size: 0.85rem;
  color: #999;
}

.comment-text {
  color: #555;
  line-height: 1.6;
  margin: 0;
}

.no-comments {
  text-align: center;
  padding: 30px;
  color: #999;
}

.related-articles {
  margin-top: 40px;
  padding: 30px;
  background: white;
  border-radius: 16px;
}

.related-articles h3 {
  font-size: 1.3rem;
  margin-bottom: 20px;
  color: #333;
}

.related-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.related-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.related-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.related-cover {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.related-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.related-info h4 {
  font-size: 0.95rem;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.4;
}

.related-info span {
  font-size: 0.8rem;
  color: #999;
}

.article-sidebar {
  width: 280px;
  flex-shrink: 0;
  position: sticky;
  top: 30px;
  height: fit-content;
}

.toc-container {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.toc-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.toc-header .icon {
  font-size: 1.1rem;
}

.toc {
  max-height: 400px;
  overflow-y: auto;
}

.toc-item {
  display: block;
  padding: 8px 12px;
  color: #666;
  text-decoration: none;
  font-size: 0.9rem;
  border-radius: 6px;
  transition: all 0.2s;
  border-left: 2px solid transparent;
}

.toc-item:hover {
  background: #f5f5f5;
  color: #333;
}

.toc-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  border-left-color: #667eea;
}

.toc-item.level-2 {
  padding-left: 24px;
  font-size: 0.88rem;
}

.toc-item.level-3 {
  padding-left: 36px;
  font-size: 0.85rem;
  color: #999;
}

.sidebar-widget {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.widget-header {
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.widget-content {
  font-size: 0.9rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #999;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

.back-to-top {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  cursor: pointer;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  z-index: 100;
}

.back-to-top.visible {
  opacity: 1;
  visibility: visible;
}

.back-to-top:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

@media (max-width: 1024px) {
  .article-layout {
    flex-direction: column;
  }
  
  .article-sidebar {
    width: 100%;
    position: static;
  }
  
  .toc-container {
    display: none;
  }
}

@media (max-width: 768px) {
  .article-header {
    padding: 24px 20px 16px;
  }
  
  .title {
    font-size: 1.6rem;
  }
  
  .meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .markdown-body {
    padding: 16px 20px 30px;
  }
  
  .article-footer {
    padding: 20px;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .related-articles {
    padding: 20px;
  }
}
</style>