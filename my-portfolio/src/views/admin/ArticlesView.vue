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
        <label>显示:</label>
        <select v-model="filters.published" @change="loadArticles">
          <option value="">全部</option>
          <option value="true">已发布</option>
          <option value="false">未发布</option>
        </select>
      </div>

      <div class="filter-group">
        <label>分类:</label>
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
            <th>阅读量</th>
            <th>点赞</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="article in articles" :key="article.id">
            <td class="title-cell">
              <RouterLink :to="`/admin/editor/${article.id}`">
                {{ article.title }}
              </RouterLink>
            </td>
            <td>{{ article.categoryName }}</td>
            <td>
              <span class="status-badge" :class="article.isPublished ? 'published' : 'draft'">
                {{ article.isPublished ? '已发布' : '未发布' }}
              </span>
            </td>
            <td>{{ article.viewCount || 0 }}</td>
            <td>{{ article.likeCount || 0 }}</td>
            <td>{{ formatDate(article.createdAt) }}</td>
            <td class="actions-cell">
              <RouterLink :to="`/admin/editor/${article.id}`" class="btn-icon-link">✏️</RouterLink>
              <button @click="confirmDelete(article)" class="btn-icon-link delete">🗑️</button>
            </td>
          </tr>
          <tr v-if="articles.length === 0">
            <td colspan="7" class="empty">暂无文章</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button @click="prevPage" :disabled="currentPage === 0" class="btn">上一页</button>
      <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage >= totalPages - 1" class="btn">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { articleApi, categoryApi } from '../../utils/auth'

const articles = ref([])
const categories = ref([])
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(0)

const filters = ref({
  published: '',
  category: '',
  search: ''
})

onMounted(() => {
  loadCategories()
  loadArticles()
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
      page: currentPage.value,
      size: pageSize.value
    }
    
    if (filters.value.category) params.categoryId = filters.value.category
    if (filters.value.search) params.search = filters.value.search
    
    const response = await articleApi.getAll(params)
    articles.value = response.content || []
    totalPages.value = response.totalPages || 1
  } catch (error) {
    console.error('加载文章失败:', error)
  }
}

const confirmDelete = async (article) => {
  if (!confirm(`确定要删除文章"${article.title}"吗？`)) return
  
  try {
    await articleApi.delete(article.id)
    loadArticles()
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    loadArticles()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    loadArticles()
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.articles-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 5px;
}

.filter-group label {
  font-size: 14px;
  color: #666;
}

.filter-group select, .filter-group input {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.filter-group.search {
  margin-left: auto;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background: #3498db;
  color: white;
  text-decoration: none;
}

.btn-icon {
  padding: 6px 12px;
}

.articles-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f5f5f5;
  font-weight: 600;
  font-size: 14px;
}

.title-cell a {
  color: #333;
  text-decoration: none;
}

.title-cell a:hover {
  color: #3498db;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-badge.published {
  background: #d4edda;
  color: #155724;
}

.status-badge.draft {
  background: #fff3cd;
  color: #856404;
}

.actions-cell {
  display: flex;
  gap: 8px;
}

.btn-icon-link {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 4px;
  text-decoration: none;
}

.btn-icon-link:hover {
  opacity: 0.7;
}

.btn-icon-link.delete:hover {
  color: #e74c3c;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>