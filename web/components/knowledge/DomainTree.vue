<template>
  <div class="domain-tree">
    <div class="tree-header">
      <h3>知识领域</h3>
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="搜索领域..."
          @input="handleSearch"
        />
        <span class="search-icon">🔍</span>
      </div>
    </div>
    
    <div class="tree-content">
      <div 
        v-for="domain in filteredDomains" 
        :key="domain.id"
        class="domain-item"
        :class="{ active: activeDomain?.id === domain.id }"
        @click="selectDomain(domain)"
      >
        <div class="domain-header">
          <span class="domain-icon" :style="{ background: domain.color }">
            {{ domain.icon }}
          </span>
          <span class="domain-name">{{ domain.name }}</span>
          <span v-if="domain.children?.length" class="expand-icon">
            {{ expandedDomains[domain.id] ? '−' : '+' }}
          </span>
        </div>
        
        <div v-if="expandedDomains[domain.id]" class="category-children">
          <div 
            v-for="category in domain.children" 
            :key="category.id"
            class="category-item"
            :class="{ active: activeCategory?.id === category.id }"
            @click.stop="selectCategory(category, domain)"
          >
            <span class="category-icon">{{ category.icon }}</span>
            <span class="category-name">{{ category.name }}</span>
            <span class="category-count">{{ category.articleCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="tree-stats">
      <div class="stat-item">
        <span class="stat-value">{{ domains.length }}</span>
        <span class="stat-label">领域</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ totalCategories }}</span>
        <span class="stat-label">分类</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ totalArticles }}</span>
        <span class="stat-label">文章</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  domains: { type: Array, default: () => [] },
})

const emit = defineEmits(['selectDomain', 'selectCategory'])

const searchQuery = ref('')
const activeDomain = ref(null)
const activeCategory = ref(null)
const expandedDomains = ref({})

const filteredDomains = computed(() => {
  if (!searchQuery.value) return props.domains
  
  const query = searchQuery.value.toLowerCase()
  return props.domains.filter(domain => {
    if (domain.name.toLowerCase().includes(query)) return true
    if (domain.children?.some(c => c.name.toLowerCase().includes(query))) return true
    return false
  })
})

const totalCategories = computed(() => {
  return props.domains.reduce((sum, d) => sum + (d.children?.length || 0), 0)
})

const totalArticles = computed(() => {
  return props.domains.reduce((sum, d) => 
    sum + d.children?.reduce((s, c) => s + (c.articleCount || 0), 0) || 0, 0
  )
})

const selectDomain = (domain) => {
  activeDomain.value = domain
  activeCategory.value = null
  expandedDomains.value[domain.id] = !expandedDomains.value[domain.id]
  emit('selectDomain', domain)
}

const selectCategory = (category, domain) => {
  activeCategory.value = category
  emit('selectCategory', category, domain)
}

const handleSearch = () => {
}
</script>

<style scoped>
.domain-tree {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 1.5rem;
  border: 1px solid var(--border-color);
}

.tree-header {
  margin-bottom: 1.5rem;
}

.tree-header h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.search-box {
  position: relative;
}

.search-box input {
  width: 100%;
  padding: 0.75rem 1rem 0.75rem 2.5rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  color: var(--text-primary);
  font-size: 0.9rem;
  transition: all var(--transition-fast);
}

.search-box input:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.9rem;
  opacity: 0.5;
}

.tree-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-height: 500px;
  overflow-y: auto;
}

.domain-item {
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.domain-item:hover {
  background: var(--bg-tertiary);
}

.domain-item.active {
  background: rgba(102, 126, 234, 0.1);
}

.domain-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
}

.domain-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
}

.domain-name {
  flex: 1;
  font-weight: 500;
  color: var(--text-primary);
}

.expand-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  background: var(--bg-tertiary);
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.category-children {
  padding-left: 2.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.category-item:hover {
  background: var(--bg-tertiary);
}

.category-item.active {
  background: var(--accent-primary);
  color: white;
}

.category-icon {
  font-size: 0.9rem;
}

.category-name {
  flex: 1;
  font-size: 0.9rem;
}

.category-count {
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
}

.category-item.active .category-count {
  background: rgba(255, 255, 255, 0.2);
}

.tree-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
</style>
