<template>
  <div class="cast-page" ref="pageRef">
    <div class="cast-background">
      <div class="bg-texture"></div>
      <div class="bg-particles" ref="particlesRef"></div>
    </div>
    
    <div class="cast-container">
      <div class="art-divider top-divider">
        <div class="divider-line"></div>
        <div class="divider-ornament left">◆</div>
        <div class="divider-ornament right">◆</div>
      </div>
      
      <div class="cast-title-wrapper">
        <h1 class="cast-title">
          <span class="title-char" v-for="(char, i) in '角色一览'" :key="i">{{ char }}</span>
        </h1>
        <p class="cast-subtitle">点击头像揭示身份</p>
      </div>
      
      <div class="characters-showcase" v-if="characters.length > 0">
        <div 
          v-for="(character, index) in characters" 
          :key="character.id"
          class="character-showcase"
          :class="{ 'revealed': revealedCharacters.includes(character.id) }"
          :style="getCharacterTheme(character)"
          @click="selectCharacter(character.id)"
        >
          <div class="showcase-frame">
            <div class="frame-corner top-left"></div>
            <div class="frame-corner top-right"></div>
            <div class="frame-corner bottom-left"></div>
            <div class="frame-corner bottom-right"></div>
            
            <div class="showcase-image-wrapper">
              <img 
                :src="character.portraitUrl || '/images/placeholder-portrait.jpg'" 
                :alt="character.name"
                class="showcase-image"
              />
              <div class="image-overlay">
                <div class="overlay-pattern"></div>
              </div>
            </div>
            
            <div class="showcase-placeholder" v-if="!revealedCharacters.includes(character.id)">
              <div class="mystery-mark">?</div>
              <div class="reveal-hint">点击揭示</div>
            </div>
          </div>
          
          <div class="showcase-info" v-if="revealedCharacters.includes(character.id)">
            <h2 class="showcase-name">{{ character.name }}</h2>
            <p class="showcase-title" v-if="character.title">{{ character.title }}</p>
            <div class="info-divider"></div>
            <p class="showcase-desc">{{ character.shortDesc }}</p>
          </div>
          
          <div class="showcase-label" v-if="revealedCharacters.includes(character.id)">
            <span class="label-number">{{ String(index + 1).padStart(2, '0') }}</span>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-realm">
        <div class="empty-illustration">
          <div class="illustration-frame">
            <div class="frame-slats">
              <div class="slat" v-for="i in 5" :key="i"></div>
            </div>
          </div>
        </div>
        <p class="empty-text">暂无角色记录</p>
      </div>
      
      <div class="art-divider bottom-divider">
        <div class="divider-line"></div>
      </div>
      
      <div class="cast-progress">
        <div class="progress-text">
          <span>{{ revealedCount }} / {{ characters.length }}</span>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { characterApi } from '../utils/api'

const router = useRouter()
const pageRef = ref(null)
const particlesRef = ref(null)
const characters = ref([])
const revealedCharacters = ref([])
const isLoading = ref(true)

const revealedCount = computed(() => revealedCharacters.value.length)
const progressPercent = computed(() => {
  if (characters.value.length === 0) return 0
  return (revealedCount.value / characters.value.length) * 100
})

const getCharacterTheme = (char) => {
  const presets = {
    golden: { primary: '#c9a96a', secondary: '#8b7355' },
    silver: { primary: '#b8c4ce', secondary: '#7a8a99' },
    blue: { primary: '#4a90d9', secondary: '#2c5aa0' },
    purple: { primary: '#9b59b6', secondary: '#6c3483' },
    red: { primary: '#e74c3c', secondary: '#922b21' },
    green: { primary: '#27ae60', secondary: '#1e8449' },
    cyan: { primary: '#1abc9c', secondary: '#16a085' },
    pink: { primary: '#ff6b9d', secondary: '#c44569' },
    black: { primary: '#2c3e50', secondary: '#1a252f' },
    white: { primary: '#ecf0f1', secondary: '#bdc3c7' }
  }
  
  let primary = char.themeColor || '#c9a96a'
  let secondary = char.themeColorSecondary || '#8b7355'
  
  if (char.themePreset && char.themePreset !== 'custom' && presets[char.themePreset]) {
    primary = char.themeColor || presets[char.themePreset].primary
    secondary = char.themeColorSecondary || presets[char.themePreset].secondary
  }
  
  if (char.customThemeConfig) {
    try {
      const parsed = JSON.parse(char.customThemeConfig)
      if (parsed.primary) primary = parsed.primary
      if (parsed.secondary) secondary = parsed.secondary
    } catch (e) {}
  }
  
  return {
    '--char-primary': primary,
    '--char-secondary': secondary
  }
}

