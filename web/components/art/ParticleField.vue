<template>
  <div class="particle-field" ref="containerRef">
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useWindowSize } from '@vueuse/core'

const props = defineProps({
  particleCount: { type: Number, default: 150 },
  connectionDistance: { type: Number, default: 120 },
  mouseRadius: { type: Number, default: 150 },
  speed: { type: Number, default: 0.5 },
  color: { type: String, default: '#667eea' },
  secondaryColor: { type: String, default: '#764ba2' },
  showMouseTrail: { type: Boolean, default: true },
})

const containerRef = ref(null)
const canvasRef = ref(null)
const { width, height } = useWindowSize()

let ctx = null
let animationId = null
let particles = []
let mouse = { x: null, y: null, active: false }

class Particle {
  constructor(canvasWidth, canvasHeight) {
    this.x = Math.random() * canvasWidth
    this.y = Math.random() * canvasHeight
    this.vx = (Math.random() - 0.5) * props.speed
    this.vy = (Math.random() - 0.5) * props.speed
    this.size = Math.random() * 2 + 1
    this.color = Math.random() > 0.5 ? props.color : props.secondaryColor
    this.baseX = this.x
    this.baseY = this.y
  }

  update(canvasWidth, canvasHeight) {
    this.x += this.vx
    this.y += this.vy

    if (this.x < 0 || this.x > canvasWidth) this.vx *= -1
    if (this.y < 0 || this.y > canvasHeight) this.vy *= -1

    if (mouse.active) {
      const dx = mouse.x - this.x
      const dy = mouse.y - this.y
      const distance = Math.sqrt(dx * dx + dy * dy)
      
      if (distance < props.mouseRadius) {
        const force = (props.mouseRadius - distance) / props.mouseRadius
        const angle = Math.atan2(dy, dx)
        this.vx -= Math.cos(angle) * force * 0.02
        this.vy -= Math.sin(angle) * force * 0.02
      }
    }
  }

  draw(ctx) {
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fillStyle = this.color
    ctx.fill()
  }
}

const initParticles = () => {
  if (!canvasRef.value) return
  
  const canvas = canvasRef.value
  canvas.width = width.value
  canvas.height = height.value
  
  particles = []
  for (let i = 0; i < props.particleCount; i++) {
    particles.push(new Particle(canvas.width, canvas.height))
  }
}

const drawConnections = () => {
  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < props.connectionDistance) {
        const opacity = (props.connectionDistance - distance) / props.connectionDistance
        ctx.beginPath()
        ctx.strokeStyle = `rgba(102, 126, 234, ${opacity * 0.4})`
        ctx.lineWidth = 1
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.stroke()
      }
    }
  }
}

const animate = () => {
  if (!canvasRef.value || !ctx) return
  
  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)
  
  particles.forEach(particle => {
    particle.update(canvasRef.value.width, canvasRef.value.height)
    particle.draw(ctx)
  })
  
  drawConnections()
  animationId = requestAnimationFrame(animate)
}

const handleMouseMove = (e) => {
  mouse.x = e.clientX
  mouse.y = e.clientY
  mouse.active = true
}

const handleMouseLeave = () => {
  mouse.active = false
}

const handleResize = () => {
  if (!canvasRef.value) return
  canvasRef.value.width = width.value
  canvasRef.value.height = height.value
  initParticles()
}

onMounted(() => {
  if (canvasRef.value) {
    ctx = canvasRef.value.getContext('2d')
    initParticles()
    animate()
    
    window.addEventListener('mousemove', handleMouseMove)
    window.addEventListener('mouseleave', handleMouseLeave)
    window.addEventListener('resize', handleResize)
  }
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseleave', handleMouseLeave)
  window.removeEventListener('resize', handleResize)
})

watch([width, height], () => {
  handleResize()
})
</script>

<style scoped>
.particle-field {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.particle-field canvas {
  width: 100%;
  height: 100%;
}
</style>
