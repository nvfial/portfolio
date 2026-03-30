export default defineEventHandler(async (event) => {
  const method = event.method
  
  if (method === 'GET') {
    return {
      domains: [
        {
          id: 1,
          name: '前端开发',
          slug: 'frontend',
          icon: '⚛️',
          description: '探索现代前端技术的无限可能',
          color: '#667eea',
          categoryCount: 4,
          articleCount: 42,
          children: [
            { id: 11, name: 'Vue.js', slug: 'vue', icon: '💚', articleCount: 15 },
            { id: 12, name: 'React', slug: 'react', icon: '⚛️', articleCount: 12 },
            { id: 13, name: 'TypeScript', slug: 'typescript', icon: '🔷', articleCount: 8 },
            { id: 14, name: 'CSS动画', slug: 'css-animation', icon: '🎨', articleCount: 7 }
          ]
        },
        {
          id: 2,
          name: '后端架构',
          slug: 'backend',
          icon: '🚀',
          description: '构建高性能的后端系统',
          color: '#764ba2',
          categoryCount: 3,
          articleCount: 35,
          children: [
            { id: 21, name: 'Spring Boot', slug: 'spring-boot', icon: '🍃', articleCount: 10 },
            { id: 22, name: 'Node.js', slug: 'nodejs', icon: '🟢', articleCount: 9 },
            { id: 23, name: '微服务', slug: 'microservice', icon: '🏗️', articleCount: 6 }
          ]
        },
        {
          id: 3,
          name: '人工智能',
          slug: 'ai',
          icon: '🤖',
          description: '探索AI的边界与可能',
          color: '#f093fb',
          categoryCount: 2,
          articleCount: 28,
          children: [
            { id: 31, name: '机器学习', slug: 'machine-learning', icon: '📊', articleCount: 8 },
            { id: 32, name: 'LLM应用', slug: 'llm', icon: '💬', articleCount: 5 }
          ]
        },
        {
          id: 4,
          name: '设计艺术',
          slug: 'design',
          icon: '🎭',
          description: '视觉设计与创意表达',
          color: '#4facfe',
          categoryCount: 2,
          articleCount: 20,
          children: [
            { id: 41, name: 'UI设计', slug: 'ui-design', icon: '🎯', articleCount: 6 },
            { id: 42, name: '3D艺术', slug: '3d-art', icon: '🎲', articleCount: 4 }
          ]
        }
      ]
    }
  }
})
