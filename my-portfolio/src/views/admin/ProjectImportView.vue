<template>
  <div class="project-import">
    <div class="page-header">
      <h2>项目导入</h2>
      <p class="subtitle">从 GitHub 仓库或本地上传项目文件自动分析导入</p>
    </div>

    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="GitHub 导入" name="github">
        <el-form :model="githubForm" :rules="githubRules" ref="githubFormRef" label-width="120px">
          <el-form-item label="仓库地址" prop="source">
            <el-input 
              v-model="githubForm.source" 
              placeholder="https://github.com/username/repo"
              clearable
            >
              <template #prefix>
                <el-icon><Link /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="项目分类">
            <el-select v-model="githubForm.category" placeholder="选择分类" clearable>
              <el-option label="前端" value="前端" />
              <el-option label="后端" value="后端" />
              <el-option label="全栈" value="全栈" />
              <el-option label="移动端" value="移动端" />
              <el-option label="设计" value="设计" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>

          <el-form-item label="设为精选">
            <el-switch v-model="githubForm.isFeatured" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="importFromGitHub" :loading="githubLoading">
              <el-icon v-if="!githubLoading"><Upload /></el-icon>
              开始导入
            </el-button>
          </el-form-item>
        </el-form>

        <el-alert
          v-if="githubResult"
          :type="githubResult.success ? 'success' : 'error'"
          :title="githubResult.success ? '导入成功' : '导入失败'"
          :description="githubResult.message"
          show-icon
          style="margin-top: 20px"
        />
      </el-tab-pane>

      <el-tab-pane label="本地上传" name="upload">
        <el-upload
          ref="uploadRef"
          class="upload-area"
          drag
          :auto-upload="false"
          :limit="1"
          accept=".zip,.tar,.tar.gz"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
        >
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">将项目压缩包拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="upload-tip">支持 .zip, .tar, .tar.gz 格式</div>
          </template>
        </el-upload>

        <el-form :model="uploadForm" label-width="120px" style="margin-top: 20px">
          <el-form-item label="项目分类">
            <el-select v-model="uploadForm.category" placeholder="选择分类" clearable>
              <el-option label="前端" value="前端" />
              <el-option label="后端" value="后端" />
              <el-option label="全栈" value="全栈" />
              <el-option label="移动端" value="移动端" />
              <el-option label="设计" value="设计" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="importFromUpload" :loading="uploadLoading" :disabled="!selectedFile">
              <el-icon v-if="!uploadLoading"><Upload /></el-icon>
              开始分析导入
            </el-button>
          </el-form-item>
        </el-form>

        <el-alert
          v-if="uploadResult"
          :type="uploadResult.success ? 'success' : 'error'"
          :title="uploadResult.success ? '导入成功' : '导入失败'"
          :description="uploadResult.message"
          show-icon
          style="margin-top: 20px"
        />
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="showProjectDetail" title="导入的项目详情" width="600px">
      <div v-if="importedProject" class="project-detail">
        <div class="detail-item">
          <span class="label">项目名称:</span>
          <span class="value">{{ importedProject.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">描述:</span>
          <span class="value">{{ importedProject.description }}</span>
        </div>
        <div class="detail-item" v-if="importedProject.primaryLanguage">
          <span class="label">主要语言:</span>
          <el-tag>{{ importedProject.primaryLanguage }}</el-tag>
        </div>
        <div class="detail-item" v-if="importedProject.tags">
          <span class="label">技术栈:</span>
          <el-tag v-for="tag in importedProject.tags" :key="tag" type="info">{{ tag }}</el-tag>
        </div>
        <div class="detail-item" v-if="importedProject.fileCount">
          <span class="label">文件数量:</span>
          <span class="value">{{ importedProject.fileCount }}</span>
        </div>
        <div class="detail-item" v-if="importedProject.totalLines">
          <span class="label">代码行数:</span>
          <span class="value">{{ importedProject.totalLines }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showProjectDetail = false">关闭</el-button>
        <el-button type="success" @click="viewProject">查看项目</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Link, Upload, UploadFilled } from '@element-plus/icons-vue'
import { projectApi } from '@/utils/api'

const router = useRouter()

const activeTab = ref('github')
const githubLoading = ref(false)
const uploadLoading = ref(false)
const githubFormRef = ref(null)
const uploadRef = ref(null)
const selectedFile = ref(null)

const githubForm = reactive({
  source: '',
  category: '',
  isFeatured: false
})

const uploadForm = reactive({
  category: ''
})

const githubRules = {
  source: [
    { required: true, message: '请输入GitHub仓库地址', trigger: 'blur' },
    { pattern: /github\.com/i, message: '请输入有效的GitHub仓库地址', trigger: 'blur' }
  ]
}

const githubResult = ref(null)
const uploadResult = ref(null)
const showProjectDetail = ref(false)
const importedProject = ref(null)

const importFromGitHub = async () => {
  if (!githubForm.source) {
    ElMessage.warning('请输入GitHub仓库地址')
    return
  }

  githubLoading.value = true
  githubResult.value = null

  try {
    const response = await projectApi.importFromGitHub({
      source: githubForm.source,
      category: githubForm.category,
      isFeatured: githubForm.isFeatured
    })
    
    githubResult.value = {
      success: true,
      message: `项目 "${response.title}" 导入成功！`
    }
    importedProject.value = response
    showProjectDetail.value = true
    ElMessage.success('导入成功')
  } catch (error) {
    githubResult.value = {
      success: false,
      message: error.response?.data?.message || error.message || '导入失败'
    }
    ElMessage.error('导入失败: ' + (error.response?.data?.message || error.message))
  } finally {
    githubLoading.value = false
  }
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

const importFromUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }

  uploadLoading.value = true
  uploadResult.value = null

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    if (uploadForm.category) {
      formData.append('category', uploadForm.category)
    }

    const response = await projectApi.importFromUpload(formData)
    
    uploadResult.value = {
      success: true,
      message: `项目 "${response.title}" 分析导入成功！`
    }
    importedProject.value = response
    showProjectDetail.value = true
    ElMessage.success('导入成功')
    selectedFile.value = null
    uploadRef.value?.clearFiles()
  } catch (error) {
    uploadResult.value = {
      success: false,
      message: error.response?.data?.message || error.message || '分析导入失败'
    }
    ElMessage.error('导入失败: ' + (error.response?.data?.message || error.message))
  } finally {
    uploadLoading.value = false
  }
}

const viewProject = () => {
  if (importedProject.value?.id) {
    router.push({ name: 'Projects' })
  }
  showProjectDetail.value = false
}
</script>

<style scoped>
.project-import {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.upload-area {
  width: 100%;
}

.upload-icon {
  font-size: 67px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-text {
  color: #606266;
  font-size: 14px;
}

.upload-text em {
  color: #409eff;
  font-style: normal;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.project-detail {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.detail-item .label {
  width: 100px;
  color: #909399;
  flex-shrink: 0;
}

.detail-item .value {
  flex: 1;
  color: #303133;
}

.detail-item .el-tag {
  margin-right: 8px;
}
</style>