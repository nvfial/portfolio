<template>
  <div class="character-manage">
    <div class="page-header">
      <h1>角色管理</h1>
      <button class="btn-primary" @click="openCreateModal">
        <span>+</span> 添加角色
      </button>
    </div>

    <div class="characters-table" v-if="characters.length > 0">
      <table>
        <thead>
          <tr>
            <th>头像</th>
            <th>名称</th>
            <th>称号</th>
            <th>声优</th>
            <th>排序</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="char in characters" :key="char.id">
            <td>
              <img 
                :src="char.portraitUrl || '/images/placeholder.jpg'" 
                class="avatar"
                alt=""
              />
            </td>
            <td>{{ char.name }}</td>
            <td>{{ char.title || '-' }}</td>
            <td>{{ char.cv || '-' }}</td>
            <td>{{ char.sortOrder }}</td>
            <td>
              <span class="status-badge" :class="{ active: char.isActive }">
                {{ char.isActive ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <button class="btn-icon" @click="editCharacter(char)">✏️</button>
              <button class="btn-icon" @click="deleteCharacter(char.id)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="empty-state">
      <p>暂无角色，请添加第一个角色</p>
    </div>

    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑角色' : '创建角色' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>

        <form @submit.prevent="saveCharacter" class="character-form">
          <div class="form-group">
            <label>角色名称 *</label>
            <input 
              v-model="form.name" 
              type="text" 
              required 
              placeholder="输入角色名称"
            />
          </div>

          <div class="form-group">
            <label>称号/职位</label>
            <input 
              v-model="form.title" 
              type="text" 
              placeholder="例如：水之神、宰相等"
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>声优</label>
              <input 
                v-model="form.cv" 
                type="text" 
                placeholder="声优名称"
              />
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

          <div class="form-group">
            <label>简短描述</label>
            <textarea 
              v-model="form.shortDesc" 
              rows="2"
              placeholder="角色的一句话介绍"
            ></textarea>
          </div>

          <div class="form-group">
            <label>立绘图片URL</label>
            <input 
              v-model="form.portraitUrl" 
              type="url" 
              placeholder="https://..."
            />
          </div>

          <div class="form-group">
            <label>背景图URL</label>
            <input 
              v-model="form.bgImageUrl" 
              type="url" 
              placeholder="https://..."
            />
          </div>

          <div class="form-group">
            <label>全身照URL</label>
            <input 
              v-model="form.fullBodyUrl" 
              type="url" 
              placeholder="https://..."
            />
          </div>

          <div class="form-group">
            <label>场景图URL</label>
            <input 
              v-model="form.sceneUrl" 
              type="url" 
              placeholder="https://..."
            />
          </div>

          <div class="form-group">
            <label>语音URL</label>
            <input 
              v-model="form.voiceUrl" 
              type="url" 
              placeholder="https://...mp3"
            />
          </div>

          <div class="form-group">
            <label>详细介绍</label>
            <textarea 
              v-model="form.bio" 
              rows="6"
              placeholder="角色的详细介绍..."
            ></textarea>
          </div>

          <div class="form-section-title">主题配置</div>
          
          <div class="form-row">
            <div class="form-group">
              <label>主题主色</label>
              <div class="color-input-wrapper">
                <input 
                  v-model="form.themeColor" 
                  type="text" 
                  placeholder="#c9a96a"
                />
                <input 
                  v-model="form.themeColor" 
                  type="color"
                  class="color-picker"
                />
              </div>
            </div>
            <div class="form-group">
              <label>主题副色</label>
              <div class="color-input-wrapper">
                <input 
                  v-model="form.themeColorSecondary" 
                  type="text" 
                  placeholder="#8b7355"
                />
                <input 
                  v-model="form.themeColorSecondary" 
                  type="color"
                  class="color-picker"
                />
              </div>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>背景类型</label>
              <select v-model="form.backgroundType">
                <option value="gradient">渐变</option>
                <option value="image">图片</option>
                <option value="solid">纯色</option>
              </select>
            </div>
            <div class="form-group">
              <label>粒子效果</label>
              <select v-model="form.particleEffect">
                <option value="stars">星空</option>
                <option value="fireflies">萤火虫</option>
                <option value="rain">雨滴</option>
                <option value="snow">雪花</option>
                <option value="neon">霓虹</option>
                <option value="none">无</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>边框风格</label>
              <select v-model="form.frameStyle">
                <option value="classic">古典</option>
                <option value="modern">现代</option>
                <option value="minimal">简约</option>
                <option value="ornate">华丽</option>
              </select>
            </div>
            <div class="form-group">
              <label>字体风格</label>
              <select v-model="form.fontFamily">
                <option value="serif">衬线体</option>
                <option value="sans-serif">无衬线</option>
                <option value="cursive">手写体</option>
                <option value="fantasy">艺术体</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>入场动画</label>
              <select v-model="form.entranceAnimation">
                <option value="fade">淡入</option>
                <option value="slide">滑入</option>
                <option value="scale">缩放</option>
                <option value="rotate">旋转</option>
              </select>
            </div>
            <div class="form-group">
              <label>标题动画</label>
              <select v-model="form.titleAnimation">
                <option value="typewriter">打字机</option>
                <option value="fade">淡入</option>
                <option value="slide">滑入</option>
                <option value="none">无</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>主题预设</label>
              <select v-model="form.themePreset">
                <option value="golden">金色古典</option>
                <option value="silver">银色月光</option>
                <option value="blue">蓝色深海</option>
                <option value="purple">紫色星云</option>
                <option value="red">赤红烈焰</option>
                <option value="green">翠绿森林</option>
                <option value="cyan">青色水玉</option>
                <option value="pink">粉色樱花</option>
                <option value="black">暗夜星辰</option>
                <option value="white">纯白梦境</option>
                <option value="custom">自定义</option>
              </select>
            </div>
          </div>

          <div class="form-group" v-if="form.themePreset === 'custom'">
            <label>自定义主题配置 (JSON)</label>
            <textarea 
              v-model="form.customThemeConfig" 
              rows="8"
              placeholder='{"primary": "#ff0000", "particleEffect": "fireflies", "fontFamily": "cursive"}'
            ></textarea>
            <div class="config-hint">
              可用字段: primary, secondary, particleEffect, frameStyle, fontFamily, entranceAnimation, titleAnimation
            </div>
          </div>

          <div class="form-group checkbox">
            <label>
              <input 
                v-model="form.isActive" 
                type="checkbox"
              />
              启用此角色
            </label>
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
import { characterApi } from '../../utils/api'

const characters = ref([])
const showModal = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = ref({
  name: '',
  title: '',
  cv: '',
  shortDesc: '',
  portraitUrl: '',
  bgImageUrl: '',
  fullBodyUrl: '',
  sceneUrl: '',
  voiceUrl: '',
  bio: '',
  sortOrder: 0,
  isActive: true,
  themeColor: '#c9a96a',
  themeColorSecondary: '#8b7355',
  backgroundType: 'gradient',
  particleEffect: 'stars',
  frameStyle: 'classic',
  fontFamily: 'serif',
  entranceAnimation: 'fade',
  titleAnimation: 'typewriter',
  themePreset: 'golden',
  customThemeConfig: ''
})

const loadCharacters = async () => {
  try {
    const data = await characterApi.getAll()
    characters.value = data
  } catch (error) {
    ElMessage.error('加载角色失败')
  }
}

const openCreateModal = () => {
  isEditing.value = false
  editingId.value = null
  form.value = {
    name: '',
    title: '',
    cv: '',
    shortDesc: '',
    portraitUrl: '',
    bgImageUrl: '',
    fullBodyUrl: '',
    sceneUrl: '',
    voiceUrl: '',
    bio: '',
    sortOrder: 0,
    isActive: true
  }
  showModal.value = true
}

const editCharacter = (char) => {
  isEditing.value = true
  editingId.value = char.id
  form.value = { ...char }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const saveCharacter = async () => {
  try {
    if (isEditing.value) {
      await characterApi.update(editingId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await characterApi.create(form.value)
      ElMessage.success('创建成功')
    }
    closeModal()
    loadCharacters()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteCharacter = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个角色吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await characterApi.delete(id)
    ElMessage.success('删除成功')
    loadCharacters()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadCharacters()
})
</script>

<style scoped>
.character-manage {
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 1.5rem;
  color: var(--text-primary);
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: var(--accent-primary);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background: var(--accent-secondary);
}

.characters-table {
  background: var(--bg-secondary);
  border-radius: 12px;
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: var(--bg-primary);
}

th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 0.85rem;
}

td {
  padding: 1rem;
  border-top: 1px solid var(--border-color);
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-secondary);
}

.status-badge.active {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  font-size: 1rem;
  opacity: 0.7;
  transition: opacity 0.3s;
}

.btn-icon:hover {
  opacity: 1;
}

.empty-state {
  text-align: center;
  padding: 4rem;
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
  background: var(--bg-secondary);
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
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
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-secondary);
}

.character-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--text-primary);
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 0.95rem;
}

.form-group input:focus,
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-secondary {
  padding: 0.75rem 1.5rem;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-primary);
  font-weight: 500;
}

.btn-secondary:hover {
  background: var(--bg-secondary);
}

.form-section-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 1.5rem 0 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-color);
}

.form-group select {
  width: 100%;
  padding: 0.75rem;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 0.95rem;
  cursor: pointer;
}

.form-group select:focus {
  outline: none;
  border-color: var(--accent-primary);
}

.color-input-wrapper {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.color-input-wrapper input[type="text"] {
  flex: 1;
}

.color-picker {
  width: 50px;
  height: 42px;
  padding: 0;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  background: transparent;
}

.color-picker::-webkit-color-swatch-wrapper {
  padding: 2px;
}

.color-picker::-webkit-color-swatch {
  border-radius: 6px;
  border: none;
}

.config-hint {
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-top: 0.5rem;
  font-family: monospace;
}
</style>