const loadCharacters = async () => {
  try {
    const data = await characterApi.getActive()
    characters.value = data
    isLoading.value = false
    
    nextTick(() => {
      initAnimations()
      createParticles()
    })
  } catch (error) {
    console.error('Failed to load characters:', error)
    isLoading.value = false
  }
}

const initAnimations = () => {
  gsap.from('.art-divider', {
    scaleX: 0,
    duration: 1,
    ease: 'power2.out'
  })
  
  gsap.from('.title-char', {
    opacity: 0,
    y: 30,
    rotateX: -90,
    stagger: 0.1,
    duration: 0.8,
    delay: 0.5
  })
  
  gsap.from('.showcase-frame', {
    opacity: 0,
    scale: 0.8,
    stagger: 0.15,
    duration: 0.6,
    delay: 1
  })
}

const createParticles = () => {
  if (!particlesRef.value) return
  
  for (let i = 0; i < 30; i++) {
    const particle = document.createElement('div')
    particle.className = 'particle'
    particle.style.setProperty('--x', Math.random() * 100 + '%')
    particle.style.setProperty('--y', Math.random() * 100 + '%')
    particle.style.setProperty('--delay', Math.random() * 5 + 's')
    particle.style.setProperty('--duration', (Math.random() * 10 + 10) + 's')
    particlesRef.value.appendChild(particle)
  }
}

const selectCharacter = (id) => {
  if (!revealedCharacters.value.includes(id)) {
    revealedCharacters.value.push(id)
    
    gsap.to(`.character-showcase:nth-child(${characters.value.findIndex(c => c.id === id) + 1}) .showcase-info`, {
      opacity: 1,
      y: 0,
      duration: 0.5,
      ease: 'power2.out'
    })
  }
  
  setTimeout(() => {
    router.push(`/character/${id}`)
  }, 800)
}

onMounted(() => {
  loadCharacters()
})
</script>

<style scoped>
.character-showcase {
  --default-primary: #c9a96a;
  --default-secondary: #8b7355;
}

.character-showcase .showcase-frame {
  border-color: var(--char-primary, var(--default-primary));
  border-color: color-mix(in srgb, var(--char-primary, var(--default-primary)) 30%, transparent);
}

.character-showcase .frame-corner {
  border-color: var(--char-primary, var(--default-primary));
  border-color: color-mix(in srgb, var(--char-primary, var(--default-primary)) 60%, transparent);
}

.character-showcase .showcase-name {
  color: var(--char-primary, var(--default-primary));
}

.character-showcase .info-divider {
  background: linear-gradient(90deg, transparent, var(--char-primary, var(--default-primary)), transparent);
}

.cast-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: #0a0a0f;
}

.cast-background {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.bg-texture {
  position: absolute;
  inset: 0;
  background: 
    repeating-linear-gradient(
      90deg,
      transparent,
      transparent 50px,
      rgba(255, 255, 255, 0.02) 50px,
      rgba(255, 255, 255, 0.02) 51px
    ),
    repeating-linear-gradient(
      0deg,
      transparent,
      transparent 50px,
      rgba(255, 255, 255, 0.02) 50px,
      rgba(255, 255, 255, 0.02) 51px
    );
  opacity: 0.5;
}

.bg-particles {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

:deep(.particle) {
  position: absolute;
  width: 4px;
  height: 4px;
  background: radial-gradient(circle, rgba(201, 169, 106, 0.6), transparent);
  border-radius: 50%;
  left: var(--x);
  top: var(--y);
  animation: float var(--duration) ease-in-out infinite;
  animation-delay: var(--delay);
  opacity: 0;
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0) scale(1); 
    opacity: 0; 
  }
  10% { opacity: 0.8; }
  90% { opacity: 0.8; }
  50% { 
    transform: translateY(-100px) scale(0.5); 
    opacity: 0.3;
  }
}

.cast-container {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 4rem 2rem;
}

.art-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 2rem 0;
}

.divider-line {
  width: 200px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(201, 169, 106, 0.5), transparent);
}

.divider-ornament {
  color: rgba(201, 169, 106, 0.4);
  font-size: 0.8rem;
  position: absolute;
}

.divider-ornament.left { left: 0; }
.divider-ornament.right { right: 0; }

.cast-title-wrapper {
  text-align: center;
  margin-bottom: 4rem;
}

.cast-title {
  font-size: 4rem;
  font-weight: 300;
  color: #c9a96a;
  letter-spacing: 0.5em;
  margin-bottom: 1rem;
  perspective: 1000px;
}

