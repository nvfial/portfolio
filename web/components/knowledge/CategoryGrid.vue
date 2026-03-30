<template>
  <div class="category-grid">
    <div 
      v-for="category in categories" 
      :key="category.id"
      class="category-card"
      :style="{ '--accent-color': category.color || '#667eea' }"
      @click="handleClick(category)"
    >
      <div class="card-background">
        <div class="card-pattern"></div>
      </div>
      
      <div class="card-content">
        <div class="card-icon">
          {{ category.icon }}
        </div>
        
        <h3 class="card-title">{{ category.name }}</h3>
        <p class="card-description">{{ category.description }}</p>
        
        <div class="card-meta">
          <span class="article-count">
            <span class="count-number">{{ category.articleCount || 0 }}</span>
            <span class="count-label">篇文章</span>
          </span>
          
          <div class="tags">
            <span 
              v-for="(tag, index) in (category.tags || []).slice(0, 3)" 
              :key="index"
              class="tag"
            >
              {{ tag }}
            </span>
          </div>
        </div>
        
        <div class="card-arrow">
          →
        </div>
      </div>
      
      <div class="card-glow"></div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  categories: { type: Array, default: () => [] },
})

const emit = defineEmits(['select'])

const handleClick = (category) => {
  emit('select', category)
}
</script>

<style scoped>
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.category-card {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-base);
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
}

.category-card:hover {
  transform: translateY(-8px);
  border-color: var(--accent-color);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.card-background {
  position: absolute;
  inset: 0;
  opacity: 0.05;
  background: var(--accent-color);
}

.card-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, var(--accent-color) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, var(--accent-color) 0%, transparent 50%);
  opacity: 0.3;
}

.card-content {
  position: relative;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  height: 100%;
}

.card-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.card-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
}

.card-description {
  font-size: 0.9rem;
  color: var(--text-secondary);
  line-height: 1.6;
  flex: 1;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.article-count {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
}

.count-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--accent-color);
}

.count-label {
  font-size: 0.8rem;
  color: var(--text-tertiary);
}

.tags {
  display: flex;
  gap: 0.25rem;
}

.tag {
  padding: 0.25rem 0.5rem;
  font-size: 0.7rem;
  border-radius: var(--radius-full);
  background: var(--bg-tertiary);
  color: var(--text-secondary);
}

.card-arrow {
  position: absolute;
  bottom: 1.5rem;
  right: 1.5rem;
  font-size: 1.5rem;
  color: var(--accent-color);
  opacity: 0;
  transform: translateX(-10px);
  transition: all var(--transition-base);
}

.category-card:hover .card-arrow {
  opacity: 1;
  transform: translateX(0);
}

.card-glow {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 0%, var(--accent-color) 200%);
  opacity: 0;
  transition: opacity var(--transition-base);
  pointer-events: none;
}

.category-card:hover .card-glow {
  opacity: 0.05;
}
</style>
