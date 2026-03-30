<template>
  <div class="article-editor">
    <div class="editor-header">
      <h1>{{ isEdit ? '编辑文章' : '撰写新文章' }}</h1>
      <div class="editor-actions">
        <span class="status-badge" :class="currentStatus">{{ statusText }}</span>
        <button @click="saveDraft" class="btn btn-secondary" :disabled="saving">
          保存草稿
        </button>
        <button @click="submitReview" class="btn btn-primary" :disabled="saving || !canSubmit">
          提交审核
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
            :disabled="!canEdit"
          />
        </div>

        <div class="form-group">
          <select v-model="form.categoryId" class="category-select" :disabled="!canEdit">
            <option value="">选择分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.domainName }} / {{ cat.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <div class="cover-uploader" @click="triggerCoverUpload" v-if="canEdit">
            <img v-if="form.coverImage" :src="form.coverImage" alt="封面" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <span>点击上传封面图片</span>
            </div>
          </div>
          <img v-else-if="form.coverImage" :src="form.coverImage" alt="封面" class="cover-preview" />
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
            :disabled="!canEdit"
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
                <button v-if="canEdit" @click="removeTag(tag)" class="tag-remove">×</button>
              </span>
            </div>
            <input
              v-if="canEdit"
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
            :disabled="!canEdit"
          ></textarea>
        </div>

        <div class="sidebar-section">
          <h3>SEO设置</h3>
          <div class="form-group">
            <label>SEO标题</label>
            <input v-model="form.seoTitle" type="text" placeholder="SEO标题" :disabled="!canEdit" />
          </div>
          <div class="form-group">
            <label>SEO描述</label>
            <textarea v-model="form.seoDescription" placeholder="SEO描述" :disabled="!canEdit"></textarea>
          </div>
        </div>

        <div class="sidebar-section" v-if="workflow">
          <h3>工作流状态</h3>
          <div class="workflow-info">
            <div class="workflow-item">
              <span class="label">当前状态:</span>
              <span class="value">{{ workflow.statusDescription }}</span>
            </div>
            <div class="workflow-item" v-if="workflow.authorName">
              <span class="label">作者:</span>
              <span class="value">{{ workflow.authorName }}</span>
            </div>
            <div class="workflow-item" v-if="workflow.submittedAt">
              <span class="label">提交时间:</span>
              <span class="value">{{ formatDate(workflow.submittedAt) }}</span>
            </div>
            <div class="workflow-item" v-if="workflow.reviewComment">
              <span class="label">审核意见:</span>
              <span class="value review-comment">{{ workflow.reviewComment }}</span>
            </div>
          </div>

          <div class="workflow-actions" v-if="availableActions.length > 0">
            <button
              v-for="action in availableActions"
              :key="action.targetStatus"
              @click="handleWorkflowAction(action)"
              class="btn btn-sm"
              :class="getActionClass(action)"
            >
              {{ action.action }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="editor-footer">
      <span class="auto-save-status" v-if="lastSaved">
        自动保存于 {{ formatTime(lastSaved) }}
      </span>
      <span class="word-count">字数: {{ wordCount }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi, uploadApi, categoryApi } from '../../utils/auth'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const articleId = computed(() => route.params.id)
const isEdit = computed(() => !!articleId.value)

const form = ref({
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: '',
  tags: [],
  seoTitle: '',
  seoDescription: ''
})

const newTag = ref('')
const categories = ref([])
const workflow = ref(null)
const saving = ref(false)
const lastSaved = ref(null)
const coverInput = ref(null)

let autoSaveTimer = null

const currentStatus = computed(() => workflow.value?.currentStatus?.toLowerCase() || 'draft')
const statusText = computed(() => {
  const statusMap = {
    DRAFT: '草稿',
    EDITING: '编辑中',
    PENDING_REVIEW: '待审核',
    PUBLISHED: '已发布',
    REJECTED: '已驳回',
    OFFLINE: '已下线'
  }
  return statusMap[workflow.value?.currentStatus] || '草稿'
})

const canEdit = computed(() => {
  if (!authStore.isAuthenticated) return false
  const status = workflow.value?.currentStatus
  return ['DRAFT', 'EDITING', 'REJECTED'].includes(status) || !isEdit.value
})

const canSubmit = computed(() => {
  return form.value.title && form.value.content && form.value.categoryId
})

const wordCount = computed(() => {
  return form.value.content.replace(/\s/g, '').length
})

const availableActions = computed(() => {
  if (!workflow.value?.availableTransitions) return []
  return workflow.value.availableTransitions.filter(t => t.allowed)
})

onMounted(async () => {
  await loadCategories()
  
  if (isEdit.value) {
    await loadArticle()
  } else {
    workflow.value = {
      currentStatus: 'DRAFT',
      statusDescription: '草稿'
    }
  }

  autoSaveTimer = setInterval(autoSave, 120000)
})

onUnmounted(() => {
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer)
  }
})

watch(() => form.value.content, () => {
  if (isEdit.value && workflow.value?.currentStatus === 'DRAFT') {
    workflow.value.currentStatus = 'EDITING'
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
      summary: article.summary,
      coverImage: article.coverImage,
      categoryId: article.categoryId,
      tags: article.tags || [],
      seoTitle: article.seoTitle,
      seoDescription: article.seoDescription
    }

    const workflowResponse = await articleApi.getWorkflowState(articleId.value)
    workflow.value = workflowResponse
  } catch (error) {
    console.error('加载文章失败:', error)
  }
}

const saveDraft = async () => {
  saving.value = true
  try {
    const data = {
      ...form.value,
      status: workflow.value?.currentStatus === 'EDITING' ? 'EDITING' : 'DRAFT'
    }

    if (isEdit.value) {
      await articleApi.update(articleId.value, data)
    } else {
      const response = await articleApi.create(data)
      router.replace(`/admin/editor/${response.id}`)
    }

    lastSaved.value = new Date()
    await loadArticle()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  } finally {
    saving.value = false
  }
}

const autoSave = async () => {
  if (isEdit.value && form.value.title) {
    try {
      await articleApi.update(articleId.value, {
        ...form.value,
        status: 'EDITING'
      })
      lastSaved.value = new Date()
    } catch (error) {
      console.error('自动保存失败:', error)
    }
  }
}

const submitReview = async () => {
  if (!canSubmit.value) {
    alert('请填写标题、内容和分类')
    return
  }

  saving.value = true
  try {
    await saveDraft()
    await articleApi.submitForReview(articleId.value, '提交审核')
    await loadArticle()
  } catch (error) {
    console.error('提交审核失败:', error)
    alert('提交审核失败')
  } finally {
    saving.value = false
  }
}

const handleWorkflowAction = async (action) => {
  const confirmText = {
    '提交审核': '确定要提交审核吗？',
    '通过审核': '确定要通过审核吗？',
    '驳回': '确定要驳回吗？',
    '下线': '确定要下线吗？',
    '重新发布': '确定要重新发布吗？'
  }

  if (!confirm(confirmText[action.action])) return

  try {
    switch (action.targetStatus) {
      case 'PENDING_REVIEW':
        await articleApi.submitForReview(articleId.value)
        break
      case 'PUBLISHED':
        await articleApi.approve(articleId.value)
        break
      case 'REJECTED':
        const comment = prompt('请输入驳回原因:')
        if (comment) await articleApi.reject(articleId.value, comment)
        break
      case 'OFFLINE':
        await articleApi.offline(articleId.value)
        break
    }
    await loadArticle()
  } catch (error) {
    console.error('操作失败:', error)
    alert('操作失败')
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

const getActionClass = (action) => {
  const classMap = {
    '提交审核': 'btn-primary',
    '通过审核': 'btn-success',
    '驳回': 'btn-danger',
    '下线': 'btn-warning',
    '重新发布': 'btn-primary'
  }
  return classMap[action.action] || 'btn-secondary'
}

const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN')
}

const formatTime = (date) => {
  return new Date(date).toLocaleTimeString('zh-CN')
}
</script>

<style scoped>
.article-editor {
  max-width: 1400px;
  margin: 0 auto;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border-color);
}

.editor-header h1 {
  font-size: 1.5rem;
  color: var(--text-primary);
}

.editor-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
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

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-success {
  background: #10b981;
  color: white;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-warning {
  background: #f59e0b;
  color: white;
}

.editor-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 2rem;
}

.editor-main {
  background: var(--bg-secondary);
  border-radius: 1rem;
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.title-input {
  width: 100%;
  padding: 0.75rem;
  font-size: 1.25rem;
  border: 2px solid var(--border-color);
  border-radius: 0.5rem;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.title-input:focus {
  outline: none;
  border-color: #667eea;
}

.category-select {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid var(--border-color);
  border-radius: 0.5rem;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.cover-uploader {
  width: 100%;
  height: 200px;
  border: 2px dashed var(--border-color);
  border-radius: 0.5rem;
  cursor: pointer;
  overflow: hidden;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.content-textarea {
  width: 100%;
  min-height: 500px;
  padding: 1rem;
  font-size: 1rem;
  line-height: 1.8;
  border: 2px solid var(--border-color);
  border-radius: 0.5rem;
  background: var(--bg-primary);
  color: var(--text-primary);
  resize: vertical;
}

.editor-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.sidebar-section {
  background: var(--bg-secondary);
  border-radius: 1rem;
  padding: 1rem;
}

.sidebar-section h3 {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-bottom: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.tags-input {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  width: 100%;
}

.tag {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  background: #667eea;
  color: white;
  border-radius: 0.25rem;
  font-size: 0.875rem;
}

.tag-remove {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  line-height: 1;
}

.tag-input {
  flex: 1;
  min-width: 100px;
  padding: 0.25rem 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 0.25rem;
  font-size: 0.875rem;
}

.summary-textarea {
  width: 100%;
  min-height: 80px;
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 0.25rem;
  resize: vertical;
}

.workflow-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.workflow-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.workflow-item .label {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.workflow-item .value {
  font-size: 0.875rem;
  color: var(--text-primary);
}

.review-comment {
  color: #dc2626;
  font-style: italic;
}

.workflow-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 1rem;
}

.btn-sm {
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
}

.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.auto-save-status {
  color: #10b981;
}

@media (max-width: 1024px) {
  .editor-content {
    grid-template-columns: 1fr;
  }
}
</style>