<template>
  <div class="article-editor">
    <div class="editor-header">
      <h1>{{ isEdit ? '编辑文章' : '撰写新文章' }}</h1>
      <div class="editor-actions">
        <button @click="saveDraft" class="btn btn-secondary" :disabled="saving">
          保存草稿
        </button>
        <button @click="publishArticle" class="btn btn-primary" :disabled="saving || !canPublish">
          {{ form.isPublished ? '取消发布' : '发布文章' }}
        </button>
      </div>
    </div>

    <div class="editor-content">
      <div class="editor-main">
        <div class="form-group">
          <input
            v-model="form.title"
            type="text"
            placeholder="文章标题"
            class="title-input"
          />
        </div>

        <div class="form-group">
          <select v-model="form.categoryId" class="category-select">
            <option value="">选择分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.domainName }} / {{ cat.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <div class="cover-uploader" @click="triggerCoverUpload">
            <img v-if="form.coverImage" :src="form.coverImage" alt="封面" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <span>点击上传封面图片</span>
            </div>
          </div>
          <input
            ref="coverInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleCoverUpload"
          />
        </div>

        <div class="form-group">
          <textarea
            v-model="form.content"
            placeholder="撰写文章内容..."
            class="content-textarea"
          ></textarea>
        </div>
      </div>

      <div class="editor-sidebar">
        <div class="sidebar-section">
          <h3>标签</h3>
          <div class="tags-input">
            <div class="tags-list">
              <span v-for="tag in form.tags" :key="tag" class="tag">
                {{ tag }}
                <button @click="removeTag(tag)" class="tag-remove">×</button>
              </span>
            </div>
            <input
              v-model="newTag"
              type="text"
              placeholder="添加标签"
              @keydown.enter.prevent="addTag"
              class="tag-input"
            />
          </div>
        </div>

        <div class="sidebar-section">
          <h3>摘要</h3>
          <textarea
            v-model="form.summary"
            placeholder="文章摘要（可选）"
            class="summary-textarea"
          ></textarea>
        </div>

        <div class="sidebar-section">
          <h3>SEO设置</h3>
          <div class="form-group">
            <label>SEO标题</label>
            <input v-model="form.seoTitle" type="text" placeholder="SEO标题" />
          </div>
          <div class="form-group">
            <label>SEO描述</label>
            <textarea v-model="form.seoDescription" placeholder="SEO描述" class="seo-textarea"></textarea>
          </div>
        </div>

        <div class="sidebar-section">
          <h3>文章设置</h3>
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.isFeatured" />
              推荐文章
            </label>
          </div>
          <div class="form-group">
            <label>预计阅读时间（分钟）</label>
            <input v-model.number="form.readTime" type="number" min="1" />
          </div>
        </div>

        <div class="sidebar-section" v-if="isEdit">
          <h3>统计信息</h3>
          <div class="stats">
            <div class="stat-item">
              <span class="stat-label">阅读量</span>
              <span class="stat-value">{{ articleStats.viewCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">点赞数</span>
              <span class="stat-value">{{ articleStats.likeCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">创建时间</span>
              <span class="stat-value">{{ formatDate(articleStats.createdAt) }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">更新时间</span>
              <span class="stat-value">{{ formatDate(articleStats.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { articleApi, categoryApi, uploadApi } from '../../utils/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isEdit = computed(() => !!route.params.id)
const articleId = computed(() => route.params.id)

const form = ref({
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: '',
  tags: [],
  seoTitle: '',
  seoDescription: '',
  isFeatured: false,
  isPublished: false,
  readTime: 5,
  authorId: authStore.user?.id,
  authorName: authStore.user?.displayName || authStore.user?.username
})

const categories = ref([])
const newTag = ref('')
const saving = ref(false)
const articleStats = ref({})

const canPublish = computed(() => {
  return form.value.title && form.value.content && form.value.categoryId
})

const wordCount = computed(() => {
  return form.value.content.replace(/\s/g, '').length
})

onMounted(async () => {
  await loadCategories()
  
  if (isEdit.value) {
    await loadArticle()
  }

  setInterval(autoSave, 120000)
})

watch(() => form.value.content, () => {
  if (isEdit.value && form.value.readTime === 5) {
    form.value.readTime = Math.max(1, Math.ceil(wordCount.value / 500))
  }
})

const loadCategories = async () => {
  try {
    const response = await categoryApi.getAll()
    categories.value = response || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadArticle = async () => {
  try {
    const response = await articleApi.getById(articleId.value)
    const article = response

    form.value = {
      title: article.title,
      content: article.content,
      summary: article.summary || '',
      coverImage: article.coverImage || '',
      categoryId: article.categoryId || '',
      tags: article.tags || [],
      seoTitle: article.seoTitle || '',
      seoDescription: article.seoDescription || '',
      isFeatured: article.isFeatured || false,
      isPublished: article.isPublished || false,
      readTime: article.readTime || 5,
      authorId: article.authorId,
      authorName: article.authorName
    }

    articleStats.value = {
      viewCount: article.viewCount,
      likeCount: article.likeCount,
      createdAt: article.createdAt,
      updatedAt: article.updatedAt
    }
  } catch (error) {
    console.error('加载文章失败:', error)
  }
}

const saveDraft = async () => {
  saving.value = true
  try {
    const data = {
      ...form.value,
      authorId: authStore.user?.id,
      authorName: authStore.user?.displayName || authStore.user?.username
    }

    if (isEdit.value) {
      await articleApi.update(articleId.value, data)
    } else {
      const response = await articleApi.create(data)
      if (!isEdit.value) {
        router.replace(`/admin/editor/${response.id}`)
      }
    }
    alert('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  } finally {
    saving.value = false
  }
}

const publishArticle = async () => {
  form.value.isPublished = !form.value.isPublished
  await saveDraft()
}

const autoSave = async () => {
  if (!form.value.title && !form.value.content) return
  
  try {
    const data = {
      ...form.value,
      authorId: authStore.user?.id,
      authorName: authStore.user?.displayName || authStore.user?.username
    }

    if (isEdit.value) {
      await articleApi.update(articleId.value, data)
    } else if (form.value.title) {
      const response = await articleApi.create(data)
      if (!isEdit.value && articleId.value) {
        router.replace(`/admin/editor/${response.id}`)
      }
    }
  } catch (error) {
    console.error('自动保存失败:', error)
  }
}

const triggerCoverUpload = () => {
  coverInput.value?.click()
}

const handleCoverUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    const response = await uploadApi.uploadImage(file)
    form.value.coverImage = response.data.url
  } catch (error) {
    console.error('上传失败:', error)
    alert('封面上传失败')
  }
}

const addTag = () => {
  const tag = newTag.value.trim()
  if (tag && !form.value.tags.includes(tag)) {
    form.value.tags.push(tag)
  }
  newTag.value = ''
}

const removeTag = (tag) => {
  form.value.tags = form.value.tags.filter(t => t !== tag)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.article-editor {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.editor-header h1 {
  font-size: 24px;
  margin: 0;
}

.editor-actions {
  display: flex;
  gap: 10px;
  align-items: center;
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
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.editor-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
}

.editor-main {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 15px;
}

.title-input {
  width: 100%;
  font-size: 24px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.category-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.cover-uploader {
  cursor: pointer;
  border: 2px dashed #ddd;
  border-radius: 8px;
  overflow: hidden;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-preview {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.cover-placeholder {
  color: #999;
}

.content-textarea {
  width: 100%;
  min-height: 400px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
}

.editor-sidebar {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.sidebar-section {
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.sidebar-section h3 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
}

.tags-input {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  background: #e0f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.tag-remove {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  padding: 0;
  font-size: 14px;
}

.tag-input {
  width: 100%;
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-top: 5px;
}

.summary-textarea, .seo-textarea {
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.stats {
  font-size: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.stat-label {
  color: #666;
}

.stat-value {
  font-weight: 500;
}

@media (max-width: 768px) {
  .editor-content {
    grid-template-columns: 1fr;
  }
}
</style>