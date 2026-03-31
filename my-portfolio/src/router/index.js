import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const HomeView = () => import('../views/HomeView.vue')
const ProjectsView = () => import('../views/ProjectsView.vue')
const AboutView = () => import('../views/AboutView.vue')
const ContactView = () => import('../views/ContactView.vue')
const TestimonialsView = () => import('../views/TestimonialsView.vue')
const LoginView = () => import('../views/LoginView.vue')

const AdminLayout = () => import('../views/admin/AdminLayout.vue')
const DashboardView = () => import('../views/admin/DashboardView.vue')
const MessagesView = () => import('../views/admin/MessagesView.vue')
const DataScreenView = () => import('../views/admin/DataScreenView.vue')
const ArticleEditorView = () => import('../views/admin/ArticleEditorView.vue')
const ArticlesView = () => import('../views/admin/ArticlesView.vue')
const GalleryView = () => import('../views/admin/GalleryView.vue')

const routes = [
  { path: '/', component: HomeView, name: 'Home' },
  { path: '/projects', component: ProjectsView, name: 'Projects' },
  { path: '/about', component: AboutView, name: 'About' },
  { path: '/contact', component: ContactView, name: 'Contact' },
  { path: '/testimonials', component: TestimonialsView, name: 'Testimonials' },
  { path: '/login', component: LoginView, name: 'Login' },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', component: DashboardView, name: 'AdminDashboard' },
      { path: 'articles', component: ArticlesView, name: 'AdminArticles' },
      { path: 'editor', component: ArticleEditorView, name: 'ArticleEditor' },
      { path: 'editor/:id', component: ArticleEditorView, name: 'ArticleEdit' },
      { path: 'gallery', component: GalleryView, name: 'AdminGallery' },
      { path: 'messages', component: MessagesView, name: 'AdminMessages' },
      { path: 'screen', component: DataScreenView, name: 'DataScreen' }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  if (to.path === '/login' && authStore.isAuthenticated) {
    next('/admin')
    return
  }

  next()
})

export default router