<template>
  <div class="data-screen">
    <div class="screen-header">
      <h1>数据大屏</h1>
      <div class="screen-time">{{ currentTime }}</div>
    </div>

    <div class="screen-grid">
      <!-- 核心指标 -->
      <div class="metric-card large">
        <h3>总访问量</h3>
        <div class="metric-value">{{ dashboardData.visits?.total || 0 }}</div>
        <div class="metric-trend">
          <span>今日: {{ dashboardData.visits?.today || 0 }}</span>
          <span>本周: {{ dashboardData.visits?.week || 0 }}</span>
        </div>
      </div>

      <div class="metric-card">
        <h3>独立IP</h3>
        <div class="metric-value">{{ dashboardData.ips?.totalIps || 0 }}</div>
      </div>

      <div class="metric-card">
        <h3>消息总数</h3>
        <div class="metric-value">{{ dashboardData.messages?.total || 0 }}</div>
      </div>

      <div class="metric-card">
        <h3>项目数量</h3>
        <div class="metric-value">{{ dashboardData.projects?.total || 0 }}</div>
      </div>

      <!-- 访问趋势图 -->
      <div class="chart-card large">
        <h3>访问趋势</h3>
        <div ref="trendChart" class="chart"></div>
      </div>

      <!-- IP分布 -->
      <div class="chart-card">
        <h3>IP访问排行</h3>
        <div ref="ipChart" class="chart"></div>
      </div>

      <!-- 页面访问分布 -->
      <div class="chart-card">
        <h3>页面访问分布</h3>
        <div ref="pathChart" class="chart"></div>
      </div>

      <!-- 项目稳定度 -->
      <div class="stability-card large">
        <h3>项目稳定度</h3>
        <div class="stability-list">
          <div v-for="project in projects" :key="project.id" class="stability-item">
            <div class="project-name">{{ project.title }}</div>
            <div class="stability-bar">
              <div 
                class="stability-fill" 
                :style="{ width: calculateStability(project) + '%' }"
                :class="getStabilityClass(project)"
              ></div>
            </div>
            <div class="stability-value">{{ calculateStability(project) }}%</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { statsApi, projectApi } from '../../utils/api'
import * as echarts from 'echarts'

const dashboardData = ref({})
const projects = ref([])
const currentTime = ref('')
const trendChart = ref(null)
const ipChart = ref(null)
const pathChart = ref(null)

let trendChartInstance = null
let ipChartInstance = null
let pathChartInstance = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN')
}

const loadData = async () => {
  try {
    dashboardData.value = await statsApi.getDashboardData()
    projects.value = await projectApi.getAll()
    
    updateCharts()
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const updateCharts = () => {
  // 访问趋势图
  if (dashboardData.value.visits?.trends && trendChart.value) {
    if (!trendChartInstance) {
      trendChartInstance = echarts.init(trendChart.value)
    }
    trendChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: dashboardData.value.visits.trends.map(t => t.date)
      },
      yAxis: { type: 'value' },
      series: [{
        data: dashboardData.value.visits.trends.map(t => t.count),
        type: 'line',
        smooth: true,
        areaStyle: { color: 'rgba(99, 102, 241, 0.3)' },
        itemStyle: { color: '#6366f1' },
        lineStyle: { width: 3 }
      }]
    })
  }

  // IP排行图
  if (dashboardData.value.ips?.topIps && ipChart.value) {
    if (!ipChartInstance) {
      ipChartInstance = echarts.init(ipChart.value)
    }
    const top5 = dashboardData.value.ips.topIps.slice(0, 5)
    ipChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: top5.map(ip => ip.ip)
      },
      series: [{
        data: top5.map(ip => ip.count),
        type: 'bar',
        itemStyle: { color: '#10b981' }
      }]
    })
  }

  // 页面访问分布
  if (dashboardData.value.paths?.topPaths && pathChart.value) {
    if (!pathChartInstance) {
      pathChartInstance = echarts.init(pathChart.value)
    }
    pathChartInstance.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        data: dashboardData.value.paths.topPaths.map(p => ({
          value: p.count,
          name: p.path
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    })
  }
}

const calculateStability = (project) => {
  // 模拟稳定度计算（实际应该基于错误率、响应时间等）
  return Math.floor(Math.random() * 20) + 80 // 80-100%
}

const getStabilityClass = (project) => {
  const stability = calculateStability(project)
  if (stability >= 95) return 'excellent'
  if (stability >= 85) return 'good'
  return 'warning'
}

let timeInterval = null
let dataInterval = null

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  loadData()
  dataInterval = setInterval(loadData, 30000) // 每30秒刷新
  
  // 窗口大小变化时重新调整图表
  window.addEventListener('resize', () => {
    if (trendChartInstance) trendChartInstance.resize()
    if (ipChartInstance) ipChartInstance.resize()
    if (pathChartInstance) pathChartInstance.resize()
  })
})

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
  if (dataInterval) clearInterval(dataInterval)
  if (trendChartInstance) trendChartInstance.dispose()
  if (ipChartInstance) ipChartInstance.dispose()
  if (pathChartInstance) pathChartInstance.dispose()
})
</script>

<style scoped>
.data-screen {
  padding: 2rem;
  background: #0a0e27;
  color: #fff;
  min-height: 100vh;
}

.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.screen-header h1 {
  font-size: 2.5rem;
  margin: 0;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.screen-time {
  font-size: 1.2rem;
  color: #8b5cf6;
}

.screen-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.metric-card {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.metric-card.large {
  grid-column: span 2;
}

.metric-card h3 {
  margin: 0 0 1rem 0;
  color: #94a3b8;
  font-size: 0.9rem;
}

.metric-value {
  font-size: 3rem;
  font-weight: bold;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.metric-trend {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
  font-size: 0.9rem;
  color: #94a3b8;
}

.chart-card {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.chart-card.large {
  grid-column: span 2;
}

.chart-card h3 {
  margin: 0 0 1rem 0;
  color: #94a3b8;
}

.chart {
  width: 100%;
  height: 300px;
}

.stability-card {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  grid-column: span 2;
}

.stability-card h3 {
  margin: 0 0 1.5rem 0;
  color: #94a3b8;
}

.stability-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.project-name {
  width: 150px;
  color: #fff;
  font-size: 0.9rem;
}

.stability-bar {
  flex: 1;
  height: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  overflow: hidden;
}

.stability-fill {
  height: 100%;
  transition: width 0.5s ease;
  border-radius: 10px;
}

.stability-fill.excellent {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.stability-fill.good {
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
}

.stability-fill.warning {
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
}

.stability-value {
  width: 50px;
  text-align: right;
  color: #94a3b8;
  font-size: 0.9rem;
}
</style>






