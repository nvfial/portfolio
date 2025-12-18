// 访问者追踪工具
export const trackVisit = async () => {
  try {
    // 获取访问者信息
    const visitorInfo = {
      ip: await getClientIP(),
      userAgent: navigator.userAgent,
      referrer: document.referrer,
      language: navigator.language,
      screenResolution: `${window.screen.width}x${window.screen.height}`,
      timestamp: new Date().toISOString(),
      path: window.location.pathname
    }

    // 发送到后端（静默发送，不阻塞页面）
    fetch('http://localhost:8080/api/admin/visits/track', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(visitorInfo),
      keepalive: true // 确保页面关闭时也能发送
    }).catch(err => {
      console.log('Visit tracking failed:', err)
    })
  } catch (error) {
    console.log('Visit tracking error:', error)
  }
}

// 获取客户端IP（通过第三方服务）
const getClientIP = async () => {
  try {
    const response = await fetch('https://api.ipify.org?format=json')
    const data = await response.json()
    return data.ip
  } catch (error) {
    // 如果失败，返回本地IP标识
    return 'unknown'
  }
}

// 页面加载时自动追踪
if (typeof window !== 'undefined') {
  trackVisit()
  
  // 页面可见性变化时也追踪
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
      trackVisit()
    }
  })
}






