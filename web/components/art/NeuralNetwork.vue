<template>
  <div class="neural-network" ref="containerRef">
    <canvas ref="canvasRef"></canvas>
    <div class="network-info">
      <span class="node-count">{{ nodes.length }} 节点</span>
      <span class="edge-count">{{ edges.length }} 连接</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'

const props = defineProps({
  nodeCount: { type: Number, default: 80 },
  connectionDistance: { type: Number, default: 30 },
  nodeColor: { type: String, default: '#667eea' },
  lineColor: { type: String, default: '#764ba2' },
  backgroundColor: { type: String, default: '#0a0e27' },
  interactive: { type: Boolean, default: true },
  autoRotate: { type: Boolean, default: true },
  rotationSpeed: { type: Number, default: 0.001 },
})

const emit = defineEmits(['nodeClick', 'nodeHover'])

const containerRef = ref(null)
const canvasRef = ref(null)
const nodes = ref([])
const edges = ref([])

let scene, camera, renderer, controls
let animationId = null
let nodeMeshes = []
let lineGeometry, lineMesh
let particleSystem

class NetworkNode {
  constructor(bounds) {
    this.position = new THREE.Vector3(
      (Math.random() - 0.5) * bounds,
      (Math.random() - 0.5) * bounds,
      (Math.random() - 0.5) * bounds
    )
    this.velocity = new THREE.Vector3(
      (Math.random() - 0.5) * 0.02,
      (Math.random() - 0.5) * 0.02,
      (Math.random() - 0.5) * 0.02
    )
    this.acceleration = new THREE.Vector3()
    this.id = Math.random().toString(36).substr(2, 9)
    this.data = { label: `Node ${this.id}`, type: 'concept' }
  }

  update() {
    this.velocity.add(this.acceleration)
    this.velocity.clampLength(0, 0.05)
    this.position.add(this.velocity)
    this.acceleration.set(0, 0, 0)
  }

  applyForce(force) {
    this.acceleration.add(force)
  }
}

const initScene = () => {
  if (!containerRef.value) return

  const bounds = 50

  scene = new THREE.Scene()
  scene.background = new THREE.Color(props.backgroundColor)
  scene.fog = new THREE.FogExp2(props.backgroundColor, 0.015)

  camera = new THREE.PerspectiveCamera(
    60,
    containerRef.value.clientWidth / containerRef.value.clientHeight,
    0.1,
    1000
  )
  camera.position.z = 80

  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    antialias: true,
    alpha: true,
  })
  renderer.setSize(containerRef.value.clientWidth, containerRef.value.clientHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))

  if (props.interactive) {
    controls = new OrbitControls(camera, renderer.domElement)
    controls.enableDamping = true
    controls.dampingFactor = 0.05
    controls.enableZoom = true
    controls.autoRotate = props.autoRotate
    controls.autoRotateSpeed = 0.5
  }

  const ambientLight = new THREE.AmbientLight(0xffffff, 0.4)
  scene.add(ambientLight)

  const pointLight = new THREE.PointLight(props.nodeColor, 1, 100)
  pointLight.position.set(20, 20, 20)
  scene.add(pointLight)

  nodes.value = []
  for (let i = 0; i < props.nodeCount; i++) {
    nodes.value.push(new NetworkNode(bounds))
  }

  const geometry = new THREE.BufferGeometry()
  const positions = new Float32Array(props.nodeCount * 3)
  const colors = new Float32Array(props.nodeCount * 3)

  const nodeColor = new THREE.Color(props.nodeColor)
  const lineColor = new THREE.Color(props.lineColor)

  nodes.value.forEach((node, i) => {
    positions[i * 3] = node.position.x
    positions[i * 3 + 1] = node.position.y
    positions[i * 3 + 2] = node.position.z

    const color = Math.random() > 0.5 ? nodeColor : lineColor
    colors[i * 3] = color.r
    colors[i * 3 + 1] = color.g
    colors[i * 3 + 2] = color.b
  })

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))

  const material = new THREE.PointsMaterial({
    size: 1.5,
    vertexColors: true,
    transparent: true,
    opacity: 0.8,
    blending: THREE.AdditiveBlending,
  })

  particleSystem = new THREE.Points(geometry, material)
  scene.add(particleSystem)

  updateEdges()
}

