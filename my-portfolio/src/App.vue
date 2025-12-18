<template>
  <Navbar />
  <RouterView v-slot="{ Component, route }">
    <Transition name="page" mode="out-in">
      <component :is="Component" :key="route.path" />
    </Transition>
  </RouterView>
  <footer class="footer">
    <div class="footer-background"></div>
    <div class="footer-content container">
      <div class="footer-main">
        <div class="footer-brand">
          <div class="footer-logo">
            <span class="logo-text">Portfolio</span>
            <span class="logo-dot">.</span>
          </div>
          <p class="footer-tagline">Built to showcase your best work.</p>
          <div class="footer-social">
            <a href="#" class="social-link" aria-label="GitHub">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
              </svg>
            </a>
            <a href="#" class="social-link" aria-label="LinkedIn">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
              </svg>
            </a>
            <a href="#" class="social-link" aria-label="Email">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M0 3v18h24v-18h-24zm6.623 7.929l-4.623 5.712v-9.458l4.623 3.746zm-4.141-5.929h19.035l-9.517 7.713-9.518-7.713zm5.694 7.188l3.824 3.099 3.83-3.104 5.612 6.817h-18.779l5.513-6.812zm9.208-1.264l4.616-3.741v9.348l-4.616-5.607z"/>
              </svg>
            </a>
            <a href="#" class="social-link" aria-label="Twitter">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"/>
              </svg>
            </a>
          </div>
        </div>
        
        <div class="footer-links-section">
          <div class="footer-column">
            <h4 class="footer-column-title">项目</h4>
            <ul class="footer-link-list">
              <li><RouterLink to="/projects" class="footer-link">所有项目</RouterLink></li>
              <li><a href="#" class="footer-link">前端项目</a></li>
              <li><a href="#" class="footer-link">全栈项目</a></li>
              <li><a href="#" class="footer-link">移动应用</a></li>
            </ul>
          </div>
          
          <div class="footer-column">
            <h4 class="footer-column-title">关于</h4>
            <ul class="footer-link-list">
              <li><RouterLink to="/about" class="footer-link">关于我</RouterLink></li>
              <li><a href="#" class="footer-link">技能</a></li>
              <li><a href="#" class="footer-link">经历</a></li>
              <li><a href="#" class="footer-link">博客</a></li>
            </ul>
          </div>
          
          <div class="footer-column">
            <h4 class="footer-column-title">联系</h4>
            <ul class="footer-link-list">
              <li><RouterLink to="/contact" class="footer-link">联系我</RouterLink></li>
              <li><a href="#" class="footer-link">邮箱</a></li>
              <li><a href="#" class="footer-link">社交媒体</a></li>
              <li><a href="#" class="footer-link">反馈</a></li>
            </ul>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p class="footer-copyright">&copy; 2025 张小明. 保留所有权利。</p>
        <div class="footer-legal">
          <a href="#" class="footer-link-small">隐私政策</a>
          <span class="footer-separator">·</span>
          <a href="#" class="footer-link-small">服务条款</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
import { RouterView, RouterLink } from 'vue-router'
</script>

<style scoped>
.footer {
  position: relative;
  background: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  margin-top: 5rem;
  padding: 4rem 0 2rem;
  transition: background-color 0.3s ease;
  overflow: hidden;
}

.footer-background {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60%;
  z-index: 0;
  /* 亮色模式下完全透明，避免颜色分层 */
  background: transparent;
}

/* 暗黑模式下显示渐变 */
.dark .footer-background {
  background: linear-gradient(135deg, rgba(129, 140, 248, 0.1) 0%, rgba(167, 139, 250, 0.1) 100%);
  opacity: 0.5;
}

.footer-content {
  position: relative;
  z-index: 1;
}

.footer-main {
  display: grid;
  grid-template-columns: 2fr 3fr;
  gap: 4rem;
  margin-bottom: 3rem;
}

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-logo {
  display: flex;
  align-items: baseline;
  gap: 2px;
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.footer-logo .logo-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-logo .logo-dot {
  color: var(--accent-primary);
  font-size: 1.2em;
}

.footer-tagline {
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin: 0;
  line-height: 1.6;
}

.footer-social {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--bg-primary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
}

.social-link:hover {
  background: var(--gradient-primary);
  color: white;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.footer-links-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.footer-column-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 1rem 0;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.footer-link-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.footer-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.9rem;
  transition: all 0.3s;
  display: inline-block;
  position: relative;
}

.footer-link::before {
  content: '';
  position: absolute;
  left: -0.5rem;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--accent-primary);
  opacity: 0;
  transition: opacity 0.3s;
}

.footer-link:hover {
  color: var(--accent-primary);
  transform: translateX(4px);
}

.footer-link:hover::before {
  opacity: 1;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 2rem;
  border-top: 1px solid var(--border-color);
  flex-wrap: wrap;
  gap: 1rem;
}

.footer-copyright {
  color: var(--text-secondary);
  font-size: 0.875rem;
  margin: 0;
}

.footer-legal {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.footer-link-small {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.875rem;
  transition: color 0.3s;
}

.footer-link-small:hover {
  color: var(--accent-primary);
}

.footer-separator {
  color: var(--text-tertiary);
}

@media (max-width: 968px) {
  .footer-main {
    grid-template-columns: 1fr;
    gap: 3rem;
  }
  
  .footer-links-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .footer {
    padding: 3rem 0 1.5rem;
  }
  
  .footer-links-section {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .footer-bottom {
    flex-direction: column;
    text-align: center;
  }
  
  .footer-social {
    justify-content: center;
  }
}
</style>