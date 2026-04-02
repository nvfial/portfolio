<template>
  <div class="resource-manage-page">
    <div class="page-header">
      <h1 class="page-title">资源管理</h1>
      <button class="btn-primary" @click="openCreateModal">
        <span>+</span> 上传资源
      </button>
    </div>
    
    <div class="filters-bar">
      <input 
        v-model="searchKeyword" 
        type="text" 
        placeholder="搜索资源..."
        class="search-input"
        @input="handleSearch"
      />
      <select v-model="filterType" @change="loadResources" class="filter-select">
        <option value="">全部类型</option>
        <option value="AUDIO">音频</option>
        <option value="VIDEO">视频</option>
        <option value="DOCUMENT">文档</option>
        <option value="IMAGE">图片</option>
        <option value="ARCHIVE">压缩包</option>
        <option value="SOFTWARE">软件</option>
      </select>
    </div>
    
    <div class="resource-table">
      <table>
        <thead>
          <tr>
            <th>缩略图</th>
            <th>名称</th>
            <th>类型</th>
            <th>大小</th>
            <th>下载量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in resources" :key="item.id">
            <td>
              <img 
                :src="item.thumbnailUrl || '/images/resource-default.jpg'" 
                class="table-thumb"
                alt=""
              />
            </td>
            <td class="name-cell">{{ item.name }}</td>
            <td>
              <span class="type-badge">{{ item.type }}</span>
            </td>
            <td>{{ formatSize(item.fileSize) }}</td>
            <td>{{ item.downloadCount || 0 }}</td>
            <td>
              <span class="status-badge" :class="item.isActive ? 'active' : 'inactive'">
                {{ item.isActive ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <button class="btn-icon" @click="editResource(item)">✏️</button>
              <button class="btn-icon danger" @click="deleteResource(item.id)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="empty-state" v-if="resources.length === 0">
        <p>暂无资源数据</p>
      </div>
    </div>
    
    <div class="pagination" v-if="totalPages > 1">
      <button 
        class="page-btn" 
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
      <button 
        class="page-btn"
        :disabled="currentPage >= totalPages - 1"
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>
    
    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑资源' : '上传资源' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <form @submit.prevent="saveResource" class="resource-form">
          <div class="form-group">
            <label>资源名称 *</label>
            <input 
              v-model="form.name" 
              type="text" 
              required
              placeholder="请输入资源名称"
            />
          </div>
          
          <div class="form-group">
            <label>资源描述</label>
            <textarea 
              v-model="form.description" 
              rows="3"
              placeholder="资源描述信息..."
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>资源类型 *</label>
              <select v-model="form.type" required>
                <option value="">选择类型</option>
                <option value="AUDIO">音频</option>
                <option value="VIDEO">视频</option>
                <option value="DOCUMENT">文档</option>
                <option value="IMAGE">图片</option>
                <option value="ARCHIVE">压缩包</option>
                <option value="SOFTWARE">软件</option>
                <option value="OTHER">其他</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>分类</label>
              <select v-model="form.categoryId">
                <option :value="null">无分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>
          </div>
          
          <div class="form-group" v-if="!isEditing">
            <label>上传文件 *</label>
            <div class="file-upload">
              <input 
                type="file" 
                @change="handleFileSelect"
                accept="*/*"
              />
              <div class="upload-area" v-if="!selectedFile">
                <span class="upload-icon">📁</span>
                <span>点击或拖拽文件到此处</span>
              </div>
              <div class="selected-file" v-else>
                <span class="file-name">{{ selectedFile.name }}</span>
                <span class="file-size">{{ formatSize(selectedFile.size) }}</span>
              </div>
            </div>
          </div>
          
          <div class="form-group" v-else>
            <label>替换文件（可选）</label>
            <input 
              type="file" 
              @change="handleFileSelect"
              accept="*/*"
            />
          </div>
          
          <div class="form-row">
            <div class="form-group checkbox">
              <label>
                <input 
                  v-model="form.isActive" 
                  type="checkbox"
                />
                启用此资源
              </label>
            </div>
            
            <div class="form-group">
              <label>排序</label>
              <input 
                v-model.number="form.sortOrder" 
                type="number"
                min="0"
              />
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="closeModal">
              取消
            </button>
            <button type="submit" class="btn-primary">
              {{ isEditing ? '保存' : '上传' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { resourceApi, resourceCategoryApi } from '../../utils/api'

const resources = ref([])
const categories = ref([])
const searchKeyword = ref('')
const filterType = ref('')
const currentPage = ref(0)
const pageSize = ref(20)
const totalElements = ref(0)

const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const selectedFile = ref(null)

const form = ref({
  name: '',
  description: '',
  type: '',
  categoryId: null,
  isActive: true,
  sortOrder: 0
})

const totalPages = computed(() => Math.ceil(totalElements.value / pageSize.value))

const loadResources = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      type: filterType.value || undefined,
      keyword: searchKeyword.value || undefined
    }
    const data = await resourceApi.getAll(params)
    resources.value = data.content
    totalElements.value = data.totalElements
  } catch (error) {
    ElMessage.error('加载资源失败')
  }
}

const loadCategories = async () => {
  try {
    const data = await resourceCategoryApi.getAll()
    categories.value = data
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 0
  loadResources()
}

const changePage = (page) => {
  currentPage.value = page
  loadResources()
}

const openCreateModal = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    name: '',
    description: '',
    type: '',
    categoryId: null,
    isActive: true,
    sortOrder: 0
  }
  selectedFile.value = null
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedFile.value = null
}

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    selectedFile.value = file
  }
}

