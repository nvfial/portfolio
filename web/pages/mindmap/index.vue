<template>
  <div class="mindmap-page">
    <ParticleField :particle-count="50" :connection-distance="50" />
    
    <header class="page-header">
      <div class="container">
        <h1 class="page-title">思维图谱</h1>
        <p class="page-subtitle">可视化知识网络</p>
      </div>
    </header>
    
    <div class="container">
      <div class="mindmap-controls">
        <div class="domain-selector">
          <button 
            v-for="domain in domains" 
            :key="domain.slug"
            class="domain-btn"
            :class="{ active: activeDomain === domain.slug }"
            @click="activeDomain = domain.slug"
          >
            <span class="btn-icon">{{ domain.icon }}</span>
            {{ domain.name }}
          </button>
        </div>
        
        <div class="view-controls">
          <button 
            class="control-btn" 
            :class="{ active: viewMode === '2d' }"
            @click="viewMode = '2d'"
          >
            2D视图
          </button>
          <button 
            class="control-btn" 
            :class="{ active: viewMode === '3d' }"
            @click="viewMode = '3d'"
          >
            3D视图
          </button>
        </div>
      </div>
      
      <div class="mindmap-container">
        <ClientOnly>
          <NodeNetwork 
            v-if="viewMode === '2d'"
            :data="graphData"
            :show-labels="true"
            :show-flow-particles="true"
            @node-click="handleNodeClick"
          />
          <NeuralNetwork 
            v-else
            :node-count="80"
            :connection-distance="25"
            :interactive="true"
            @node-click="handleNodeClick"
          />
        </ClientOnly>
      </div>
      
      <div class="mindmap-legend">
        <div class="legend-item">
          <span class="legend-dot" style="background: #667eea"></span>
          <span>领域</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot" style="background: #764ba2"></span>
          <span>分类</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot" style="background: #f093fb"></span>
          <span>关键词</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot" style="background: #4facfe"></span>
          <span>参考</span>
        </div>
      </div>
    </div>
    
    <Teleport to="body">
      <div v-if="selectedNode" class="node-detail-panel" @click="selectedNode = null">
        <div class="panel-content" @click.stop>
          <button class="panel-close" @click="selectedNode = null">×</button>
          
          <div class="panel-header">
            <span class="node-type">{{ getNodeTypeLabel(selectedNode.type) }}</span>
            <h2>{{ selectedNode.label }}</h2>
          </div>
          
          <div class="panel-body">
            <p class="node-description">
              {{ selectedNode.description || '点击查看该节点的详细信息和相关文章' }}
            </p>
            
            <div class="related-nodes">
              <h4>相关节点</h4>
              <div class="related-list">
                <button 
                  v-for="node in getRelatedNodes(selectedNode)" 
                  :key="node.id"
                  class="related-item"
                  @click="selectRelatedNode(node)"
                >
                  {{ node.label }}
                </button>
              </div>
            </div>
            
            <button class="btn btn-primary panel-action">
              查看详细内容 →
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ParticleField from '~/components/art/ParticleField.vue'
import NodeNetwork from '~/components/graph/NodeNetwork.vue'
import NeuralNetwork from '~/components/art/NeuralNetwork.vue'

const activeDomain = ref('frontend')
const viewMode = ref('2d')
const selectedNode = ref(null)

const domains = [
  { id: 1, name: '前端开发', slug: 'frontend', icon: '⚛️' },
  { id: 2, name: '后端架构', slug: 'backend', icon: '🚀' },
  { id: 3, name: '人工智能', slug: 'ai', icon: '🤖' },
  { id: 4, name: '设计艺术', slug: 'design', icon: '🎭' }
]

