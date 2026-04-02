<template>
  <div class="character-page" ref="pageRef" v-if="character" :style="themeStyles">
    <div class="hero-section" :class="particleClass">
      <div class="hero-bg">
        <img 
          v-if="character.bgImageUrl" 
          :src="character.bgImageUrl" 
          class="bg-image" 
          alt=""
        />
        <div class="bg-overlay"></div>
        <div class="bg-texture"></div>
      </div>
      
      <div class="hero-content">
        <div class="hero-frame">
          <div class="frame-line top"></div>
          <div class="frame-line bottom"></div>
          <div class="frame-line left"></div>
          <div class="frame-line right"></div>
          
          <div class="portrait-container">
            <img 
              :src="character.portraitUrl || '/images/placeholder-portrait.jpg'" 
              class="portrait-image"
              alt=""
            />
            <div class="portrait-filter"></div>
          </div>
        </div>
        
        <div class="hero-info">
          <div class="info-label">CAST</div>
          <h1 class="character-name" @mouseenter="revealText('name')">
            <span class="char-hidden" ref="nameRef">{{ character.name }}</span>
            <span class="char-reveal" v-if="revealed.name">{{ character.name }}</span>
          </h1>
          <p class="character-title" v-if="character.title" @mouseenter="revealText('title')">
            <span class="char-hidden" ref="titleRef">{{ '—'.repeat(character.title.length) }}</span>
            <span class="char-reveal" v-if="revealed.title">{{ character.title }}</span>
          </p>
          
          <div class="info-ornaments">
            <span class="ornament">◆</span>
            <span class="ornament">◇</span>
            <span class="ornament">◆</span>
          </div>
        </div>
      </div>
      
      <div class="scroll-indicator" @click="scrollToContent">
        <div class="scroll-line"></div>
        <span class="scroll-text">SCROLL</span>
      </div>
    </div>
    
    <div class="content-section" ref="contentRef">
      <div class="section-nav">
        <div class="nav-track">
          <div 
            v-for="(tab, index) in tabs" 
            :key="tab.id"
            class="nav-tab"
            :class="{ active: activeTab === tab.id }"
            @click="switchTab(tab.id)"
            @mouseenter="revealTabLabel(tab.id)"
          >
            <div class="tab-frame">
              <span class="tab-hidden" :ref="el => tabRefs[tab.id] = el">{{ '—'.repeat(tab.label.length) }}</span>
              <span class="tab-reveal" v-if="revealedTabs.includes(tab.id)">{{ tab.label }}</span>
            </div>
            <div class="tab-indicator" v-if="activeTab === tab.id"></div>
          </div>
        </div>
      </div>
      
      <div class="tab-content">
        <div v-if="activeTab === 'intro'" class="tab-panel intro-panel">
          <div class="panel-art-divider">
            <div class="art-line"></div>
            <span class="art-symbol">✦</span>
            <div class="art-line"></div>
          </div>
          
          <div class="bio-section" @mouseenter="revealText('bio')">
            <div class="bio-frame">
              <div class="frame-corner tl"></div>
              <div class="frame_corner tr"></div>
              <div class="frame_corner bl"></div>
              <div class="frame_corner br"></div>
              
              <div class="bio-texture"></div>
              
              <p class="bio-text">
                <span class="text-hidden" ref="bioRef">{{ '　'.repeat(50) }}</span>
                <span class="text-reveal" v-if="revealed.bio" v-html="formatBio(character.bio)"></span>
              </p>
            </div>
          </div>
          
          <div class="stats-grid">
            <div class="stat-card" v-for="stat in quickStats" :key="stat.label">
              <div class="stat-icon">{{ stat.icon }}</div>
              <div class="stat-info">
                <div class="stat-label">{{ stat.label }}</div>
                <div class="stat-value" @mouseenter="revealText(stat.key)">
                  <span class="val-hidden">{{ '—'.repeat(stat.value.length) }}</span>
                  <span class="val-reveal" v-if="revealed[stat.key]">{{ stat.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'stories'" class="tab-panel stories-panel">
          <div class="panel-art-divider">
            <div class="art-line"></div>
            <span class="art-symbol">✦</span>
            <div class="art-line"></div>
          </div>
          
          <div class="stories-showcase">
            <div 
              v-for="(story, index) in stories" 
              :key="story.id"
              class="story-card"
              @mouseenter="revealStory(index)"
            >
              <div class="card-frame">
                <img 
                  v-if="story.coverImageUrl" 
                  :src="story.coverImageUrl" 
                  class="story-cover" 
                  alt=""
                />
                <div class="cover-overlay">
                  <div class="overlay-gradient"></div>
                </div>
                <div class="card-number">{{ String(index + 1).padStart(2, '0') }}</div>
              </div>
              
              <div class="story-info">
                <h3 class="story-chapter">
                  <span class="chapter-hidden">{{ '—'.repeat(story.chapterTitle?.length || 10) }}</span>
                  <span class="chapter-reveal" v-if="revealedStories.includes(index)">{{ story.chapterTitle }}</span>
                </h3>
                <p class="story-type">
                  <span class="type-hidden">{{ '—'.repeat(story.contentType?.length || 5) }}</span>
                  <span class="type-reveal" v-if="revealedStories.includes(index)">{{ story.contentType }}</span>
                </p>
              </div>
            </div>
            
            <div v-if="stories.length === 0" class="empty-stories">
              <div class="empty-frame"></div>
              <p>暂无故事篇章</p>
            </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'timeline'" class="tab-panel timeline-panel">
          <div class="panel-art-divider">
            <div class="art-line"></div>
            <span class="art-symbol">✦</span>
            <div class="art-line"></div>
          </div>
          
          <div class="timeline-container">
            <div 
              v-for="(event, index) in timelineEvents" 
              :key="event.id"
              class="timeline-event"
              :class="{ 'left': index % 2 === 0, 'right': index % 2 !== 0, 'revealed': revealedTimeline.includes(index) }"
              @mouseenter="revealTimelineEvent(index)"
            >
              <div class="event-marker">
                <div class="marker-dot"></div>
                <div class="marker-line"></div>
              </div>
              
              <div class="event-card">
                <div class="card-image" v-if="event.eventImageUrl">
                  <img :src="event.eventImageUrl" alt="" />
                  <div class="image-overlay"></div>
                </div>
                
                <div class="card-content">
                  <div class="event-year">
                    <span class="year-hidden">{{ '————' }}</span>
                    <span class="year-reveal" v-if="revealedTimeline.includes(index)">{{ event.eventYear }}</span>
                  </div>
                  <h4 class="event-title">
                    <span class="title-hidden">{{ '—'.repeat(event.eventTitle?.length || 8) }}</span>
                    <span class="title-reveal" v-if="revealedTimeline.includes(index)">{{ event.eventTitle }}</span>
                  </h4>
                  <p class="event-desc">
                    <span class="desc-hidden">{{ '　'.repeat(30) }}</span>
                    <span class="desc-reveal" v-if="revealedTimeline.includes(index)">{{ event.eventDesc }}</span>
                  </p>
                </div>
              </div>
            </div>
            
            <div v-if="timelineEvents.length === 0" class="empty-timeline">
              <div class="timeline-spacer"></div>
              <p>暂无生涯记录</p>
            </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'quotes'" class="tab-panel quotes-panel">
          <div class="panel-art-divider">
            <div class="art-line"></div>
            <span class="art-symbol">✦</span>
            <div class="art-line"></div>
          </div>
          
          <div class="quotes-gallery">
            <div 
              v-for="(quote, index) in quotes" 
              :key="quote.id"
              class="quote-card"
              @mouseenter="revealQuote(index)"
            >
              <div class="quote-mark opening">"</div>
              <blockquote class="quote-text">
                <span class="text-hidden">{{ '　'.repeat(quote.quoteText?.length || 20) }}</span>
                <span class="text-reveal" v-if="revealedQuotes.includes(index)">{{ quote.quoteText }}</span>
              </blockquote>
              <div class="quote-mark closing">"</div>
              <cite class="quote-source" v-if="quote.context">
                <span class="source-hidden">{{ '—'.repeat(quote.context.length) }}</span>
                <span class="source-reveal" v-if="revealedQuotes.includes(index)">— {{ quote.context }}</span>
              </cite>
            </div>
            
            <div v-if="quotes.length === 0" class="empty-quotes">
              <div class="quote-frame"></div>
              <p>暂无语录</p>
            </div>
          </div>
        </div>
        
        <div v-if="activeTab === 'gallery'" class="tab-panel gallery-panel">
          <div class="panel-art-divider">
            <div class="art-line"></div>
            <span class="art-symbol">✦</span>
            <div class="art-line"></div>
          </div>
          
          <div class="gallery-grid">
            <div 
              v-for="(media, index) in galleryMedia" 
              :key="media.id"
              class="gallery-item"
              @mouseenter="revealGalleryItem(index)"
            >
              <div class="item-frame">
                <img 
                  v-if="media.mediaType === 'IMAGE'" 
                  :src="media.url" 
                  :alt="media.title"
                  class="item-image"
                />
                <video 
                  v-else-if="media.mediaType === 'VIDEO'" 
                  :src="media.url"
                  class="item-video"
                  muted
                  loop
                  @mouseenter="$event.target.play()"
                  @mouseleave="$event.target.pause()"
                ></video>
                <div v-else class="item-audio">
                  <div class="audio-icon">♫</div>
                </div>
                
                <div class="item-overlay">
                  <div class="overlay-pattern"></div>
                </div>
                
                <div class="item-info">
                  <h4 class="item-title">
                    <span class="title-hidden">{{ '—'.repeat(media.title?.length || 8) }}</span>
                    <span class="title-reveal" v-if="revealedGallery.includes(index)">{{ media.title }}</span>
                  </h4>
                  <p class="item-desc">
                    <span class="desc-hidden">{{ '　'.repeat(media.description?.length || 15) }}</span>
                    <span class="desc-reveal" v-if="revealedGallery.includes(index)">{{ media.description }}</span>
                  </p>
                </div>
              </div>
            </div>
            
            <div v-if="galleryMedia.length === 0" class="empty-gallery">
              <div class="gallery-frame"></div>
              <p>暂无媒体资源</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="back-indicator" @click="goBack">
      <span class="back-text">RETURN</span>
    </div>
  </div>
  
  <div v-else class="loading-page">
    <div class="loading-frame">
      <div class="loading-slats">
        <div class="slat" v-for="i in 7" :key="i"></div>
      </div>
    </div>
    <p class="loading-text">LOADING...</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { gsap } from 'gsap'
import { characterApi } from '../utils/api'

const router = useRouter()
const route = useRoute()
const pageRef = ref(null)
const contentRef = ref(null)
const tabRefs = ref({})

const character = ref(null)
const stories = ref([])
const timelineEvents = ref([])
const quotes = ref([])
const galleryMedia = ref([])

const activeTab = ref('intro')

const revealed = ref({
  name: false,
  title: false,
  bio: false,
  cv: false
})

const revealedTabs = ref([])
const revealedStories = ref([])
const revealedTimeline = ref([])
const revealedQuotes = ref([])
const revealedGallery = ref([])

const nameRef = ref(null)
const titleRef = ref(null)
const bioRef = ref(null)

const themeStyles = computed(() => {
  let primary = character.value?.themeColor || '#c9a96a'
  let secondary = character.value?.themeColorSecondary || '#8b7355'
  let particleEffect = character.value?.particleEffect || 'stars'
  let frameStyle = character.value?.frameStyle || 'classic'
  let fontFamily = character.value?.fontFamily || 'serif'
  let entranceAnimation = character.value?.entranceAnimation || 'fade'
  let titleAnimation = character.value?.titleAnimation || 'typewriter'
  
  const preset = character.value?.themePreset || 'golden'
  const customConfig = character.value?.customThemeConfig
  
  const presets = {
    golden: { primary: '#c9a96a', secondary: '#8b7355', particle: 'stars', frame: 'classic', font: 'serif' },
    silver: { primary: '#b8c4ce', secondary: '#7a8a99', particle: 'snow', frame: 'minimal', font: 'serif' },
    blue: { primary: '#4a90d9', secondary: '#2c5aa0', particle: 'rain', frame: 'modern', font: 'sans-serif' },
    purple: { primary: '#9b59b6', secondary: '#6c3483', particle: 'stars', frame: 'ornate', font: 'fantasy' },
    red: { primary: '#e74c3c', secondary: '#922b21', particle: 'fireflies', frame: 'classic', font: 'serif' },
    green: { primary: '#27ae60', secondary: '#1e8449', particle: 'fireflies', frame: 'classic', font: 'cursive' },
    cyan: { primary: '#1abc9c', secondary: '#16a085', particle: 'rain', frame: 'modern', font: 'sans-serif' },
    pink: { primary: '#ff6b9d', secondary: '#c44569', particle: 'snow', frame: 'ornate', font: 'cursive' },
    black: { primary: '#2c3e50', secondary: '#1a252f', particle: 'neon', frame: 'modern', font: 'sans-serif' },
    white: { primary: '#ecf0f1', secondary: '#bdc3c7', particle: 'snow', frame: 'minimal', font: 'serif' }
  }
  
  if (preset !== 'custom' && presets[preset]) {
    const p = presets[preset]
    primary = character.value?.themeColor || p.primary
    secondary = character.value?.themeColorSecondary || p.secondary
    particleEffect = character.value?.particleEffect || p.particle
    frameStyle = character.value?.frameStyle || p.frame
    fontFamily = character.value?.fontFamily || p.font
  }
  
  if (customConfig) {
    try {
      const parsed = JSON.parse(customConfig)
      if (parsed.primary) primary = parsed.primary
      if (parsed.secondary) secondary = parsed.secondary
      if (parsed.particleEffect) particleEffect = parsed.particleEffect
      if (parsed.frameStyle) frameStyle = parsed.frameStyle
      if (parsed.fontFamily) fontFamily = parsed.fontFamily
      if (parsed.entranceAnimation) entranceAnimation = parsed.entranceAnimation
      if (parsed.titleAnimation) titleAnimation = parsed.titleAnimation
    } catch (e) {}
  }
  
  return {
    '--theme-primary': primary,
    '--theme-secondary': secondary,
    '--theme-glow': `${primary}40`,
    fontFamily: fontFamily
  }
})

const particleClass = computed(() => {
  let effect = character.value?.particleEffect || 'stars'
  const preset = character.value?.themePreset || 'golden'
  const customConfig = character.value?.customThemeConfig
  
  const presets = {
    golden: 'stars', silver: 'snow', blue: 'rain', purple: 'stars',
    red: 'fireflies', green: 'fireflies', cyan: 'rain', pink: 'snow',
    black: 'neon', white: 'snow'
  }
  
  if (preset !== 'custom' && !character.value?.particleEffect) {
    effect = presets[preset] || 'stars'
  }
  
  if (customConfig) {
    try {
      const parsed = JSON.parse(customConfig)
      if (parsed.particleEffect) effect = parsed.particleEffect
    } catch (e) {}
  }
  
  return `particle-${effect}`
})

const tabs = [
  { id: 'intro', label: '简介' },
  { id: 'stories', label: '过往' },
  { id: 'timeline', label: '生涯' },
  { id: 'quotes', label: '语录' },
  { id: 'gallery', label: '图鉴' }
]

const quickStats = computed(() => {
  const stats = []
  if (character.value?.cv) {
    stats.push({ key: 'cv', label: '声优', value: character.value.cv, icon: '🎤' })
  }
  if (character.value?.fullBodyUrl) {
    stats.push({ key: 'fullBody', label: '全身', value: '已解锁', icon: '🖼️' })
  }
  if (character.value?.voiceUrl) {
    stats.push({ key: 'voice', label: '语音', value: '已解锁', icon: '🔊' })
  }
  if (character.value?.sceneUrl) {
    stats.push({ key: 'scene', label: '场景', value: '已解锁', icon: '🏞️' })
  }
  return stats
})

const loadCharacter = async () => {
  try {
    const id = route.params.id
    const data = await characterApi.getById(id)
    character.value = data
    
    stories.value = data.stories || []
    timelineEvents.value = data.timelineEvents || []
    quotes.value = data.quotes || []
    galleryMedia.value = data.media || []
    
    nextTick(() => {
      initHeroAnimations()
    })
  } catch (error) {
    console.error('Failed to load character:', error)
  }
}

const initHeroAnimations = () => {
  gsap.from('.hero-bg', {
    opacity: 0,
    scale: 1.1,
    duration: 1.5,
    ease: 'power2.out'
  })
  
  gsap.from('.hero-frame', {
    x: -50,
    opacity: 0,
    duration: 1,
    delay: 0.5
  })
  
  gsap.from('.hero-info', {
    x: 50,
    opacity: 0,
    duration: 1,
    delay: 0.8
  })
  
  gsap.from('.scroll-indicator', {
    opacity: 0,
    y: 20,
    duration: 0.5,
    delay: 1.5
  })
}

const revealText = (key) => {
  revealed.value[key] = true
  
  if (key === 'name' && nameRef.value) {
    gsap.to(nameRef.value, { opacity: 0, duration: 0.3 })
  }
  if (key === 'title' && titleRef.value) {
    gsap.to(titleRef.value, { opacity: 0, duration: 0.3 })
  }
  if (key === 'bio' && bioRef.value) {
    gsap.to(bioRef.value, { opacity: 0, duration: 0.3 })
  }
}

const revealTabLabel = (tabId) => {
  if (!revealedTabs.value.includes(tabId)) {
    revealedTabs.value.push(tabId)
    
    const el = tabRefs.value[tabId]
    if (el) {
      gsap.to(el, { opacity: 0, duration: 0.3 })
    }
  }
}

const switchTab = (tabId) => {
  activeTab.value = tabId
  
  nextTick(() => {
    gsap.from(`.tab-panel`, {
      opacity: 0,
      y: 30,
      duration: 0.5
    })
  })
}

const revealStory = (index) => {
  if (!revealedStories.value.includes(index)) {
    revealedStories.value.push(index)
  }
}

const revealTimelineEvent = (index) => {
  if (!revealedTimeline.value.includes(index)) {
    revealedTimeline.value.push(index)
  }
}

const revealQuote = (index) => {
  if (!revealedQuotes.value.includes(index)) {
    revealedQuotes.value.push(index)
  }
}

const revealGalleryItem = (index) => {
  if (!revealedGallery.value.includes(index)) {
    revealedGallery.value.push(index)
  }
}

const formatBio = (text) => {
  if (!text) return ''
  return text.replace(/\n/g, '<br>')
}

const scrollToContent = () => {
  contentRef.value?.scrollIntoView({ behavior: 'smooth' })
}

const goBack = () => {
  router.push('/cast')
}

onMounted(() => {
  loadCharacter()
})
</script>

<style scoped>
.character-page {
  min-height: 100vh;
  background: #0a0a0f;
}

.hero-section {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

.hero-section.particle-stars::before {
  background-image: 
    radial-gradient(2px 2px at 20px 30px, var(--theme-primary), transparent),
    radial-gradient(2px 2px at 40px 70px, var(--theme-secondary), transparent),
    radial-gradient(1px 1px at 90px 40px, var(--theme-primary), transparent),
    radial-gradient(2px 2px at 130px 80px, var(--theme-secondary), transparent),
    radial-gradient(1px 1px at 160px 120px, var(--theme-primary), transparent);
  background-size: 200px 200px;
  animation: twinkle 4s ease-in-out infinite;
}

.hero-section.particle-fireflies::before {
  background-image: 
    radial-gradient(3px 3px at 10% 20%, var(--theme-primary), transparent),
    radial-gradient(2px 2px at 30% 60%, var(--theme-primary), transparent),
    radial-gradient(4px 4px at 50% 30%, var(--theme-secondary), transparent),
    radial-gradient(2px 2px at 70% 80%, var(--theme-primary), transparent),
    radial-gradient(3px 3px at 90% 50%, var(--theme-secondary), transparent);
  background-size: 300px 300px;
  animation: firefly 6s ease-in-out infinite;
}

.hero-section.particle-rain::before {
  background-image: 
    linear-gradient(to bottom, var(--theme-glow), transparent),
    linear-gradient(to bottom, var(--theme-glow), transparent),
    linear-gradient(to bottom, var(--theme-glow), transparent);
  background-size: 100px 200px;
  background-position: 0 0, 25px 50px, 50px 100px;
  animation: rain 1s linear infinite;
}

.hero-section.particle-snow::before {
  background-image: 
    radial-gradient(2px 2px at 10% 10%, var(--theme-primary), transparent),
    radial-gradient(3px 3px at 30% 30%, var(--theme-primary), transparent),
    radial-gradient(2px 2px at 50% 50%, var(--theme-secondary), transparent),
    radial-gradient(3px 3px at 70% 70%, var(--theme-primary), transparent),
    radial-gradient(2px 2px at 90% 90%, var(--theme-secondary), transparent);
  background-size: 150px 150px;
  animation: snow 10s linear infinite;
}

.hero-section.particle-neon::before {
  background-image: 
    radial-gradient(circle at 20% 30%, var(--theme-glow) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, var(--theme-glow) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, var(--theme-glow) 0%, transparent 30%);
  background-size: 100% 100%;
  animation: neonPulse 3s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.8; }
}

@keyframes firefly {
  0%, 100% { opacity: 0.2; transform: translateY(0); }
  50% { opacity: 0.6; transform: translateY(-20px); }
}

@keyframes rain {
  0% { background-position: 0 0, 25px 50px, 50px 100px; }
  100% { background-position: 0 200px, 25px 250px, 50px 300px; }
}

@keyframes snow {
  0% { background-position: 0 0, 30px 60px, 60px 30px; }
  100% { background-position: 100px 200px, 130px 260px, 160px 230px; }
}

@keyframes neonPulse {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 0.8; }
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.4) saturate(0.8);
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(10, 10, 15, 0.3) 0%,
    rgba(10, 10, 15, 0.7) 50%,
    rgba(10, 10, 15, 1) 100%
  );
}

