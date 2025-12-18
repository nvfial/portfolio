import { createRouter, createWebHistory } from 'vue-router'

// 懒加载视图组件
const HomeView = () => import('../views/HomeView.vue')
const ProjectsView = () => import('../views/ProjectsView.vue')
const AboutView = () => import('../views/AboutView.vue')
const ContactView = () => import('../views/ContactView.vue')
const TestimonialsView = () => import('../views/TestimonialsView.vue')

// 管理后台
const AdminLayout = () => import('../views/admin/AdminLayout.vue')
const DashboardView = () => import('../views/admin/DashboardView.vue')
const MessagesView = () => import('../views/admin/MessagesView.vue')
const DataScreenView = () => import('../views/admin/DataScreenView.vue')

const routes = [
  { path: '/', component: HomeView, name: 'Home' },
  { path: '/projects', component: ProjectsView, name: 'Projects' },
  { path: '/about', component: AboutView, name: 'About' },
  { path: '/contact', component: ContactView, name: 'Contact' },
  { path: '/testimonials', component: TestimonialsView, name: 'Testimonials' },
  // 管理后台路由
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: '', component: DashboardView, name: 'AdminDashboard' },
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

export default router