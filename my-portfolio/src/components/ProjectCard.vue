<template>
  <div 
    class="project-card" 
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
    @mousemove="handleMouseMove"
    @click="$emit('click')"
    :style="cardStyle"
    ref="cardRef"
  >
    <div class="card-inner">
      <div class="card-front">
        <div class="card-image-wrapper">
          <img :src="project.image" :alt="project.title" />
          <div class="card-overlay">
            <div class="card-tags">
              <span v-for="tag in project.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
        <div class="card-content">
          <h3>{{ project.title }}</h3>
          <p>{{ project.description }}</p>
          <div class="card-actions">
            <a :href="project.link" class="card-link" target="_blank" rel="noopener">
              <span>查看项目</span>
              <span class="link-arrow">→</span>
            </a>
            <a :href="project.github" class="card-link github" target="_blank" rel="noopener" v-if="project.github">
              <span>GitHub</span>
            </a>
          </div>
        </div>
      </div>
    </div>
    <div class="card-glow"></div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

defineEmits(['click'])

const props = defineProps({
  project: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    default: 0
  }
})

const cardRef = ref(null)
const rotateX = ref(0)
const rotateY = ref(0)

const cardStyle = computed(() => ({
  transform: `perspective(1000px) rotateX(${rotateX.value}deg) rotateY(${rotateY.value}deg)`,
  transition: 'transform 0.1s ease-out'
}))

const handleMouseMove = (e) => {
  if (!cardRef.value) return
  const rect = cardRef.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2
  const rotateXValue = (y - centerY) / 10
  const rotateYValue = (centerX - x) / 10
  
  rotateX.value = Math.max(-10, Math.min(10, rotateXValue))
  rotateY.value = Math.max(-10, Math.min(10, rotateYValue))
}

const handleMouseEnter = () => {
  // 可以添加额外的进入效果
}

const handleMouseLeave = () => {
  rotateX.value = 0
  rotateY.value = 0
}
</script>

<style scoped>
.project-card {
  position: relative;
  height: 100%;
  cursor: pointer;
  transform-style: preserve-3d;
}

.card-inner {
  position: relative;
  height: 100%;
  background: var(--bg-secondary);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-md);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-color);
}

.project-card:hover .card-inner {
  box-shadow: var(--shadow-xl);
  transform: translateY(-8px);
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: var(--bg-tertiary);
}

.card-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.project-card:hover .card-image-wrapper img {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.7) 100%);
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  align-items: flex-end;
  padding: 1rem;
}

.project-card:hover .card-overlay {
  opacity: 1;
}

.card-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.tag {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.card-content {
  padding: 1.5rem;
}

.card-content h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
  transition: color 0.3s;
}

.project-card:hover .card-content h3 {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-content p {
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
}

.card-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.card-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--accent-primary);
  text-decoration: none;
  font-weight: 500;
  font-size: 0.9rem;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  background: rgba(102, 126, 234, 0.1);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.card-link::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity 0.3s;
}

.card-link span {
  position: relative;
  z-index: 1;
}

.card-link:hover {
  color: white;
  transform: translateX(4px);
}

.card-link:hover::before {
  opacity: 1;
}

.link-arrow {
  transition: transform 0.3s;
}

.card-link:hover .link-arrow {
  transform: translateX(4px);
}

.card-link.github {
  background: rgba(0, 0, 0, 0.05);
}

.dark .card-link.github {
  background: rgba(255, 255, 255, 0.1);
}

.card-glow {
  position: absolute;
  inset: -2px;
  border-radius: 16px;
  background: var(--gradient-primary);
  opacity: 0;
  filter: blur(20px);
  transition: opacity 0.3s;
  z-index: -1;
}

.project-card:hover .card-glow {
  opacity: 0.3;
}

@media (max-width: 768px) {
  .card-content {
    padding: 1.25rem;
  }
  
  .card-content h3 {
    font-size: 1.25rem;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .card-link {
    width: 100%;
    justify-content: center;
  }
}
</style>