.bg-texture {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    90deg,
    transparent,
    transparent 100px,
    rgba(255, 255, 255, 0.01) 100px,
    rgba(255, 255, 255, 0.01) 101px
  );
}

.hero-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 4rem;
  max-width: 1200px;
  padding: 2rem;
}

.hero-frame {
  position: relative;
  width: 350px;
  height: 500px;
  flex-shrink: 0;
}

.frame-line {
  position: absolute;
  background: rgba(201, 169, 106, 0.4);
}

.frame-line.top {
  top: 0; left: 20px; right: 20px; height: 1px;
}

.frame-line.bottom {
  bottom: 0; left: 20px; right: 20px; height: 1px;
}

.frame-line.left {
  top: 20px; bottom: 20px; left: 0; width: 1px;
}

.frame-line.right {
  top: 20px; bottom: 20px; right: 0; width: 1px;
}

.portrait-container {
  position: absolute;
  inset: 30px;
  overflow: hidden;
}

.portrait-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: grayscale(20%);
}

.portrait-filter {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(201, 169, 106, 0.1) 0%,
    transparent 50%,
    rgba(0, 0, 0, 0.3) 100%
  );
}

.hero-info {
  flex: 1;
}

.info-label {
  font-size: 0.75rem;
  color: rgba(201, 169, 106, 0.5);
  letter-spacing: 0.5em;
  margin-bottom: 1rem;
}

