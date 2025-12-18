<template>
  <section class="projects">
    <ParticlesBackground id="projects-particles" />
    <div class="container">
      <h2 class="section-title">我的项目</h2>
      <p class="section-subtitle">探索我的一些精选作品，每个项目都体现了我的技能和创造力</p>
      
      <!-- 过滤器 -->
      <div class="filter-tabs">
        <button 
          v-for="category in categories" 
          :key="category"
          @click="activeFilter = category"
          :class="['filter-btn', { active: activeFilter === category }]"
        >
          {{ category }}
        </button>
      </div>
      
      <!-- 项目网格 -->
      <div class="project-grid">
        <div 
          v-for="(project, index) in filteredProjects" 
          :key="project.id"
          class="project-wrapper"
          :ref="el => { if (el) projectRefs[index] = el }"
        >
          <ProjectCard 
            :project="project"
            :index="project.id"
            @click="openProjectModal(project)"
          />
        </div>
      </div>
      
      <!-- 项目详情模态框 -->
      <div v-if="selectedProject" class="project-modal" @click.self="closeModal">
        <div class="modal-content">
          <button class="modal-close" @click="closeModal">×</button>
          <div class="modal-header">
            <img :src="selectedProject.image" :alt="selectedProject.title" class="modal-image" />
            <div class="modal-header-content">
              <h2 class="modal-title">{{ selectedProject.title }}</h2>
              <div class="modal-tags">
                <span v-for="tag in selectedProject.tags" :key="tag" class="modal-tag">{{ tag }}</span>
              </div>
            </div>
          </div>
          <div class="modal-body">
            <div class="modal-section">
              <h3 class="modal-section-title">项目描述</h3>
              <p class="modal-description">{{ selectedProject.description }}</p>
            </div>
            <div class="modal-section">
              <h3 class="modal-section-title">技术栈</h3>
              <div class="tech-stack">
                <span v-for="tag in selectedProject.tags" :key="tag" class="tech-item">{{ tag }}</span>
              </div>
            </div>
            <div class="modal-section">
              <h3 class="modal-section-title">项目亮点</h3>
              <ul class="highlights-list">
                <li v-for="(highlight, idx) in getProjectHighlights(selectedProject)" :key="idx">
                  {{ highlight }}
                </li>
              </ul>
            </div>
            <div class="modal-actions">
              <a :href="selectedProject.link" target="_blank" rel="noopener" class="btn btn-primary">
                查看项目
                <span class="btn-arrow">→</span>
              </a>
              <a v-if="selectedProject.github" :href="selectedProject.github" target="_blank" rel="noopener" class="btn btn-outline">
                GitHub
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ProjectCard from '../components/ProjectCard.vue'
import ParticlesBackground from '../components/ParticlesBackground.vue'
import { projectApi } from '../utils/api'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const activeFilter = ref('全部')
const selectedProject = ref(null)
const projectRefs = ref([])
const projects = ref([])
const loading = ref(true)

const categories = ['全部', '前端', '全栈', '移动端', '设计']

// 备用数据（API失败时使用）
const fallbackProjects = [
  {
    id: 1,
    title: '电商网站',
    description: '使用 Vue 3 和 Tailwind CSS 构建的响应式电商平台，支持购物车、支付集成和用户管理。',
    image: 'https://via.placeholder.com/400x250?text=E-Commerce',
    category: '前端',
    tags: ['Vue 3', 'Tailwind CSS', 'Pinia'],
    link: '#',
    github: '#'
  },
  {
    id: 2,
    title: '博客平台',
    description: 'Node.js + Express 后端，支持 Markdown 编辑、评论系统和SEO优化。',
    image: 'https://via.placeholder.com/400x250?text=Blog+Platform',
    category: '全栈',
    tags: ['Node.js', 'Express', 'MongoDB'],
    link: '#',
    github: '#'
  },
  {
    id: 3,
    title: '移动记账 App',
    description: 'React Native 开发，支持数据同步、图表分析和多币种管理。',
    image: 'https://via.placeholder.com/400x250?text=Budget+App',
    category: '移动端',
    tags: ['React Native', 'Firebase', 'Chart.js'],
    link: '#',
    github: '#'
  },
  {
    id: 4,
    title: '设计系统',
    description: '完整的设计系统，包含组件库、设计规范和文档。',
    image: 'https://via.placeholder.com/400x250?text=Design+System',
    category: '设计',
    tags: ['Figma', 'Storybook', 'Design Tokens'],
    link: '#',
    github: '#'
  },
  {
    id: 5,
    title: '实时聊天应用',
    description: '使用 Socket.io 构建的实时聊天应用，支持群组、私聊和文件分享。',
    image: 'https://via.placeholder.com/400x250?text=Chat+App',
    category: '全栈',
    tags: ['Socket.io', 'React', 'Node.js'],
    link: '#',
    github: '#'
  },
  {
    id: 6,
    title: '数据可视化仪表板',
    description: '交互式数据可视化平台，支持多种图表类型和数据导出。',
    image: 'https://via.placeholder.com/400x250?text=Dashboard',
    category: '前端',
    tags: ['D3.js', 'Vue 3', 'TypeScript'],
    link: '#',
    github: '#'
  }
]

