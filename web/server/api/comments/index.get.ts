export default defineEventHandler(async (event) => {
  const method = event.method
  const query = getQuery(event)
  
  if (method === 'GET') {
    const page = parseInt(query.page as string) || 0
    const size = parseInt(query.size as string) || 20
    const sortBy = query.sortBy || 'newest'
    
    const comments = [
      {
        id: 1,
        name: '前端开发者',
        email: 'dev@example.com',
        message: '太棒了！这个知识库系统设计得非常精美，尤其是动态粒子效果。期待更多内容！',
        likes: 12,
        liked: false,
        createdAt: '2025-01-20T10:30:00',
        replies: [
          { id: 101, name: '站长', message: '感谢支持！会持续更新更多内容~' }
        ]
      },
      {
        id: 2,
        name: '设计师小王',
        message: '艺术展示部分真的很惊艳，尤其是3D节点网络，让人印象深刻！',
        likes: 8,
        liked: true,
        createdAt: '2025-01-18T15:20:00',
        replies: []
      },
      {
        id: 3,
        name: '技术爱好者',
        message: '想请教一下这个粒子效果是用什么技术实现的？Three.js吗？',
        likes: 5,
        liked: false,
        createdAt: '2025-01-15T09:45:00',
        replies: [
          { id: 102, name: '站长', message: '是的，用的是Three.js配合自定义着色器实现的效果' }
        ]
      }
    ]
    
    let sortedComments = [...comments]
    if (sortBy === 'newest') {
      sortedComments.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    } else {
      sortedComments.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
    }
    
    return {
      total: sortedComments.length,
      page,
      size,
      comments: sortedComments.slice(page * size, (page + 1) * size)
    }
  }
  
  if (method === 'POST') {
    const body = await readBody(event)
    
    const newComment = {
      id: Date.now(),
      name: body.name,
      email: body.email,
      website: body.website,
      message: body.message,
      likes: 0,
      liked: false,
      createdAt: new Date().toISOString(),
      replies: []
    }
    
    return {
      success: true,
      comment: newComment
    }
  }
})
