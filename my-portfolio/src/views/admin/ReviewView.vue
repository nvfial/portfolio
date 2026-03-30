<template>
  <div class="review-page">
    <div class="page-header">
      <h1>文章审核</h1>
      <div class="filter-tabs">
        <button
          v-for="status in reviewStatuses"
          :key="status.value"
          :class="['tab', { active: currentFilter === status.value }]"
          @click="currentFilter = status.value"
        >
          {{ status.label }}
          <span class="count" v-if="status.count > 0">{{ status.count }}</span>
        </button>
      </div>
    </div>

    <div class="articles-list">
      <div v-if="loading" class="loading">加载中...</div>

      <div v-else-if="articles.length === 0" class="empty">
        <p>暂无待审核文章</p>
      </div>

      <div v-else class="article-cards">
        <div v-for="article in articles" :key="article.id" class="article-card">
          <div class="card-header">
            <img v-if="article.coverImage" :src="article.coverImage" alt="封面" class="cover" />
            <div v-else class="cover-placeholder">无封面</div>
            <div class="card-info">
              <h3 class="title">{{ article.title }}</h3>
              <div class="meta">
                <span class="category">{{ article.domainName }} / {{ article.categoryName }}</span>
                <span class="author">{{ article.author || '未知' }}</span>
                <span class="date">{{ formatDate(article.createdAt) }}</span>
              </div>
            </div>
          </div>

          <div class="card-content">
            <p class="summary">{{ article.summary || '暂无摘要' }}</p>
            <div class="tags" v-if="article.tags">
              <span v-for="tag in JSON.parse(article.tags)" :key="tag" class="tag">
                {{ tag }}
              </span>
            </div>
          </div>

          <div class="card-footer">
            <div class="stats">
              <span>👁️ {{ article.viewCount }}</span>
              <span>❤️ {{ article.likeCount }}</span>
              <span>⏱️ {{ article.readTime }}分钟</span>
            </div>
            <div class="actions">
              <button @click="viewArticle(article)" class="btn btn-outline">
                查看详情
              </button>
              <button @click="showReviewModal(article, true)" class="btn btn-success">
                通过
              </button>
              <button @click="showReviewModal(article, false)" class="btn btn-danger">
                驳回
              </button>
            </div>
          </div>

          <div v-if="article.reviewComment" class="review-history">
            <strong>审核意见:</strong> {{ article.reviewComment }}
          </div>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button
        v-for="page in totalPages"
        :key="page"
        :class="['page-btn', { active: currentPage === page }]"
        @click="changePage(page)"
      >
        {{ page }}
      </button>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <h3>{{ isApproving ? '通过审核' : '驳回文章' }}</h3>
        
        <div class="form-group">
          <label>{{ isApproving ? '通过意见（可选）' : '驳回原因（必填）' }}</label>
          <textarea
            v-model="reviewComment"
            :placeholder="isApproving ? '可添加通过说明...' : '请说明驳回原因，帮助作者改进...'"
            class="comment-textarea"
          ></textarea>
        </div>

        <div class="modal-actions">
          <button @click="showModal = false" class="btn btn-secondary">取消</button>
          <button
            @click="submitReview"
            class="btn"
            :class="isApproving ? 'btn-success' : 'btn-danger'"
            :disabled="!isApproving && !reviewComment.trim()"
          >
            确认{{ isApproving ? '通过' : '驳回' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { articleApi } from '../../utils/auth'

const articles = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = computed(() => Math.ceil(articles.value.length / pageSize.value))

const currentFilter = ref('PENDING_REVIEW')
const reviewStatuses = ref([
  { label: '待审核', value: 'PENDING_REVIEW', count: 0 },
  { label: '已通过', value: 'PUBLISHED', count: 0 },
  { label: '已驳回', value: 'REJECTED', count: 0 },
  { label: '已下线', value: 'OFFLINE', count: 0 }
])

const showModal = ref(false)
const isApproving = ref(true)
const reviewComment = ref('')
const currentArticle = ref(null)

onMounted(() => {
  loadArticles()
})

watch(currentFilter, () => {
  currentPage.value = 1
  loadArticles()
})

const loadArticles = async () => {
  loading.value = true
  try {
    const response = await articleApi.getAll({
      status: currentFilter.value,
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    if (response.content) {
      articles.value = response.content
    } else {
      articles.value = Array.isArray(response) ? response : []
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    articles.value = []
  } finally {
    loading.value = false
  }
}

const changePage = (page) => {
  currentPage.value = page
  loadArticles()
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const viewArticle = (article) => {
  window.open(`/knowledge/article/${article.slug}`, '_blank')
}

const showReviewModal = (article, isApprove) => {
  currentArticle.value = article
  isApproving.value = isApprove
  reviewComment.value = ''
  showModal.value = true
}

const submitReview = async () => {
  try {
    if (isApproving.value) {
      await articleApi.approve(currentArticle.value.id, reviewComment.value)
    } else {
      await articleApi.reject(currentArticle.value.id, reviewComment.value)
    }
    
    showModal.value = false
    loadArticles()
  } catch (error) {
    console.error('审核操作失败:', error)
    alert('操作失败')
  }
}
</script>

<style scoped>
.review-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 1.5rem;
}

.filter-tabs {
  display: flex;
  gap: 0.5rem;
}

.tab {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
}

.tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.tab .count {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
  font-size: 0.75rem;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.loading, .empty {
  padding: 3rem;
  text-align: center;
  color: var(--text-secondary);
}

.article-card {
  background: var(--bg-secondary);
  border-radius: 1rem;
  padding: 1.5rem;
  transition: transform 0.2s, box-shadow 0.2s;
}

.article-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.cover {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 0.5rem;
}

.cover-placeholder {
  width: 120px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-tertiary);
  border-radius: 0.5rem;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.card-info {
  flex: 1;
}

.card-info .title {
  font-size: 1.125rem;
  margin-bottom: 0.5rem;
}

.card-info .meta {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.card-content {
  margin-bottom: 1rem;
}

.summary {
  color: var(--text-secondary);
  font-size: 0.875rem;
  line-height: 1.6;
  margin-bottom: 0.75rem;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  padding: 0.25rem 0.5rem;
  background: var(--bg-tertiary);
  border-radius: 0.25rem;
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.stats {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
}

.btn-success {
  background: #10b981;
  color: white;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
}

.review-history {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #fef3c7;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  color: #92400e;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 2rem;
}

.page-btn {
  width: 2.5rem;
  height: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  cursor: pointer;
}

.page-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--bg-primary);
  border-radius: 1rem;
  padding: 2rem;
  width: 100%;
  max-width: 500px;
}

.modal h3 {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.comment-textarea {
  width: 100%;
  min-height: 120px;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  resize: vertical;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}
</style>