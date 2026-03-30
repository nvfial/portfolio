<template>
  <div class="article-viewer">
    <div class="article-header">
      <div class="article-breadcrumb">
        <NuxtLink to="/knowledge" class="breadcrumb-link">知识库</NuxtLink>
        <span class="breadcrumb-separator">/</span>
        <NuxtLink :to="`/knowledge/${article?.domain?.slug}`" class="breadcrumb-link">
          {{ article?.domain?.name }}
        </NuxtLink>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">{{ article?.title }}</span>
      </div>
      
      <h1 class="article-title">{{ article?.title }}</h1>
      
      <div class="article-meta">
        <div class="meta-left">
          <span class="author" v-if="article?.author">
            <span class="author-avatar">👤</span>
            {{ article.author }}
          </span>
          <span class="publish-date">
            📅 {{ formatDate(article?.createdAt) }}
          </span>
        </div>
        
        <div class="meta-right">
          <span class="view-count">👁 {{ article?.viewCount || 0 }}</span>
          <span class="like-count">❤️ {{ article?.likeCount || 0 }}</span>
          <span class="read-time">⏱ {{ article?.readTime || 5 }}分钟</span>
        </div>
      </div>
      
      <div class="article-tags">
        <span 
          v-for="tag in article?.tags" 
          :key="tag"
          class="tag"
        >
          {{ tag }}
        </span>
      </div>
    </div>
    
    <div class="article-cover" v-if="article?.coverImage">
      <img :src="article.coverImage" :alt="article.title" />
    </div>
    
    <div class="article-content" v-html="renderedContent"></div>
    
    <div class="article-actions">
      <button class="action-btn like-btn" @click="handleLike">
        <span class="btn-icon">{{ liked ? '❤️' : '🤍' }}</span>
        <span class="btn-text">{{ liked ? '已赞' : '点赞' }}</span>
      </button>
      
      <button class="action-btn share-btn" @click="handleShare">
        <span class="btn-icon">📤</span>
        <span class="btn-text">分享</span>
      </button>
      
      <button class="action-btn comment-btn" @click="scrollToComments">
        <span class="btn-icon">💬</span>
        <span class="btn-text">评论</span>
      </button>
    </div>
    
    <div class="article-toc" v-if="toc.length > 0">
      <h4>目录</h4>
      <ul>
        <li 
          v-for="item in toc" 
          :key="item.id"
          :class="[`toc-level-${item.level}`, { active: activeToc === item.id }]"
        >
          <a @click="scrollToHeading(item.id)">{{ item.text }}</a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  article: { type: Object, default: null },
})

const emit = defineEmits(['like', 'share', 'comment'])

const liked = ref(false)
const activeToc = ref('')
const toc = ref([])

const renderedContent = computed(() => {
  if (!props.article?.content) return ''
  return marked(props.article.content)
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const extractToc = () => {
  if (!props.article?.content) return
  
  const renderer = new marked.Renderer()
  const headings = []
  
  renderer.heading = (args) => {
    const text = args.text
    const id = text.toLowerCase().replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    headings.push({
      id,
      text,
      level: args.depth
    })
    return `<h${args.depth} id="${id}">${text}</h${args.depth}>`
  }
  
  marked(props.article.content, { renderer })
  toc.value = headings
}

const scrollToHeading = (id) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
    activeToc.value = id
  }
}

const handleLike = () => {
  liked.value = !liked.value
  emit('like', props.article?.id)
}

const handleShare = () => {
  emit('share', props.article)
}

const scrollToComments = () => {
  emit('comment')
}

onMounted(() => {
  extractToc()
})
</script>

<style scoped>
.article-viewer {
  max-width: 800px;
  margin: 0 auto;
}

.article-header {
  margin-bottom: 2rem;
}

.article-breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

.breadcrumb-link {
  color: var(--text-tertiary);
  transition: color var(--transition-fast);
}

.breadcrumb-link:hover {
  color: var(--accent-primary);
}

.breadcrumb-separator {
  color: var(--text-tertiary);
}

.breadcrumb-current {
  color: var(--text-secondary);
}

.article-title {
  font-size: clamp(2rem, 5vw, 3rem);
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  color: var(--text-primary);
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
  padding: 1rem 0;
  border-bottom: 1px solid var(--border-color);
}

.meta-left,
.meta-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.author,
.publish-date,
.view-count,
.like-count,
.read-time {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.author-avatar {
  font-size: 1rem;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 1rem;
}

.tag {
  padding: 0.25rem 0.75rem;
  font-size: 0.8rem;
  border-radius: var(--radius-full);
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.tag:hover {
  background: var(--accent-primary);
  color: white;
}

.article-cover {
  margin: 2rem 0;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: auto;
}

.article-content {
  font-size: 1.1rem;
  line-height: 1.8;
  color: var(--text-primary);
}

.article-content :deep(h2) {
  font-size: 1.75rem;
  font-weight: 600;
  margin: 2.5rem 0 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.article-content :deep(h3) {
  font-size: 1.4rem;
  font-weight: 600;
  margin: 2rem 0 0.75rem;
}

.article-content :deep(p) {
  margin-bottom: 1.5rem;
}

.article-content :deep(code) {
  padding: 0.2rem 0.4rem;
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.9em;
  background: var(--bg-tertiary);
  border-radius: var(--radius-sm);
}

.article-content :deep(pre) {
  padding: 1.5rem;
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  overflow-x: auto;
  margin: 1.5rem 0;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  padding-left: 1.5rem;
  margin-bottom: 1.5rem;
}

.article-content :deep(li) {
  margin-bottom: 0.5rem;
}

.article-content :deep(blockquote) {
  padding: 1rem 1.5rem;
  border-left: 4px solid var(--accent-primary);
  background: var(--bg-tertiary);
  margin: 1.5rem 0;
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
}

.article-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin: 3rem 0;
  padding: 1.5rem;
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius-full);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  font-size: 0.9rem;
  transition: all var(--transition-fast);
}

.action-btn:hover {
  border-color: var(--accent-primary);
  color: var(--accent-primary);
  transform: translateY(-2px);
}

.like-btn.liked {
  background: #fee2e2;
  border-color: #ef4444;
  color: #ef4444;
}

.article-toc {
  position: fixed;
  right: 2rem;
  top: 100px;
  width: 200px;
  padding: 1rem;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
}

.article-toc h4 {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: var(--text-primary);
}

.article-toc ul {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.article-toc a {
  font-size: 0.85rem;
  color: var(--text-secondary);
  transition: color var(--transition-fast);
}

.article-toc a:hover {
  color: var(--accent-primary);
}

.toc-level-3 {
  padding-left: 1rem;
}

.active a {
  color: var(--accent-primary);
  font-weight: 500;
}

@media (max-width: 1200px) {
  .article-toc {
    display: none;
  }
}
</style>