.character-name {
  font-size: 4rem;
  font-weight: 300;
  color: var(--theme-primary);
  font-family: var(--font-family, serif);
  letter-spacing: 0.2em;
  margin-bottom: 0.5rem;
  cursor: default;
}

.char-hidden, .char-reveal {
  position: absolute;
}

.char-hidden {
  opacity: 1;
}

.char-reveal {
  opacity: 0;
}

.character-title {
  font-size: 1.2rem;
  color: rgba(201, 169, 106, 0.6);
  font-family: serif;
  letter-spacing: 0.3em;
  cursor: default;
}

.info-ornaments {
  margin-top: 2rem;
  display: flex;
  gap: 1rem;
}

.ornament {
  color: rgba(201, 169, 106, 0.3);
  font-size: 0.8rem;
}

.scroll-indicator {
  position: absolute;
  bottom: 3rem;
  left: 50%;
  transform: translateX(-50%);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.scroll-line {
  width: 1px;
  height: 40px;
  background: linear-gradient(to bottom, rgba(201, 169, 106, 0.5), transparent);
  animation: scrollPulse 2s ease-in-out infinite;
}

@keyframes scrollPulse {
  0%, 100% { transform: scaleY(1); opacity: 1; }
  50% { transform: scaleY(0.5); opacity: 0.5; }
}

.scroll-text {
  font-size: 0.7rem;
  color: rgba(201, 169, 106, 0.4);
  letter-spacing: 0.3em;
}

.content-section {
  min-height: 100vh;
  padding: 2rem;
  background: linear-gradient(to bottom, #0a0a0f, #0f0f15);
}

.section-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(10, 10, 15, 0.9);
  backdrop-filter: blur(10px);
  padding: 1rem 0;
  border-bottom: 1px solid rgba(201, 169, 106, 0.1);
}

.nav-track {
  display: flex;
  justify-content: center;
  gap: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.nav-tab {
  cursor: pointer;
  position: relative;
}

.tab-frame {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 0.2em;
  transition: color 0.3s;
}

.nav-tab.active .tab-frame {
  color: var(--theme-primary);
}

.tab-hidden, .tab-reveal {
  position: absolute;
}

.tab-hidden {
  opacity: 1;
}

.nav-tab.active .tab-frame {
  color: var(--theme-primary);
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: var(--theme-primary);
}

.tab-content {
  max-width: 1000px;
  margin: 3rem auto;
  padding-bottom: 5rem;
}

.tab-panel {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.panel-art-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin: 3rem 0;
}

.art-line {
  width: 100px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(201, 169, 106, 0.3), transparent);
}

.art-symbol {
  color: rgba(201, 169, 106, 0.3);
  font-size: 0.8rem;
}

.bio-section {
  margin-bottom: 3rem;
}

.bio-frame {
  position: relative;
  padding: 2rem;
  border: 1px solid rgba(201, 169, 106, 0.2);
  background: rgba(20, 20, 30, 0.5);
}

.frame_corner {
  position: absolute;
  width: 15px;
  height: 15px;
  border-color: rgba(201, 169, 106, 0.4);
  border-style: solid;
}

.frame_corner.tl { top: -1px; left: -1px; border-width: 1px 0 0 1px; }
.frame_corner.tr { top: -1px; right: -1px; border-width: 1px 1px 0 0; }
.frame_corner.bl { bottom: -1px; left: -1px; border-width: 0 0 1px 1px; }
.frame_corner.br { bottom: -1px; right: -1px; border-width: 0 1px 1px 0; }

.bio-texture {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 20px,
    rgba(201, 169, 106, 0.02) 20px,
    rgba(201, 169, 106, 0.02) 40px
  );
}