const updateEdges = () => {
  const edgePositions = []
  edges.value = []

  for (let i = 0; i < nodes.value.length; i++) {
    for (let j = i + 1; j < nodes.value.length; j++) {
      const distance = nodes.value[i].position.distanceTo(nodes.value[j].position)

      if (distance < props.connectionDistance) {
        edges.value.push({
          source: nodes.value[i].id,
          target: nodes.value[j].id,
          distance,
        })

        edgePositions.push(
          nodes.value[i].position.x,
          nodes.value[i].position.y,
          nodes.value[i].position.z,
          nodes.value[j].position.x,
          nodes.value[j].position.y,
          nodes.value[j].position.z
        )
      }
    }
  }

  if (lineMesh) {
    scene.remove(lineMesh)
    lineGeometry.dispose()
  }

  if (edgePositions.length > 0) {
    lineGeometry = new THREE.BufferGeometry()
    lineGeometry.setAttribute(
      'position',
      new THREE.Float32BufferAttribute(edgePositions, 3)
    )

    const lineMaterial = new THREE.LineBasicMaterial({
      color: props.lineColor,
      transparent: true,
      opacity: 0.15,
      blending: THREE.AdditiveBlending,
    })

    lineMesh = new THREE.LineSegments(lineGeometry, lineMaterial)
    scene.add(lineMesh)
  }
}

const animate = () => {
  animationId = requestAnimationFrame(animate)

  nodes.value.forEach((node) => {
    const bounds = 50
    const force = new THREE.Vector3()
    
    if (Math.abs(node.position.x) > bounds / 2) force.x = -node.velocity.x * 2
    if (Math.abs(node.position.y) > bounds / 2) force.y = -node.velocity.y * 2
    if (Math.abs(node.position.z) > bounds / 2) force.z = -node.velocity.z * 2

    const centerForce = node.position.clone().multiplyScalar(-0.0001)
    force.add(centerForce)

    node.applyForce(force)
    node.update()
  })

  const positions = particleSystem.geometry.attributes.position.array
  nodes.value.forEach((node, i) => {
    positions[i * 3] = node.position.x
    positions[i * 3 + 1] = node.position.y
    positions[i * 3 + 2] = node.position.z
  })
  particleSystem.geometry.attributes.position.needsUpdate = true

  if (lineMesh) {
    const linePositions = lineMesh.geometry.attributes.position.array
    let idx = 0
    edges.value.forEach((edge) => {
      const sourceNode = nodes.value.find((n) => n.id === edge.source)
      const targetNode = nodes.value.find((n) => n.id === edge.target)
      if (sourceNode && targetNode) {
        linePositions[idx++] = sourceNode.position.x
        linePositions[idx++] = sourceNode.position.y
        linePositions[idx++] = sourceNode.position.z
        linePositions[idx++] = targetNode.position.x
        linePositions[idx++] = targetNode.position.y
        linePositions[idx++] = targetNode.position.z
      }
    })
    lineMesh.geometry.attributes.position.needsUpdate = true
  }

  if (props.autoRotate && !props.interactive) {
    scene.rotation.y += props.rotationSpeed
  }

  if (controls) controls.update()
  renderer.render(scene, camera)
}

const handleResize = () => {
  if (!containerRef.value || !camera || !renderer) return
  
  camera.aspect = containerRef.value.clientWidth / containerRef.value.clientHeight
  camera.updateProjectionMatrix()
  renderer.setSize(containerRef.value.clientWidth, containerRef.value.clientHeight)
}

onMounted(() => {
  initScene()
  animate()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  window.removeEventListener('resize', handleResize)
  
  if (renderer) renderer.dispose()
  if (controls) controls.dispose()
})
</script>

<style scoped>
.neural-network {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 500px;
}

.neural-network canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.network-info {
  position: absolute;
  bottom: 1rem;
  left: 1rem;
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  font-family: 'JetBrains Mono', monospace;
}

.node-count,
.edge-count {
  padding: 0.25rem 0.75rem;
  background: rgba(0, 0, 0, 0.3);
  border-radius: var(--radius-full);
  backdrop-filter: blur(10px);
}
</style>
