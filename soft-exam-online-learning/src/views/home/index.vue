<template>
  <!-- 首页内容区域 -->
  <div id="home">
    <div class="over-scroll">
      <el-card>
        <span class="first">练习中心</span>
        <div class="body">
          <div class="no-content">
            <img src="@/assets/images/no-content.png" alt />
            <div>暂无内容</div>
          </div>
        </div>
      </el-card>
      <el-card>
        <span class="two">试卷中心</span>
        <div class="body">
          <div class="no-content">
            <img src="@/assets/images/no-content.png" alt />
            <div>暂无内容</div>
          </div>
        </div>
      </el-card>
      <el-card>
        <span class="three">官方指定书籍</span>
        <div class="body">
          <div class="no-content">
            <img src="@/assets/images/no-content.png" alt />
            <div>暂无内容</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {}
  },
  components: {},
  methods: {},
}
</script>
<style lang="scss" scoped>
#home {
  width: 100%;
  height: 90vh;
  .over-scroll {
    height: 80vh;
    overflow-y: scroll;
    overflow-x: hidden;
  }
  .body {
    position: relative;
    .no-content {
      margin-top: 80px;
      display: flex;
      justify-content: center;
      text-align: center;
      div {
        font-size: 14px;
        color: #9b9b9b;
        position: absolute;
        bottom: -30%;
        left: 50%;
        transform: translateX(-50%);
      }
    }
  }
}
::v-deep .el-card {
  height: 300px;
  margin: 50px;
  position: relative;
  span {
    font-size: 17px;
    font-weight: 400;
    color: #1f2f3d;
    padding: 10px 25px;
    color: #fff;
    border-radius: 5px;
    position: absolute;
    left: -35px;
    top: 10px;
  }
}
.first {
  background-color: #6cadc8;
}
.two {
  background-color: #d9a287;
}
.three {
  background-color: #8cbda4;
}
.el-card {
  overflow: visible;
  cursor: default;
}
</style>