.bio-text {
  font-size: 1rem;
  line-height: 2;
  color: rgba(255, 255, 255, 0.8);
  font-family: serif;
}

.text-hidden, .text-reveal {
  position: absolute;
}

.text-hidden {
  opacity: 1;
}

.text-reveal {
  opacity: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: rgba(20, 20, 30, 0.5);
  border: 1px solid rgba(201, 169, 106, 0.15);
}

.stat-icon {
  font-size: 1.5rem;
}

.stat-label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.4);
}

.stat-value {
  font-size: 0.9rem;
  color: var(--theme-primary);
  cursor: default;
}

.val-hidden, .val-reveal {
  position: absolute;
}

.val-hidden {
  opacity: 1;
}

.val-reveal {
  opacity: 0;
}

.stories-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.story-card {
  cursor: pointer;
}

.card-frame {
  position: relative;
  aspect-ratio: 16/9;
  border: 1px solid rgba(201, 169, 106, 0.2);
  overflow: hidden;
  background: rgba(20, 20, 30, 0.8);
}

.story-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.story-card:hover .story-cover {
  transform: scale(1.1);
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 50%, rgba(10, 10, 15, 0.9));
}

.overlay-gradient {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    -45deg,
    transparent,
    transparent 5px,
    rgba(201, 169, 106, 0.02) 5px,
    rgba(201, 169, 106, 0.02) 10px
  );
}