const editResource = (item) => {
  isEditing.value = true
  editingId.value = item.id
  form.value = {
    name: item.name,
    description: item.description,
    type: item.type,
    categoryId: item.categoryId,
    isActive: item.isActive,
    sortOrder: item.sortOrder
  }
  selectedFile.value = null
  showModal.value = true
}

const saveResource = async () => {
  try {
    const formData = new FormData()
    formData.append('data', JSON.stringify(form.value))
    
    if (selectedFile.value) {
      formData.append('file', selectedFile.value)
    }
    
    if (isEditing.value) {
      await resourceApi.update(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await resourceApi.create(formData)
      ElMessage.success('上传成功')
    }
    
    closeModal()
    loadResources()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteResource = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个资源吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await resourceApi.delete(id)
    ElMessage.success('删除成功')
    loadResources()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  while (bytes >= 1024 && i < units.length - 1) {
    bytes /= 1024
    i++
  }
  return bytes.toFixed(1) + ' ' + units[i]
}

import { computed } from 'vue'

onMounted(() => {
  loadResources()
  loadCategories()
})
</script>

<style scoped>
.resource-manage-page {
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 1.5rem;
  color: var(--text-primary);
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #c9a96a, #8b7355);
  border: none;
  border-radius: 8px;
  color: #0a0a0f;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(201, 169, 106, 0.3);
}

.filters-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-input {
  flex: 1;
  max-width: 400px;
  padding: 0.75rem 1rem;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
}

.filter-select {
  padding: 0.75rem 1rem;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  cursor: pointer;
}

.resource-table {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;
}

.resource-table table {
  width: 100%;
  border-collapse: collapse;
}

.resource-table th,
.resource-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.resource-table th {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 0.85rem;
}

.resource-table td {
  color: var(--text-primary);
}

.table-thumb {
  width: 60px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.name-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.type-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  background: rgba(201, 169, 106, 0.1);
  border-radius: 4px;
  font-size: 0.8rem;
  color: #c9a96a;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.status-badge.active {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.status-badge.inactive {
  background: rgba(149, 165, 166, 0.1);
  color: #95a5a6;
}

.btn-icon {
  padding: 0.5rem;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.btn-icon.danger:hover {
  color: #e74c3c;
}

.empty-state {
  padding: 3rem;
  text-align: center;
  color: var(--text-secondary);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-btn {
  padding: 0.5rem 1.5rem;
  background: rgba(201, 169, 106, 0.1);
  border: 1px solid rgba(201, 169, 106, 0.3);
  border-radius: 8px;
  color: #c9a96a;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background: rgba(201, 169, 106, 0.2);
}

.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-secondary);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  font-size: 1.25rem;
  color: var(--text-primary);
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 1.5rem;
  color: var(--text-secondary);
  cursor: pointer;
}

.resource-form {
  padding: 1.5rem;
  max-height: 70vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
  font-size: 0.9rem;
}

.form-group input[type="text"],
.form-group input[type="number"],
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 0.95rem;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--accent-primary);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group.checkbox label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.file-upload {
  position: relative;
}

.file-upload input[type="file"] {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  border: 2px dashed var(--border-color);
  border-radius: 8px;
  color: var(--text-secondary);
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #c9a96a;
}

.upload-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.selected-file {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: var(--bg-secondary);
  border: 1px solid rgba(201, 169, 106, 0.3);
  border-radius: 8px;
}

.file-name {
  color: var(--text-primary);
}

.file-size {
  color: var(--text-secondary);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
}

.btn-secondary {
  padding: 0.75rem 1.5rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: var(--bg-tertiary);
}
</style>