const filteredProjects = computed(() => {
  if (activeFilter.value === '全部') {
    return projects.value
  }
  return projects.value.filter(p => p.category === activeFilter.value)
})

const openProjectModal = (project) => {
  selectedProject.value = project
  document.body.style.overflow = 'hidden'
}

const closeModal = () => {
  selectedProject.value = null
  document.body.style.overflow = ''
}

const getProjectHighlights = (project) => {
  const highlightsMap = {
    1: ['响应式设计，适配所有设备', '集成支付系统', '用户友好的购物体验'],
    2: ['Markdown编辑器', 'SEO优化', '评论系统'],
    3: ['数据同步', '多币种支持', '图表分析'],
    4: ['组件库', '设计规范', '完整文档'],
    5: ['实时通信', '文件分享', '群组功能'],
    6: ['多种图表类型', '数据导出', '交互式可视化']
  }
  return highlightsMap[project.id] || ['高质量代码', '优秀用户体验', '现代化设计']
}

const loadProjects = async () => {
  try {
    loading.value = true
    const data = await projectApi.getAll()
    // 转换数据格式以匹配前端
    projects.value = data.map(p => ({
      id: p.id,
      title: p.title,
      description: p.description,
      image: p.imageUrl || 'https://via.placeholder.com/400x250?text=' + encodeURIComponent(p.title),
      category: p.category || '其他',
      tags: p.tags || [],
      link: p.link || '#',
      github: p.github || '#'
    }))
  } catch (error) {
    console.error('加载项目失败:', error)
    // 使用备用数据
    projects.value = fallbackProjects
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadProjects()
  
  await new Promise(resolve => setTimeout(resolve, 100))
  gsap.from(projectRefs.value, {
    opacity: 0,
    y: 50,
    duration: 0.8,
    stagger: 0.1,
    scrollTrigger: {
      trigger: '.project-grid',
      start: 'top 80%',
      toggleActions: 'play none none none'
    }
  })
})
</script>

<style scoped>
.projects {
  position: relative;
  z-index: 1;
  padding: 8rem 0;
  background: var(--bg-primary);
  min-height: 100vh;
}

.section-subtitle {
  text-align: center;
  color: var(--text-secondary);
  font-size: 1.1rem;
  margin-bottom: 3rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.filter-tabs {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 3rem;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 0.75rem 1.5rem;
  border: 2px solid var(--border-color);
  background: var(--bg-secondary);
  color: var(--text-primary);
  border-radius: 50px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.filter-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity 0.3s;
}

.filter-btn:hover {
  border-color: var(--accent-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.filter-btn.active {
  background: var(--gradient-primary);
  color: white;
  border-color: transparent;
  box-shadow: var(--shadow-md);
}

.filter-btn.active::before {
  opacity: 1;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .projects {
    padding: 5rem 0;
  }
  
  .project-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .filter-tabs {
    gap: 0.5rem;
  }
  
  .filter-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
}

/* 项目模态框样式 */
.project-modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: var(--bg-primary);
  border-radius: 24px;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: var(--shadow-xl);
  animation: slideUp 0.3s ease;
  border: 1px solid var(--border-color);
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-close {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  font-size: 1.5rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  z-index: 10;
}

.modal-close:hover {
  background: var(--accent-primary);
  color: white;
  transform: rotate(90deg);
}

.modal-header {
  position: relative;
  border-radius: 24px 24px 0 0;
  overflow: hidden;
}

.modal-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.modal-header-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  padding: 2rem;
  color: white;
}

.modal-title {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 1rem 0;
}

.modal-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.modal-tag {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 0.375rem 0.875rem;
  border-radius: 12px;
  font-size: 0.875rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.modal-body {
  padding: 2rem;
}

.modal-section {
  margin-bottom: 2rem;
}

.modal-section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 1rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid var(--border-color);
}

.modal-description {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 1rem;
  margin: 0;
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.tech-item {
  background: var(--bg-secondary);
  color: var(--accent-primary);
  padding: 0.5rem 1rem;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 500;
  border: 1px solid var(--border-color);
  transition: all 0.3s;
}

.tech-item:hover {
  background: var(--gradient-primary);
  color: white;
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.highlights-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.highlights-list li {
  color: var(--text-secondary);
  line-height: 1.6;
  padding-left: 1.5rem;
  position: relative;
}

.highlights-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: var(--accent-primary);
  font-weight: 600;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid var(--border-color);
}

.modal-actions .btn {
  flex: 1;
}

@media (max-width: 768px) {
  .project-modal {
    padding: 1rem;
  }
  
  .modal-content {
    max-height: 95vh;
  }
  
  .modal-image {
    height: 200px;
  }
  
  .modal-header-content {
    padding: 1.5rem;
  }
  
  .modal-title {
    font-size: 1.5rem;
  }
  
  .modal-body {
    padding: 1.5rem;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
</style>