.card-number {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 2rem;
  color: rgba(201, 169, 106, 0.2);
  font-family: serif;
}

.story-info {
  padding: 1rem 0;
}

.story-chapter {
  font-size: 1.1rem;
  color: var(--theme-primary);
  font-weight: 400;
  margin-bottom: 0.25rem;
}

.chapter-hidden, .chapter-reveal {
  position: absolute;
}

.chapter-hidden {
  opacity: 1;
}

.chapter-reveal {
  opacity: 0;
}

.story-type {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 0.1em;
}

.type-hidden, .type-reveal {
  position: absolute;
}

.type-hidden {
  opacity: 1;
}

.type-reveal {
  opacity: 0;
}

.empty-stories, .empty-timeline, .empty-quotes, .empty-gallery {
  text-align: center;
  padding: 4rem;
  color: rgba(255, 255, 255, 0.3);
}

.empty-frame, .timeline-spacer, .quote-frame, .gallery-frame {
  width: 100px;
  height: 60px;
  margin: 0 auto 1rem;
  border: 1px solid rgba(201, 169, 106, 0.2);
}

.timeline-container {
  position: relative;
  padding: 2rem 0;
}

.timeline-event {
  display: flex;
  margin-bottom: 3rem;
  opacity: 0.5;
  transition: opacity 0.3s;
}