.title-char {
  display: inline-block;
  text-shadow: 0 0 30px rgba(201, 169, 106, 0.3);
}

.cast-subtitle {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 0.3em;
  font-family: serif;
}

.characters-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 3rem;
  padding: 2rem 0;
}

.character-showcase {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.character-showcase:hover {
  transform: translateY(-10px);
}

.showcase-frame {
  position: relative;
  aspect-ratio: 3/4;
  border: 1px solid rgba(201, 169, 106, 0.3);
  background: linear-gradient(135deg, rgba(20, 20, 30, 0.9), rgba(10, 10, 15, 0.95));
  overflow: hidden;
}

.frame-corner {
  position: absolute;
  width: 20px;
  height: 20px;
  border-color: rgba(201, 169, 106, 0.6);
  border-style: solid;
}

.frame-corner.top-left {
  top: -1px; left: -1px;
  border-width: 2px 0 0 2px;
}

.frame-corner.top-right {
  top: -1px; right: -1px;
  border-width: 2px 2px 0 0;
}

.frame-corner.bottom-left {
  bottom: -1px; left: -1px;
  border-width: 0 0 2px 2px;
}

.frame-corner.bottom-right {
  bottom: -1px; right: -1px;
  border-width: 0 2px 2px 0;
}

.showcase-image-wrapper {
  position: absolute;
  inset: 15px;
}

.showcase-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: grayscale(30%) contrast(1.1);
  transition: all 0.5s ease;
}

.character-showcase:hover .showcase-image {
  filter: grayscale(0%) contrast(1);
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    transparent 50%,
    rgba(10, 10, 15, 0.9) 100%
  );
}

.overlay-pattern {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 10px,
    rgba(201, 169, 106, 0.03) 10px,
    rgba(201, 169, 106, 0.03) 20px
  );
}

.showcase-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(20, 20, 30, 0.95), rgba(10, 10, 15, 0.98));
}

.mystery-mark {
  font-size: 4rem;
  color: rgba(201, 169, 106, 0.3);
  font-family: serif;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.1); }
}

.reveal-hint {
  font-size: 0.8rem;
  color: rgba(201, 169, 106, 0.4);
  margin-top: 1rem;
  letter-spacing: 0.2em;
}

.showcase-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1.5rem;
  background: linear-gradient(to top, rgba(10, 10, 15, 0.98), transparent);
  transform: translateY(20px);
  opacity: 0;
}

.showcase-name {
  font-size: 1.5rem;
  color: #c9a96a;
  font-weight: 400;
  font-family: serif;
  letter-spacing: 0.1em;
}

.showcase-title {
  font-size: 0.85rem;
  color: rgba(201, 169, 106, 0.7);
  margin-top: 0.25rem;
}

.info-divider {
  width: 30px;
  height: 1px;
  background: rgba(201, 169, 106, 0.5);
  margin: 0.75rem 0;
}

.showcase-desc {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.showcase-label {
  position: absolute;
  top: 10px;
  right: 10px;
}

.label-number {
  font-size: 2rem;
  color: rgba(201, 169, 106, 0.2);
  font-family: serif;
  font-weight: 300;
}

.empty-realm {
  text-align: center;
  padding: 6rem 2rem;
}

.empty-illustration {
  margin-bottom: 2rem;
}

.illustration-frame {
  width: 150px;
  height: 200px;
  margin: 0 auto;
  border: 1px solid rgba(201, 169, 106, 0.2);
  position: relative;
  overflow: hidden;
}

.frame-slats {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px;
}

.slat {
  height: 20px;
  background: rgba(201, 169, 106, 0.1);
  border: 1px solid rgba(201, 169, 106, 0.2);
  transform: rotate(-2deg);
}

.slat:nth-child(even) {
  transform: rotate(2deg);
}

.empty-text {
  color: rgba(255, 255, 255, 0.3);
  font-family: serif;
  font-style: italic;
}

.cast-progress {
  text-align: center;
  margin-top: 3rem;
}

.progress-text {
  font-size: 0.9rem;
  color: rgba(201, 169, 106, 0.5);
  margin-bottom: 0.5rem;
  letter-spacing: 0.2em;
}

.progress-bar {
  width: 200px;
  height: 2px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0 auto;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(201, 169, 106, 0.8), transparent);
  transition: width 0.5s ease;
}

@media (max-width: 768px) {
  .cast-title {
    font-size: 2.5rem;
    letter-spacing: 0.2em;
  }
  
  .characters-showcase {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
  }
  
  .showcase-frame {
    aspect-ratio: 2/3;
  }
}
</style>