<template>
  <div class="articles-page">
    <div class="page-header">
      <h1>知识库管理</h1>
      <div class="header-actions">
        <RouterLink to="/admin/editor" class="btn btn-primary">
          撰写新文章
        </RouterLink>
      </div>
    </div>

    <div class="filter-bar">
      <div class="filter-group">
        <label>状态筛选:</label>
        <select v-model="filters.status" @change="loadArticles">
          <option value="">全部</option>
          <option value="DRAFT">草稿</option>
          <option value="EDITING">编辑中</option>
          <option value="PENDING_REVIEW">待审核</option>
          <option value="PUBLISHED">已发布</option>
          <option value="REJECTED">已驳回</option>
          <option value="OFFLINE">已下线</option>
        </select>
      </div>

      <div class="filter-group">
        <label>分类筛选:</label>
        <select v-model="filters.category" @change="loadArticles">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>

      <div class="filter-group search">
        <input
          v-model="filters.search"
          type="text"
          placeholder="搜索文章标题..."
          @keydown.enter="loadArticles"
        />
        <button @click="loadArticles" class="btn btn-icon">🔍</button>
      </div>
    </div>

    <div class="articles-table">
      <table>
        <thead>
          <tr>
            <th>标题</th>
            <th>分类</th>
            <th>状态</th>
            <th>作者</th>
            <th>浏览量</th>
            <th>发布时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="article in articles" :key="article.id">
            <td class="title-cell">
              <img v-if="article.coverImage" :src="article.coverImage" alt="封面" class="cover-thumb" />
              <span>{{ article.title }}</span>
            </td>
            <td>{{ article.categoryName }}</td>
            <td>
              <span class="status-badge" :class="article.status?.toLowerCase()">
                {{ getStatusText(article.status) }}
              </span>
            </td>
            <td>{{ article.author || '未知' }}</td>
            <td>{{ article.viewCount || 0 }}</td>
            <td>{{ formatDate(article.publishedAt || article.createdAt) }}</td>
            <td class="actions-cell">
              <RouterLink :to="`/admin/editor/${article.id}`" class="btn btn-sm">
                编辑
              </RouterLink>
              <button @click="previewArticle(article)" class="btn btn-sm btn-outline">
                预览
              </button>
              <button
                v-if="article.status === 'PUBLISHED'"
                @click="offlineArticle(article)"
                class="btn btn-sm btn-warning"
              >
                下线
              </button>
              <button @click="deleteArticle(article)" class="btn btn-sm btn-danger">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { articleApi, categoryApi } from '../../utils/auth'

const router = useRouter()

const articles = ref([])
const categories = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

const filters = ref({
  status: '',
  category: '',
  search: ''
})

onMounted(() => {
  loadArticles()
  loadCategories()
})

const loadCategories = async () => {
  try {
    const response = await categoryApi.getAll()
    categories.value = response || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadArticles = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }

    if (filters.value.status) {
      params.status = filters.value.status
    }

    if (filters.value.category) {
      params.categoryId = filters.value.category
    }

    if (filters.value.search) {
      params.search = filters.value.search
    }

    const response = await articleApi.getAll(params)
    
    if (response.content) {
      articles.value = response.content
      totalPages.value = response.totalPages || 1
    } else if (Array.isArray(response)) {
      articles.value = response
      totalPages.value = 1
    } else {
      articles.value = []
      totalPages.value = 1
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    articles.value = []
  }
}

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadArticles()
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const getStatusText = (status) => {
  const statusMap = {
    DRAFT: '草稿',
    EDITING: '编辑中',
    PENDING_REVIEW: '待审核',
    PUBLISHED: '已发布',
    REJECTED: '已驳回',
    OFFLINE: '已下线'
  }
  return statusMap[status] || status
}

const previewArticle = (article) => {
  window.open(`/knowledge/article/${article.slug}`, '_blank')
}

const offlineArticle = async (article) => {
  if (!confirm('确定要下线这篇文章吗？')) return

  try {
    await articleApi.offline(article.id)
    await loadArticles()
  } catch (error) {
    console.error('下线失败:', error)
    alert('下线失败')
  }
}

const deleteArticle = async (article) => {
  if (!confirm('确定要删除这篇文章吗？此操作不可撤销。')) return

  try {
    await articleApi.delete(article.id)
    await loadArticles()
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}
</script>

<style scoped>
.articles-page {
  max-width: 1400px;
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

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-warning {
  background: #f59e0b;
  color: white;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--border-color);
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}

.filter-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.filter-group select,
.filter-group input {
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.filter-group.search {
  flex: 1;
  max-width: 300px;
}

.filter-group.search input {
  flex: 1;
}

.articles-table {
  background: var(--bg-secondary);
  border-radius: 1rem;
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: var(--bg-tertiary);
}

th {
  padding: 1rem;
  text-align: left;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-secondary);
}

td {
  padding: 1rem;
  border-top: 1px solid var(--border-color);
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.cover-thumb {
  width: 60px;
  height: 40px;
  object-fit: cover;
  border-radius: 0.25rem;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-badge.draft {
  background: #e2e8f0;
  color: #4a5568;
}

.status-badge.editing {
  background: #fef3c7;
  color: #d97706;
}

.status-badge.pending_review {
  background: #dbeafe;
  color: #2563eb;
}

.status-badge.published {
  background: #d1fae5;
  color: #059669;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #dc2626;
}

.status-badge.offline {
  background: #f3f4f6;
  color: #6b7280;
}

.actions-cell {
  display: flex;
  gap: 0.5rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 0.875rem;
  color: var(--text-secondary);
}
</style>