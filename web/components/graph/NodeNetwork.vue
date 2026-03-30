<template>
  <div class="flow-network" ref="containerRef">
    <svg ref="svgRef" :width="width" :height="height">
      <defs>
        <linearGradient id="flowGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#667eea" stop-opacity="0.8" />
          <stop offset="100%" stop-color="#764ba2" stop-opacity="0.8" />
        </linearGradient>
        <filter id="glow">
          <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
          <feMerge>
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
      
      <g class="links">
        <path
          v-for="(link, index) in displayLinks"
          :key="`link-${index}`"
          :d="link.path"
          class="network-link"
          :stroke-opacity="link.opacity"
        />
      </g>
      
      <g class="nodes">
        <g
          v-for="node in nodes"
          :key="node.id"
          :transform="`translate(${node.x}, ${node.y})`"
          class="node-group"
          @mouseenter="handleNodeHover(node)"
          @mouseleave="handleNodeLeave"
          @click="handleNodeClick(node)"
        >
          <circle
            :r="node.size || 8"
            :fill="getNodeColor(node.type)"
            filter="url(#glow)"
            class="node-circle"
          />
          <text
            v-if="showLabels"
            :dy="(node.size || 8) + 15"
            class="node-label"
          >
            {{ node.label }}
          </text>
        </g>
      </g>
      
      <g v-if="showFlowParticles" class="flow-particles">
        <circle
          v-for="(particle, index) in flowParticles"
          :key="`particle-${index}`"
          :cx="particle.x"
          :cy="particle.y"
          r="3"
          fill="#fff"
          class="flow-particle"
        />
      </g>
    </svg>
    
    <div v-if="hoveredNode" class="node-tooltip" :style="tooltipStyle">
      <h4>{{ hoveredNode.label }}</h4>
      <p>{{ hoveredNode.description || '点击查看详情' }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useWindowSize } from '@vueuse/core'
import * as d3 from 'd3'

const props = defineProps({
  data: { type: Object, default: () => ({ nodes: [], links: [] }) },
  showLabels: { type: Boolean, default: true },
  showFlowParticles: { type: Boolean, default: true },
  nodeColors: { type: Object, default: () => ({}) },
  simulationStrength: { type: Number, default: -300 },
  linkDistance: { type: Number, default: 100 },
})

const emit = defineEmits(['nodeClick', 'nodeHover'])

const containerRef = ref(null)
const svgRef = ref(null)
const { width, height } = useWindowSize()

const nodes = ref([])
const links = ref([])
const hoveredNode = ref(null)
const tooltipPosition = ref({ x: 0, y: 0 })
const flowParticles = ref([])

const simulation = ref(null)

const displayLinks = computed(() => {
  return links.value.map(link => {
    const source = typeof link.source === 'object' ? link.source : nodes.value.find(n => n.id === link.source)
    const target = typeof link.target === 'object' ? link.target : nodes.value.find(n => n.id === link.target)
    
    if (!source || !target) return null
    
    const midX = (source.x + target.x) / 2
    const midY = (source.y + target.y) / 2
    const dx = target.x - source.x
    const dy = target.y - source.y
    
    const curvature = 0.3
    const controlX = midX - dy * curvature
    const controlY = midY + dx * curvature
    
    return {
      path: `M ${source.x} ${source.y} Q ${controlX} ${controlY} ${target.x} ${target.y}`,
      opacity: 0.2 + (1 - (source.distanceTo(target) / 200)) * 0.3 || 0.3
    }
  }).filter(Boolean)
})

const tooltipStyle = computed(() => ({
  left: `${tooltipPosition.value.x}px`,
  top: `${tooltipPosition.value.y}px`,
}))

const getNodeColor = (type) => {
  const colors = {
    domain: '#667eea',
    category: '#764ba2',
    article: '#f093fb',
    keyword: '#4facfe',
    reference: '#00f2fe',
    ...props.nodeColors
  }
  return colors[type] || colors.domain
}

