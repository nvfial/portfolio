<template>
  <div :class="{ dark: isDark }">
    <NuxtPage />
  </div>
</template>

<script setup>
import { useDark, useToggle } from '@vueuse/core'

const isDark = useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: '',
  storageKey: 'portfolio-theme',
})
const toggleDark = useToggle(isDark)

provide('isDark', isDark)
provide('toggleDark', toggleDark)
</script>

<style>
div {
  min-height: 100vh;
}
</style>
