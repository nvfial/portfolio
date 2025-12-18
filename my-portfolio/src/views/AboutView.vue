<template>
  <section class="about">
    <ParticlesBackground id="about-particles" />
    <div class="container">
      <h2 class="section-title">关于我</h2>
      
      <div class="about-content">
        <div class="about-main">
          <div class="about-text">
            <div class="intro-text">
              <p class="lead">
                我是一名<span class="highlight">充满热情</span>的前端开发者，拥有 3+ 年 Web 开发经验。
                专注于创建<span class="highlight">美观、高效、用户友好</span>的数字体验。
              </p>
              <p>
                我相信简洁的设计和高效的代码是优秀产品的基石。我擅长将复杂的技术转化为直观的用户界面，
                让技术服务于用户需求。
              </p>
              <p>
                业余时间我喜欢学习新技术、写技术博客、参与开源项目，以及探索最新的设计趋势和开发工具。
              </p>
            </div>
            
            <!-- 技能展示 -->
            <div class="skills-section">
              <h3>技能专长</h3>
              <div class="skills-grid">
                <div 
                  v-for="skill in skills" 
                  :key="skill.name"
                  class="skill-item"
                  :style="{ '--skill-level': skill.level + '%' }"
                >
                  <div class="skill-header">
                    <span class="skill-name">{{ skill.name }}</span>
                    <span class="skill-level">{{ skill.level }}%</span>
                  </div>
                  <div class="skill-bar">
                    <div class="skill-progress"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="about-image">
            <div class="image-wrapper">
              <div class="image-glow"></div>
              <img src="https://via.placeholder.com/400x500?text=Me" alt="张小明" />
              <div class="image-decoration"></div>
            </div>
          </div>
        </div>
        
        <!-- 时间线 -->
        <div class="timeline-section">
          <h3>工作经历</h3>
          <div class="timeline">
            <div 
              v-for="(experience, index) in experiences" 
              :key="index"
              class="timeline-item"
              :class="{ 'timeline-item-right': index % 2 === 1 }"
            >
              <div class="timeline-marker"></div>
              <div class="timeline-content">
                <div class="timeline-date">{{ experience.date }}</div>
                <h4>{{ experience.title }}</h4>
                <p class="timeline-company">{{ experience.company }}</p>
                <p class="timeline-description">{{ experience.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted } from 'vue'
import ParticlesBackground from '../components/ParticlesBackground.vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const skills = [
  { name: 'Vue.js', level: 90 },
  { name: 'JavaScript', level: 85 },
  { name: 'TypeScript', level: 75 },
  { name: 'React', level: 80 },
  { name: 'CSS/SCSS', level: 90 },
  { name: 'UI/UX Design', level: 70 },
  { name: 'Node.js', level: 75 },
  { name: 'Git', level: 85 }
]

const experiences = [
  {
    date: '2022 - 至今',
    title: '高级前端工程师',
    company: '科技公司',
    description: '负责核心产品的前端开发，使用 Vue 3 构建高性能的用户界面，优化用户体验和页面性能。'
  },
  {
    date: '2020 - 2022',
    title: '前端开发工程师',
    company: '互联网公司',
    description: '参与多个项目的开发，包括电商平台、管理系统等，积累了丰富的实战经验。'
  },
  {
    date: '2019 - 2020',
    title: '初级前端开发',
    company: '创业公司',
    description: '开始前端开发之旅，学习并实践各种前端技术，快速成长。'
  }
]

onMounted(() => {
  // 技能条动画
  gsap.from('.skill-progress', {
    width: 0,
    duration: 1.5,
    ease: 'power2.out',
    stagger: 0.1,
    scrollTrigger: {
      trigger: '.skills-section',
      start: 'top 80%'
    }
  })
  
  // 时间线动画
  gsap.from('.timeline-item', {
    opacity: 0,
    x: -50,
    duration: 0.8,
    stagger: 0.2,
    scrollTrigger: {
      trigger: '.timeline',
      start: 'top 80%'
    }
  })
})
</script>

<style scoped>
.about {
  position: relative;
  z-index: 1;
  padding: 8rem 0;
  background: var(--bg-primary);
  min-height: 100vh;
}

.about-content {
  max-width: 1200px;
  margin: 0 auto;
}

.about-main {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 4rem;
  margin-bottom: 6rem;
  align-items: start;
}

.about-text {
  display: flex;
  flex-direction: column;
  gap: 3rem;
}

.intro-text p {
  color: var(--text-secondary);
  line-height: 1.8;
  font-size: 1.05rem;
  margin-bottom: 1.5rem;
}

.intro-text .lead {
  font-size: 1.2rem;
  color: var(--text-primary);
  font-weight: 500;
}

.highlight {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 600;
}

.skills-section h3 {
  font-size: 1.5rem;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.skills-grid {
  display: grid;
  gap: 1.5rem;
}

.skill-item {
  --skill-level: 0%;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.skill-name {
  font-weight: 500;
  color: var(--text-primary);
}

.skill-level {
  color: var(--accent-primary);
  font-weight: 600;
}

.skill-bar {
  height: 8px;
  background: var(--bg-tertiary);
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.skill-progress {
  height: 100%;
  width: var(--skill-level);
  background: var(--gradient-primary);
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.skill-progress::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.about-image {
  position: relative;
  height: 500px;
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--shadow-xl);
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: relative;
  z-index: 2;
}

.image-glow {
  position: absolute;
  inset: -20px;
  background: var(--gradient-primary);
  opacity: 0.3;
  filter: blur(40px);
  z-index: 1;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.1); }
}

.image-decoration {
  position: absolute;
  top: -50px;
  right: -50px;
  width: 200px;
  height: 200px;
  background: var(--gradient-secondary);
  border-radius: 50%;
  opacity: 0.2;
  z-index: 0;
}

.timeline-section {
  margin-top: 6rem;
}

.timeline-section h3 {
  font-size: 1.5rem;
  color: var(--text-primary);
  margin-bottom: 3rem;
  text-align: center;
  font-weight: 600;
}

.timeline {
  position: relative;
  padding-left: 2rem;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--gradient-primary);
}

.timeline-item {
  position: relative;
  padding-left: 3rem;
  padding-bottom: 3rem;
}

.timeline-marker {
  position: absolute;
  left: -1.5rem;
  top: 0.5rem;
  width: 16px;
  height: 16px;
  background: var(--bg-primary);
  border: 3px solid var(--accent-primary);
  border-radius: 50%;
  z-index: 1;
}

.timeline-content {
  background: var(--bg-secondary);
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  transition: all 0.3s;
}

.timeline-item:hover .timeline-content {
  transform: translateX(10px);
  box-shadow: var(--shadow-md);
}

.timeline-date {
  color: var(--accent-primary);
  font-weight: 600;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.timeline-content h4 {
  font-size: 1.25rem;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
  font-weight: 600;
}

.timeline-company {
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin-bottom: 0.75rem;
  font-weight: 500;
}

.timeline-description {
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: 0.95rem;
}

@media (max-width: 968px) {
  .about-main {
    grid-template-columns: 1fr;
    gap: 3rem;
  }
  
  .about-image {
    height: 400px;
  }
}

@media (max-width: 768px) {
  .about {
    padding: 5rem 0;
  }
  
  .timeline {
    padding-left: 1.5rem;
  }
  
  .timeline-item {
    padding-left: 2rem;
  }
}
</style>