const initSimulation = () => {
  if (!props.data.nodes.length) return
  
  nodes.value = props.data.nodes.map(n => ({ ...n }))
  links.value = props.data.links.map(l => ({ ...l }))
  
  simulation.value = d3.forceSimulation(nodes.value)
    .force('link', d3.forceLink(links.value).id(d => d.id).distance(props.linkDistance))
    .force('charge', d3.forceManyBody().strength(props.simulationStrength))
    .force('center', d3.forceCenter(width.value / 2, height.value / 2))
    .force('collision', d3.forceCollide().radius(30))
    .on('tick', () => {})
}

const handleNodeHover = (node) => {
  hoveredNode.value = node
  tooltipPosition.value = { x: node.x + 20, y: node.y - 10 }
  emit('nodeHover', node)
}

const handleNodeLeave = () => {
  hoveredNode.value = null
}

const handleNodeClick = (node) => {
  emit('nodeClick', node)
}

const initFlowParticles = () => {
  flowParticles.value = []
  
  for (let i = 0; i < 20; i++) {
    if (links.value.length > 0) {
      const randomLink = links.value[Math.floor(Math.random() * links.value.length)]
      flowParticles.value.push({
        link: randomLink,
        progress: Math.random(),
        speed: 0.005 + Math.random() * 0.005
      })
    }
  }
}

const animateFlowParticles = () => {
  flowParticles.value.forEach(particle => {
    particle.progress += particle.speed
    if (particle.progress > 1) {
      particle.progress = 0
      if (links.value.length > 0) {
        particle.link = links.value[Math.floor(Math.random() * links.value.length)]
      }
    }
    
    const source = particle.link.source
    const target = particle.link.target
    
    if (source && target) {
      const t = particle.progress
      const controlX = (source.x + target.x) / 2
      const controlY = (source.y + target.y) / 2
      
      particle.x = (1 - t) * (1 - t) * source.x + 2 * (1 - t) * t * controlX + t * t * target.x
      particle.y = (1 - t) * (1 - t) * source.y + 2 * (1 - t) * t * controlY + t * t * target.y
    }
  })
  
  requestAnimationFrame(animateFlowParticles)
}

let animationId = null

onMounted(() => {
  initSimulation()
  initFlowParticles()
  animateFlowParticles()
})

onUnmounted(() => {
  if (simulation.value) simulation.value.stop()
  if (animationId) cancelAnimationFrame(animationId)
})

watch(() => props.data, initSimulation, { deep: true })
watch([width, height], () => {
  if (simulation.value) {
    simulation.value.force('center', d3.forceCenter(width.value / 2, height.value / 2))
    simulation.value.alpha(0.3).restart()
  }
})
</script>

<style scoped>
.flow-network {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 600px;
  background: linear-gradient(180deg, transparent 0%, rgba(102, 126, 234, 0.02) 100%);
}

.flow-network svg {
  display: block;
}

.network-link {
  fill: none;
  stroke: url(#flowGradient);
  stroke-width: 1.5;
  transition: stroke-opacity 0.3s;
}

.node-group {
  cursor: pointer;
  transition: transform 0.3s;
}

.node-group:hover {
  transform: scale(1.2);
}

.node-circle {
  transition: all 0.3s;
}

.node-group:hover .node-circle {
  filter: url(#glow);
  transform: scale(1.3);
}

.node-label {
  font-size: 11px;
  fill: var(--text-secondary);
  text-anchor: middle;
  font-family: 'Inter', sans-serif;
  pointer-events: none;
}

.flow-particle {
  pointer-events: none;
  filter: drop-shadow(0 0 4px rgba(255, 255, 255, 0.8));
}

.node-tooltip {
  position: absolute;
  background: var(--bg-glass);
  backdrop-filter: blur(20px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 1rem;
  max-width: 250px;
  pointer-events: none;
  z-index: 100;
  box-shadow: var(--shadow-lg);
}

.node-tooltip h4 {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
}

.node-tooltip p {
  font-size: 0.8rem;
  color: var(--text-secondary);
  line-height: 1.5;
}
</style>