const allNodes = ref([
  { id: 'd1', label: '前端开发', type: 'domain', x: 0, y: 0 },
  { id: 'c1', label: 'Vue.js', type: 'category', x: -150, y: 100 },
  { id: 'c2', label: 'React', type: 'category', x: 150, y: 100 },
  { id: 'c3', label: 'TypeScript', type: 'category', x: 0, y: 200 },
  { id: 'k1', label: '组件化', type: 'keyword', x: -250, y: 180 },
  { id: 'k2', label: '响应式', type: 'keyword', x: -50, y: 180 },
  { id: 'k3', label: '虚拟DOM', type: 'keyword', x: 250, y: 180 },
  { id: 'k4', label: '类型系统', type: 'keyword', x: 50, y: 280 },
  
  { id: 'd2', label: '后端架构', type: 'domain', x: 300, y: 0 },
  { id: 'c4', label: 'Spring Boot', type: 'category', x: 200, y: 100 },
  { id: 'c5', label: 'Node.js', type: 'category', x: 400, y: 100 },
  
  { id: 'd3', label: '人工智能', type: 'domain', x: 600, y: 0 },
  { id: 'c6', label: '机器学习', type: 'category', x: 500, y: 100 },
  { id: 'c7', label: 'LLM', type: 'category', x: 700, y: 100 }
])

const allLinks = ref([
  { source: 'd1', target: 'c1' },
  { source: 'd1', target: 'c2' },
  { source: 'd1', target: 'c3' },
  { source: 'c1', target: 'k1' },
  { source: 'c1', target: 'k2' },
  { source: 'c2', target: 'k3' },
  { source: 'c3', target: 'k4' },
  { source: 'd2', target: 'c4' },
  { source: 'd2', target: 'c5' },
  { source: 'd3', target: 'c6' },
  { source: 'd3', target: 'c7' }
])

const graphData = computed(() => {
  return {
    nodes: allNodes.value,
    links: allLinks.value
  }
})

const getNodeTypeLabel = (type) => {
  const labels = { domain: '领域', category: '分类', keyword: '关键词', reference: '参考' }
  return labels[type] || '节点'
}

const handleNodeClick = (node) => {
  selectedNode.value = node
}

const getRelatedNodes = (node) => {
  return allNodes.value.filter(n => {
    return allLinks.value.some(l => 
      (l.source === node.id && l.target === n.id) ||
      (l.target === node.id && l.source === n.id)
    )
  }).slice(0, 5)
}

const selectRelatedNode = (node) => {
  selectedNode.value = node
}
</script>

<style scoped>
.mindmap-page {
  position: relative;
  min-height: 100vh;
  padding-top: 80px;
}

.page-header {
  text-align: center;
  padding: 4rem 0;
}

.page-title {
  font-size: clamp(2.5rem, 6vw, 4rem);
  font-weight: 700;
  background: var(--gradient-cosmic);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 1rem;
}

.page-subtitle {
  font-size: 1.25rem;
  color: var(--text-secondary);
}

.mindmap-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.domain-selector {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.domain-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: var(--radius-full);
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  font-size: 0.9rem;
  transition: all var(--transition-fast);
}

.domain-btn:hover {
  border-color: var(--accent-primary);
}

.domain-btn.active {
  background: var(--gradient-primary);
  border-color: transparent;
  color: white;
}

.view-controls {
  display: flex;
  gap: 0.5rem;
}

.control-btn {
  padding: 0.5rem 1rem;
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  font-size: 0.85rem;
  transition: all var(--transition-fast);
}

.control-btn.active {
  background: var(--accent-primary);
  border-color: var(--accent-primary);
  color: white;
}

.mindmap-container {
  height: 600px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.mindmap-legend {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 2rem;
  padding: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.node-detail-panel {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: flex-end;
  z-index: 9999;
}

.panel-content {
  width: 400px;
  max-width: 90vw;
  height: 100%;
  background: var(--bg-secondary);
  padding: 2rem;
  position: relative;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from { transform: translateX(100%); }
  to { transform: translateX(0); }
}

.panel-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  font-size: 1.5rem;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--bg-tertiary);
}

.panel-header {
  margin-bottom: 2rem;
}

.node-type {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
  background: var(--accent-primary);
  color: white;
  border-radius: var(--radius-full);
  margin-bottom: 0.5rem;
}

.panel-header h2 {
  font-size: 1.5rem;
}

.node-description {
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 2rem;
}

.related-nodes h4 {
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.related-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 2rem;
}

.related-item {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  border-radius: var(--radius-full);
  background: var(--bg-tertiary);
  transition: all var(--transition-fast);
}

.related-item:hover {
  background: var(--accent-primary);
  color: white;
}

.panel-action {
  width: 100%;
}
</style>