.timeline-event:hover,
.timeline-event.revealed {
  opacity: 1;
}

.timeline-event.left {
  flex-direction: row;
}

.timeline-event.right {
  flex-direction: row-reverse;
}

.event-marker {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 40px;
  flex-shrink: 0;
}

.marker-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(201, 169, 106, 0.3);
  border: 1px solid rgba(201, 169, 106, 0.5);
}

.marker-line {
  flex: 1;
  width: 1px;
  background: rgba(201, 169, 106, 0.2);
  margin-top: 0.5rem;
}

.event-card {
  flex: 1;
  background: rgba(20, 20, 30, 0.5);
  border: 1px solid rgba(201, 169, 106, 0.15);
  overflow: hidden;
  max-width: 400px;
}

.timeline-event.left .event-card {
  margin-left: 2rem;
}

.timeline-event.right .event-card {
  margin-right: 2rem;
}

.card-image {
  position: relative;
  height: 150px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent, rgba(10, 10, 15, 0.9));
}

.card-content {
  padding: 1rem;
}

.event-year {
  font-size: 0.8rem;
  color: var(--theme-primary);
  margin-bottom: 0.5rem;
  font-family: serif;
}

.year-hidden, .year-reveal {
  position: absolute;
}

.year-hidden {
  opacity: 1;
}

