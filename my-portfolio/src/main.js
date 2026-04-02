import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import './utils/visitor'
import 'highlight.js/styles/github-dark.css'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus)

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