<template>
  <div class="home-page">
    <ParticleField 
      :particle-count="120" 
      :connection-distance="100"
      :speed="0.4"
    />
    
    <header class="hero" ref="heroRef">
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-dot"></span>
          知识 · 艺术 · 探索
        </div>
        
        <h1 class="hero-title">
          <span class="title-line">探索知识的</span>
          <span class="title-line gradient-text">无限可能</span>
        </h1>
        
        <p class="hero-description">
          一个融合艺术展示与知识库的顶级个人平台。<br/>
          在动态的点线面中，发现知识的脉络。
        </p>
        
        <div class="hero-actions">
          <NuxtLink to="/knowledge" class="btn btn-primary">
            进入知识库
            <span class="btn-arrow">→</span>
          </NuxtLink>
          
          <NuxtLink to="/gallery" class="btn btn-outline">
            艺术画廊
          </NuxtLink>
        </div>
      </div>
      
      <div class="hero-visual">
        <NeuralNetwork :node-count="60" :interactive="false" />
      </div>
    </header>
    
    <section class="domains-section">
      <div class="container">
        <h2 class="section-title">知识领域</h2>
        <p class="section-subtitle">选择一个领域，开启你的探索之旅</p>
        
        <div class="domains-grid">
          <div 
            v-for="domain in domains" 
            :key="domain.id"
            class="domain-card"
            :style="{ '--domain-color': domain.color }"
            @click="navigateTo(`/knowledge/${domain.slug}`)"
          >
            <div class="domain-icon">{{ domain.icon }}</div>
            <h3 class="domain-name">{{ domain.name }}</h3>
            <p class="domain-description">{{ domain.description }}</p>
            <div class="domain-stats">
              <span>{{ domain.categoryCount }} 分类</span>
              <span>·</span>
              <span>{{ domain.articleCount }} 文章</span>
            </div>
            <div class="domain-arrow">→</div>
          </div>
        </div>
      </div>
    </section>
    
    <section class="features-section">
      <div class="container">
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🧠</div>
            <h3>知识图谱</h3>
            <p>通过交互式节点网络，直观理解知识间的关联</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">🎨</div>
            <h3>艺术展示</h3>
            <p>沉浸式画廊体验，感受视觉艺术的魅力</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">💭</div>
            <h3>思维导图</h3>
            <p>动态3D可视化，呈现思维的流动与演变</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">💬</div>
            <h3>互动留言</h3>
            <p>分享你的见解，与访客进行深度交流</p>
          </div>
        </div>
      </div>
    </section>
    
    <footer class="home-footer">
      <div class="container">
        <p>© 2025 Portfolio. 构建你的数字知识殿堂。</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ParticleField from '~/components/art/ParticleField.vue'
import NeuralNetwork from '~/components/art/NeuralNetwork.vue'

const heroRef = ref(null)

const domains = [
  {
    id: 1,
    name: '前端开发',
    slug: 'frontend',
    icon: '⚛️',
    description: 'Vue、React、TypeScript等现代前端技术',
    color: '#667eea',
    categoryCount: 8,
    articleCount: 42
  },
  {
    id: 2,
    name: '后端架构',
    slug: 'backend',
    icon: '🚀',
    description: 'Spring Boot、Node.js、微服务架构',
    color: '#764ba2',
    categoryCount: 6,
    articleCount: 35
  },
  {
    id: 3,
    name: '人工智能',
    slug: 'ai',
    icon: '🤖',
    description: '机器学习、深度学习、LLM应用',
    color: '#f093fb',
    categoryCount: 5,
    articleCount: 28
  },
  {
    id: 4,
    name: '设计艺术',
    slug: 'design',
    icon: '🎭',
    description: 'UI/UX设计、3D艺术、创意视觉',
    color: '#4facfe',
    categoryCount: 4,
    articleCount: 20
  }
]

const navigateTo = (path) => {
  useRouter().push(path)
}
</script>

<style scoped>
.home-page {
  position: relative;
  min-height: 100vh;
}

.hero {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6rem 4rem 4rem;
  gap: 4rem;
  position: relative;
}

.hero-content {
  flex: 1;
  max-width: 600px;
  z-index: 1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: rgba(102, 126, 234, 0.1);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: var(--radius-full);
  font-size: 0.85rem;
  color: var(--accent-primary);
  margin-bottom: 2rem;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: var(--accent-primary);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.hero-title {
  font-size: clamp(3rem, 8vw, 5rem);
  font-weight: 700;
  line-height: 1.1;
  margin-bottom: 1.5rem;
}

.title-line {
  display: block;
}

.gradient-text {
  background: var(--gradient-cosmic);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 1.25rem;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 2.5rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.btn-arrow {
  transition: transform var(--transition-fast);
}

.btn:hover .btn-arrow {
  transform: translateX(4px);
}

.hero-visual {
  flex: 1;
  height: 100%;
  min-height: 500px;
  position: relative;
}

.domains-section {
  padding: 6rem 0;
  background: var(--bg-secondary);
}

.domains-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 3rem;
}

.domain-card {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  padding: 2rem;
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.domain-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--domain-color);
}

.domain-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border-color: var(--domain-color);
}

.domain-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.domain-name {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
}

.domain-description {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 1rem;
}

.domain-stats {
  display: flex;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: var(--text-tertiary);
}

.domain-arrow {
  position: absolute;
  bottom: 2rem;
  right: 2rem;
  font-size: 1.5rem;
  color: var(--domain-color);
  opacity: 0;
  transform: translateX(-10px);
  transition: all var(--transition-base);
}

.domain-card:hover .domain-arrow {
  opacity: 1;
  transform: translateX(0);
}

.features-section {
  padding: 6rem 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.feature-card {
  text-align: center;
  padding: 2rem;
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: var(--text-primary);
}

.feature-card p {
  font-size: 0.95rem;
  color: var(--text-secondary);
}

.home-footer {
  padding: 2rem 0;
  text-align: center;
  border-top: 1px solid var(--border-color);
  color: var(--text-tertiary);
  font-size: 0.9rem;
}

@media (max-width: 1024px) {
  .hero {
    flex-direction: column;
    text-align: center;
    padding: 6rem 2rem 4rem;
  }
  
  .hero-content {
    max-width: 100%;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .hero-visual {
    width: 100%;
    min-height: 400px;
  }
}
</style>
