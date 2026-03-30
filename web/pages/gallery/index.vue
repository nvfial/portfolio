<template>
  <div class="gallery-page">
    <ParticleField :particle-count="60" :connection-distance="60" />
    
    <header class="page-header">
      <div class="container">
        <h1 class="page-title">艺术画廊</h1>
        <p class="page-subtitle">沉浸式视觉艺术体验</p>
      </div>
    </header>
    
    <div class="container">
      <div class="gallery-filters">
        <button 
          v-for="filter in filters" 
          :key="filter.id"
          class="filter-btn"
          :class="{ active: activeFilter === filter.id }"
          @click="activeFilter = filter.id"
        >
          {{ filter.name }}
        </button>
      </div>
      
      <div class="gallery-grid">
        <div 
          v-for="(artwork, index) in filteredArtworks" 
          :key="artwork.id"
          class="gallery-item"
          :class="{ 'item-large': index % 5 === 0 }"
          @click="openLightbox(artwork, index)"
        >
          <div class="item-image">
            <img :src="artwork.image" :alt="artwork.title" />
            <div class="item-overlay">
              <span class="overlay-icon">🖼️</span>
            </div>
          </div>
          <div class="item-info">
            <h3>{{ artwork.title }}</h3>
            <p>{{ artwork.artist }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <Teleport to="body">
      <div v-if="lightboxOpen" class="lightbox" @click="closeLightbox">
        <button class="lightbox-close" @click="closeLightbox">×</button>
        <button class="lightbox-nav prev" @click.stop="prevImage">‹</button>
        
        <div class="lightbox-content" @click.stop>
          <img :src="currentArtwork?.image" :alt="currentArtwork?.title" />
          <div class="lightbox-info">
            <h2>{{ currentArtwork?.title }}</h2>
            <p>{{ currentArtwork?.artist }}</p>
            <p class="lightbox-description">{{ currentArtwork?.description }}</p>
          </div>
        </div>
        
        <button class="lightbox-nav next" @click.stop="nextImage">›</button>
        
        <div class="lightbox-counter">
          {{ currentIndex + 1 }} / {{ filteredArtworks.length }}
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ParticleField from '~/components/art/ParticleField.vue'

const activeFilter = ref('all')
const lightboxOpen = ref(false)
const currentIndex = ref(0)

const filters = [
  { id: 'all', name: '全部' },
  { id: 'digital', name: '数字艺术' },
  { id: '3d', name: '3D渲染' },
  { id: 'photo', name: '摄影' }
]

const artworks = ref([
  {
    id: 1,
    title: '数字梦境',
    artist: 'AI Generated',
    category: 'digital',
    image: 'https://picsum.photos/800/600?random=1',
    description: '数字世界的梦幻景象'
  },
  {
    id: 2,
    title: '赛博空间',
    artist: '3D Artist',
    category: '3d',
    image: 'https://picsum.photos/800/600?random=2',
    description: '未来科技感3D渲染'
  },
  {
    id: 3,
    title: '自然之美',
    artist: 'Photography',
    category: 'photo',
    image: 'https://picsum.photos/800/600?random=3',
    description: '大自然的鬼斧神工'
  },
  {
    id: 4,
    title: '抽象思维',
    artist: 'Digital Art',
    category: 'digital',
    image: 'https://picsum.photos/800/600?random=4',
    description: '抽象与具象的交融'
  },
  {
    id: 5,
    title: '虚拟现实',
    artist: '3D Designer',
    category: '3d',
    image: 'https://picsum.photos/800/600?random=5',
    description: '沉浸式虚拟体验'
  },
  {
    id: 6,
    title: '光影艺术',
    artist: 'Photo Artist',
    category: 'photo',
    image: 'https://picsum.photos/800/600?random=6',
    description: '光与影的舞蹈'
  }
])

const filteredArtworks = computed(() => {
  if (activeFilter.value === 'all') return artworks.value
  return artworks.value.filter(a => a.category === activeFilter.value)
})

const currentArtwork = computed(() => filteredArtworks.value[currentIndex.value])

const openLightbox = (artwork, index) => {
  currentIndex.value = index
  lightboxOpen.value = true
  document.body.style.overflow = 'hidden'
}

const closeLightbox = () => {
  lightboxOpen.value = false
  document.body.style.overflow = ''
}

const prevImage = () => {
  currentIndex.value = (currentIndex.value - 1 + filteredArtworks.value.length) % filteredArtworks.value.length
}

const nextImage = () => {
  currentIndex.value = (currentIndex.value + 1) % filteredArtworks.value.length
}
</script>

<style scoped>
.gallery-page {
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

.gallery-filters {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 3rem;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius-full);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  font-size: 0.95rem;
  transition: all var(--transition-fast);
}

.filter-btn:hover {
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

.filter-btn.active {
  background: var(--gradient-primary);
  border-color: transparent;
  color: white;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  padding-bottom: 4rem;
}

.gallery-item {
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-base);
  background: var(--bg-secondary);
}

.gallery-item:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
}

.item-large {
  grid-column: span 2;
}

.item-image {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.item-large .item-image {
  aspect-ratio: 16/9;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.gallery-item:hover .item-image img {
  transform: scale(1.05);
}

.item-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.gallery-item:hover .item-overlay {
  opacity: 1;
}

.overlay-icon {
  font-size: 2rem;
}

.item-info {
  padding: 1rem;
}

.item-info h3 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.item-info p {
  font-size: 0.9rem;
  color: var(--text-tertiary);
}

.lightbox {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.95);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lightbox-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  font-size: 2rem;
  color: white;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.lightbox-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  font-size: 3rem;
  color: white;
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.lightbox-nav.prev { left: 2rem; }
.lightbox-nav.next { right: 2rem; }

.lightbox-content {
  max-width: 90vw;
  max-height: 90vh;
  text-align: center;
}

.lightbox-content img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.lightbox-info {
  color: white;
  margin-top: 1rem;
}

.lightbox-info h2 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.lightbox-description {
  opacity: 0.7;
  font-size: 0.9rem;
}

.lightbox-counter {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .item-large {
    grid-column: span 1;
  }
  
  .gallery-grid {
    grid-template-columns: 1fr;
  }
}
</style>