.year-reveal {
  opacity: 0;
}

.event-title {
  font-size: 1rem;
  color: #fff;
  font-weight: 400;
  margin-bottom: 0.5rem;
}

.title-hidden, .title-reveal {
  position: absolute;
}

.title-hidden {
  opacity: 1;
}

.title-reveal {
  opacity: 0;
}

.event-desc {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.6;
}

.desc-hidden, .desc-reveal {
  position: absolute;
}

.desc-hidden {
  opacity: 1;
}

.desc-reveal {
  opacity: 0;
}

.quotes-gallery {
  display: grid;
  gap: 2rem;
}

.quote-card {
  padding: 2rem;
  background: rgba(20, 20, 30, 0.5);
  border: 1px solid rgba(201, 169, 106, 0.15);
  position: relative;
  cursor: pointer;
}

.quote-mark {
  font-size: 3rem;
  color: rgba(201, 169, 106, 0.2);
  font-family: serif;
  line-height: 1;
}

.quote-mark.opening {
  position: absolute;
  top: 1rem;
  left: 1rem;
}

.quote-mark.closing {
  position: relative;
  display: inline-block;
  vertical-align: bottom;
}

.quote-text {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  font-family: serif;
  line-height: 1.8;
  margin: 1rem 0;
  padding: 0 2rem;
}

