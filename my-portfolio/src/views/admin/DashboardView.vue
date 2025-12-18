<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h1>管理后台</h1>
      <div class="header-actions">
        <button @click="refreshData" class="btn-refresh">刷新数据</button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">👁️</div>
        <div class="stat-content">
          <h3>今日访问</h3>
          <p class="stat-value">{{ dashboardData.visits?.today || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <h3>总访问量</h3>
          <p class="stat-value">{{ dashboardData.visits?.total || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💬</div>
        <div class="stat-content">
          <h3>消息总数</h3>
          <p class="stat-value">{{ dashboardData.messages?.total || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🌐</div>
        <div class="stat-content">
          <h3>独立IP</h3>
          <p class="stat-value">{{ dashboardData.ips?.totalIps || 0 }}</p>
        </div>
      </div>
    </div>

    <!-- 访问趋势图 -->
    <div class="chart-section">
      <h2>访问趋势（最近7天）</h2>
      <div ref="visitChart" class="chart-container"></div>
    </div>

    <!-- 数据表格 -->
    <div class="tables-section">
      <div class="table-card">
        <h2>热门页面</h2>
        <table>
          <thead>
            <tr>
              <th>页面路径</th>
              <th>访问次数</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="path in topPaths" :key="path.path">
              <td>{{ path.path }}</td>
              <td>{{ path.count }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-card">
        <h2>访问IP排行</h2>
        <table>
          <thead>
            <tr>
              <th>IP地址</th>
              <th>访问次数</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ip in topIps" :key="ip.ip">
              <td>{{ ip.ip }}</td>
              <td>{{ ip.count }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { statsApi } from '../../utils/api'
import * as echarts from 'echarts'

const dashboardData = ref({})
const topPaths = ref([])
const topIps = ref([])
const visitChart = ref(null)
let chartInstance = null

const loadDashboardData = async () => {
  try {
    const data = await statsApi.getDashboardData()
    dashboardData.value = data
    topPaths.value = data.paths?.topPaths || []
    topIps.value = data.ips?.topIps || []
    
    if (data.visits?.trends) {
      updateVisitChart(data.visits.trends)
    }
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
  }
}

const updateVisitChart = (trends) => {
  if (!visitChart.value) return
  
  if (!chartInstance) {
    chartInstance = echarts.init(visitChart.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: trends.map(t => t.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: trends.map(t => t.count),
      type: 'line',
      smooth: true,
      areaStyle: {},
      itemStyle: {
        color: '#6366f1'
      }
    }]
  }
  
  chartInstance.setOption(option)
}

const refreshData = () => {
  loadDashboardData()
}

let refreshInterval = null

onMounted(() => {
  loadDashboardData()
  // 每30秒自动刷新
  refreshInterval = setInterval(loadDashboardData, 30000)
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped>
.admin-dashboard {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  font-size: 2rem;
  color: var(--text-primary);
}

.btn-refresh {
  padding: 0.5rem 1rem;
  background: var(--accent-primary);
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: var(--bg-secondary);
  padding: 1.5rem;
  border-radius: 1rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: var(--shadow-sm);
}

.stat-icon {
  font-size: 2.5rem;
}

.stat-content h3 {
  margin: 0 0 0.5rem 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.stat-value {
  margin: 0;
  font-size: 2rem;
  font-weight: bold;
  color: var(--text-primary);
}

.chart-section {
  background: var(--bg-secondary);
  padding: 1.5rem;
  border-radius: 1rem;
  margin-bottom: 2rem;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.tables-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1.5rem;
}

.table-card {
  background: var(--bg-secondary);
  padding: 1.5rem;
  border-radius: 1rem;
}

.table-card h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

th {
  color: var(--text-secondary);
  font-weight: 600;
}
</style>






