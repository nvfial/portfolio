<template>
  <div
    v-show="showParticles"
    :id="`tsparticles-${id}`"
    class="particles-background"
    ref="particlesContainerRef"
  ></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useParticles } from '../composables/useParticles'

const props = defineProps({
  id: {
    type: String,
    default: () => `particles-${Date.now()}`
  }
})

const particlesContainerRef = ref(null)
const { showParticles } = useParticles(particlesContainerRef)
</script>

<style scoped>
.particles-background {
  position: fixed;
  inset: 0;
  z-index: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  opacity: 1;
  overflow: hidden;
}

.particles-background canvas {
  width: 100% !important;
  height: 100% !important;
  display: block;
  position: absolute;
  top: 0;
  left: 0;
}
</style>

