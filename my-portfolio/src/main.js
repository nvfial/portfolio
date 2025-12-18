import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import './utils/visitor' // 自动追踪访问

const app = createApp(App)
app.use(router)

// 确保HTML有正确的初始class
const html = document.documentElement
const savedTheme = localStorage.getItem('portfolio-theme')
if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
  html.classList.add('dark')
  html.classList.remove('light')
} else {
  html.classList.add('light')
  html.classList.remove('dark')
}

app.mount('#app')