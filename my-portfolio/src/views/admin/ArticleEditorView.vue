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
          <div class="md-editor">
            <div class="md-toolbar">
              <button type="button" @click="insertMarkdown('**', '**')" title="加粗">
                <strong>B</strong>
              </button>
              <button type="button" @click="insertMarkdown('*', '*')" title="斜体">
                <em>I</em>
              </button>
              <button type="button" @click="insertMarkdown('~~', '~~')" title="删除线">
                <s>S</s>
              </button>
              <span class="toolbar-divider"></span>
              <button type="button" @click="insertMarkdown('# ', '')" title="标题1">
                H1
              </button>
              <button type="button" @click="insertMarkdown('## ', '')" title="标题2">
                H2
              </button>
              <button type="button" @click="insertMarkdown('### ', '')" title="标题3">
                H3
              </button>
              <span class="toolbar-divider"></span>
              <button type="button" @click="insertMarkdown('- ', '')" title="无序列表">
                •
              </button>
              <button type="button" @click="insertMarkdown('1. ', '')" title="有序列表">
                1.
              </button>
              <button type="button" @click="insertMarkdown('- [ ] ', '')" title="任务列表">
                ☑
              </button>
              <span class="toolbar-divider"></span>
              <button type="button" @click="insertMarkdown('[', '](url)')" title="链接">
                🔗
              </button>
              <button type="button" @click="insertMarkdown('![alt](', ')')" title="图片">
                🖼️
              </button>
              <button type="button" @click="insertMarkdown('> ', '')" title="引用">
                "
              </button>
              <button type="button" @click="insertMarkdown('```\n', '\n```')" title="代码块">
                &lt;/&gt;
              </button>
              <button type="button" @click="insertMarkdown('`', '`')" title="行内代码">
                `
              </button>
              <span class="toolbar-divider"></span>
              <button type="button" @click="insertMarkdown('---\n', '')" title="分隔线">
                —
              </button>
              <button type="button" @click="insertMarkdown('| 列1 | 列2 |\n| --- | --- |\n| 内容 | 内容 |', '')" title="表格">
                ⊞
              </button>
            </div>
            
            <div class="md-editor-body">
              <textarea
                v-model="form.content"
                placeholder="撰写文章内容... (支持 Markdown 语法)"
                class="content-textarea"
                ref="contentTextarea"
              ></textarea>
              
              <div v-if="showPreview" class="md-preview">
                <div class="preview-header">
                  <span>预览</span>
                  <button type="button" @click="showPreview = false">✕</button>
                </div>
                <div class="preview-content markdown-body" v-html="previewHtml"></div>
              </div>
            </div>
            
            <div class="md-footer">
              <span class="word-count">{{ wordCount }} 字</span>
              <button type="button" class="preview-toggle" @click="showPreview = !showPreview">
                {{ showPreview ? '隐藏预览' : '显示预览' }}
              </button>
            </div>
          </div>
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
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { articleApi, categoryApi, uploadApi } from '../../utils/auth'
import { marked } from 'marked'

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
const contentTextarea = ref(null)
const showPreview = ref(false)

const canPublish = computed(() => {
  return form.value.title && form.value.content && form.value.categoryId
})

const wordCount = computed(() => {
  return form.value.content.replace(/\s/g, '').length
})

const previewHtml = computed(() => {
  return marked(form.value.content)
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

const insertMarkdown = (before, after) => {
  if (!contentTextarea.value) return
  
  const textarea = contentTextarea.value
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = form.value.content.substring(start, end)
  
  const newText = before + selectedText + after
  form.value.content = 
    form.value.content.substring(0, start) + 
    newText + 
    form.value.content.substring(end)
  
  nextTick(() => {
    textarea.focus()
    const newCursorPos = start + before.length + selectedText.length
    textarea.setSelectionRange(newCursorPos, newCursorPos)
  })
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

.md-editor {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  background: white;
}

.md-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
  padding: 12px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.md-toolbar button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  height: 32px;
  padding: 0 8px;
  border: none;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: all 0.2s;
}

.md-toolbar button:hover {
  background: #667eea;
  color: white;
}

.md-toolbar .toolbar-divider {
  width: 1px;
  height: 24px;
  background: #ddd;
  margin: 0 8px;
}

.md-editor-body {
  display: flex;
  min-height: 400px;
}

.md-editor-body .content-textarea {
  flex: 1;
  border: none;
  padding: 20px;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.8;
  resize: vertical;
  min-height: 400px;
}

.md-editor-body .content-textarea:focus {
  outline: none;
}

.md-preview {
  width: 50%;
  border-left: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid #e0e0e0;
  font-weight: 600;
  color: #666;
}

.preview-header button {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #999;
  padding: 4px 8px;
}

.preview-header button:hover {
  color: #333;
}

.preview-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.md-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-top: 1px solid #e0e0e0;
}

.word-count {
  font-size: 13px;
  color: #666;
}

.preview-toggle {
  padding: 8px 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  color: #666;
  transition: all 0.2s;
}

.preview-toggle:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.md-editor .markdown-body {
  font-size: 14px;
  line-height: 1.7;
}

.md-editor .markdown-body h1,
.md-editor .markdown-body h2,
.md-editor .markdown-body h3 {
  margin-top: 20px;
  margin-bottom: 12px;
}

.md-editor .markdown-body code {
  padding: 2px 6px;
  background: #f0f0f0;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
  font-size: 13px;
}

.md-editor .markdown-body pre {
  background: #282c34;
  color: #abb2bf;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
}

.md-editor .markdown-body pre code {
  background: none;
  padding: 0;
  color: inherit;
}

.md-editor .markdown-body blockquote {
  border-left: 4px solid #667eea;
  background: #f8f9fa;
  padding: 12px 16px;
  margin: 12px 0;
}

.md-editor .markdown-body img {
  max-width: 100%;
  border-radius: 8px;
}
</style>