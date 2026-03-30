export default defineEventHandler(async (event) => {
  const method = event.method
  
  if (method === 'GET') {
    return {
      collections: [
        {
          id: 1,
          name: '数字艺术',
          slug: 'digital-art',
          description: '探索数字世界的艺术表达',
          category: 'digital',
          artworkCount: 2
        },
        {
          id: 2,
          name: '3D渲染',
          slug: '3d-rendering',
          description: '三维空间的视觉盛宴',
          category: '3d',
          artworkCount: 1
        },
        {
          id: 3,
          name: '摄影作品',
          slug: 'photography',
          description: '瞬间的永恒记录',
          category: 'photo',
          artworkCount: 1
        }
      ]
    }
  }
})
