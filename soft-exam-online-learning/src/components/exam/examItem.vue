<template>
  <div id="exam-item">
    <h4>{{ paperItem.name }}</h4>
    <p class="name">上午题</p>
    <div class="info">
      <span>{{ paperItem.time }}</span>
      <i class="iconfont icon-icon-time"></i>
      <span>限时{{ paperItem.suggestTime }}分钟</span>
      <i class="iconfont icon-fenshu"></i>
      <span>{{ '满分' + paperItem.score + '分' }}</span>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {}
  },
  props: ['paperItem'],
  methods: {},
}
</script>
<style scoped lang="scss">
#exam-item {
  .info i {
    margin-right: 5px;
    color: #0195ff;
  }
  .info span {
    margin-right: 12px;
  }
  .info {
    font-size: 14px;
    color: #88949b;
  }
  .name {
    font-size: 14px;
    color: #88949b;
  }
  * {
    margin: 20px 0;
  }
}
</style>
