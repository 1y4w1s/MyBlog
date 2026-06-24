<template>
  <div v-if="isLink" :style="{ height: height, width: width }">
    <iframe :src="link" frameborder="no" style="width: 100%; height: 100%" />
  </div>
</template>

<script>
export default {
  name: 'InnerLink',
  props: {
    link: {
      type: String,
      default: ''
    }
  },
  computed: {
    isLink() {
      return this.link && this.link !== ''
    },
    height() {
      return (document.documentElement.clientHeight - 84) + 'px'
    },
    width() {
      return (document.documentElement.clientWidth - 180) + 'px'
    }
  }
}
</script>