.quote-source {
  display: block;
  text-align: right;
  font-size: 0.85rem;
  color: rgba(201, 169, 106, 0.6);
  font-style: italic;
}

.source-hidden, .source-reveal {
  position: absolute;
}

.source-hidden {
  opacity: 1;
}

.source-reveal {
  opacity: 0;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.gallery-item {
  cursor: pointer;
}

.item-frame {
  position: relative;
  aspect-ratio: 1;
  border: 1px solid rgba(201, 169, 106, 0.2);
  overflow: hidden;
  background: rgba(20, 20, 30, 0.8);
}

.item-image, .item-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-audio {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(20, 20, 30, 0.9), rgba(10, 10, 15, 0.95));
}

.audio-icon {
  font-size: 3rem;
  color: rgba(201, 169, 106, 0.3);
}

.item-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(10, 10, 15, 0.9), transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.gallery-item:hover .item-overlay {
  opacity: 1;
}

.overlay-pattern {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 8px,
    rgba(201, 169, 106, 0.02) 8px,
    rgba(201, 169, 106, 0.02) 16px
  );
}

.item-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1rem;
  transform: translateY(100%);
  transition: transform 0.3s;
}

.gallery-item:hover .item-info {
  transform: translateY(0);
}

.item-title {
  font-size: 0.9rem;
  color: var(--theme-primary);
  font-weight: 400;
  margin-bottom: 0.25rem;
}

.title-hidden, .title-reveal {
  position: absolute;
}

.title-hidden {
  opacity: 1;
}

.title-reveal {
  opacity: 0;
}

.item-desc {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
}

.desc-hidden, .desc-reveal {
  position: absolute;
}

.desc-hidden {
  opacity: 1;
}

.desc-reveal {
  opacity: 0;
}

.back-indicator {
  position: fixed;
  top: 2rem;
  right: 2rem;
  cursor: pointer;
  z-index: 200;
}

.back-text {
  font-size: 0.75rem;
  color: rgba(201, 169, 106, 0.5);
  letter-spacing: 0.3em;
  padding: 0.75rem 1rem;
  border: 1px solid rgba(201, 169, 106, 0.2);
  transition: all 0.3s;
}

.back-indicator:hover .back-text {
  background: rgba(201, 169, 106, 0.1);
  color: var(--theme-primary);
}

.loading-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #0a0a0f;
}

.loading-frame {
  width: 100px;
  height: 120px;
  border: 1px solid rgba(201, 169, 106, 0.2);
  margin-bottom: 2rem;
}

.loading-slats {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px;
}

.slat {
  height: 8px;
  background: rgba(201, 169, 106, 0.1);
}

.loading-text {
  font-size: 0.8rem;
  color: rgba(201, 169, 106, 0.4);
  letter-spacing: 0.3em;
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    gap: 2rem;
  }
  
  .hero-frame {
    width: 250px;
    height: 350px;
  }
  
  .character-name {
    font-size: 2.5rem;
  }
  
  .nav-track {
    gap: 1rem;
    overflow-x: auto;
  }
  
  .timeline-event.left,
  .timeline-event.right {
    flex-direction: column;
  }
  
  .timeline-event.left .event-card,
  .timeline-event.right .event-card {
    margin: 0;
  }
}
</style>