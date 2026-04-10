<template>
  <div class="category-manage-page">
    <div class="page-header">
      <h1 class="page-title">资源分类管理</h1>
      <button class="btn-primary" @click="openCreateModal">
        <span>+</span> 新增分类
      </button>
    </div>
    
    <div class="category-table">
      <table>
        <thead>
          <tr>
            <th>排序</th>
            <th>图标</th>
            <th>名称</th>
            <th>描述</th>
            <th>资源数</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in categories" :key="item.id">
            <td>{{ item.sortOrder }}</td>
            <td class="icon-cell">{{ item.icon || '📂' }}</td>
            <td class="name-cell">{{ item.name }}</td>
            <td class="desc-cell">{{ item.description || '-' }}</td>
            <td>{{ item.resourceCount || 0 }}</td>
            <td>
              <span class="status-badge" :class="item.isActive ? 'active' : 'inactive'">
                {{ item.isActive ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <button class="btn-icon" @click="editCategory(item)">✏️</button>
              <button class="btn-icon danger" @click="deleteCategory(item.id)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="empty-state" v-if="categories.length === 0">
        <p>暂无分类数据</p>
      </div>
    </div>
    
    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑分类' : '新增分类' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <form @submit.prevent="saveCategory" class="category-form">
          <div class="form-group">
            <label>分类名称 *</label>
            <input 
              v-model="form.name" 
              type="text" 
              required
              placeholder="请输入分类名称"
            />
          </div>
          
          <div class="form-group">
            <label>图标</label>
            <div class="icon-select">
              <span 
                v-for="icon in iconOptions" 
                :key="icon"
                class="icon-option"
                :class="{ active: form.icon === icon }"
                @click="form.icon = icon"
              >
                {{ icon }}
              </span>
            </div>
            <input 
              v-model="form.icon" 
              type="text" 
              placeholder="或输入自定义图标"
              class="icon-input"
            />
          </div>
          
          <div class="form-group">
            <label>分类描述</label>
            <textarea 
              v-model="form.description" 
              rows="3"
              placeholder="分类描述信息..."
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>排序值</label>
              <input 
                v-model.number="form.sortOrder" 
                type="number"
                min="0"
              />
              <p class="form-hint">数值越小越靠前</p>
            </div>
            
            <div class="form-group">
              <label>状态</label>
              <select v-model="form.isActive">
                <option :value="true">启用</option>
                <option :value="false">禁用</option>
              </select>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="closeModal">
              取消
            </button>
            <button type="submit" class="btn-primary">
              {{ isEditing ? '保存' : '创建' }}
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
import { resourceCategoryApi } from '../../utils/api'

const categories = ref([])
const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = ref({
  name: '',
  icon: '📂',
  description: '',
  sortOrder: 0,
  isActive: true
})

const iconOptions = [
  '📁', '📂', '🎵', '🎬', '📄', '🖼️', '📦', '💿',
  '📚', '🎮', '🛠️', '🔧', '📱', '💻', '🌐', '☁️'
]

const loadCategories = async () => {
  try {
    const data = await resourceCategoryApi.getAll()
    categories.value = data
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

const openCreateModal = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    name: '',
    icon: '📂',
    description: '',
    sortOrder: categories.value.length,
    isActive: true
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const editCategory = (item) => {
  isEditing.value = true
  editingId.value = item.id
  form.value = {
    name: item.name,
    icon: item.icon || '📂',
    description: item.description,
    sortOrder: item.sortOrder,
    isActive: item.isActive
  }
  showModal.value = true
}

const saveCategory = async () => {
  try {
    if (isEditing.value) {
      await resourceCategoryApi.update(editingId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await resourceCategoryApi.create(form.value)
      ElMessage.success('创建成功')
    }
    
    closeModal()
    loadCategories()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteCategory = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await resourceCategoryApi.delete(id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-manage-page {
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

.category-table {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;
}

.category-table table {
  width: 100%;
  border-collapse: collapse;
}

.category-table th,
.category-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.category-table th {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 0.85rem;
}

.category-table td {
  color: var(--text-primary);
}

.icon-cell {
  font-size: 1.5rem;
}

.name-cell {
  font-weight: 500;
}

.desc-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-secondary);
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
  max-width: 500px;
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

.category-form {
  padding: 1.5rem;
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

.form-hint {
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.icon-select {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.icon-option {
  width: 2.5rem;
  height: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-option:hover {
  border-color: #c9a96a;
}

.icon-option.active {
  border-color: #c9a96a;
  background: rgba(201, 169, 106, 0.1);
}

.icon-input {
  margin-top: 0.5rem;
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