<template>
  <div id="exam-item">
    <ul class="paper">
      <li
        class="item"
        v-for="item in paperList"
        :key="item.id"
        @mouseover="showDetail = item.id"
        @mouseleave="showDetail = undefined"
      >
        <exam-item :paperItem="item"></exam-item>
        <div class="dialog-main" v-show="showDetail === item.id">
          <span class="detail-button" @click="toPaper(item)">查看详情</span>
        </div>
      </li>
    </ul>
  </div>
</template>
<script>
import ExamItem from "@/components/exam/examItem";
export default {
  data() {
    return {
      showDetail: undefined,
    };
  },
  props: ["paperList"],
  components: {
    ExamItem,
  },
  methods: {
    // 跳转到试卷页面
    toPaper(val) {
      this.$emit("showPaperInfo", val);
    },
  },
};
</script>
<style scoped lang="scss">
.paper {
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  margin: 0 50px;
  position: relative;
}
.paper::after {
  content: "";
  flex: auto;
}
.paper .item {
  width: 185px;
  height: 180px;
  padding: 15px;
  margin: 0 0 0 60px;
  position: relative;
  border: 1px solid #ebebeb;
  box-shadow: 0 0 4px 2px rgba(217, 222, 234, 0.3);
  transition: all 0.6s ease;
  border-radius: 3px;
  background: #fff;
}
.paper .item::after {
  // height: 0;
  // width: 20%;
  // min-width: 200px;
  // content: '';
}
.paper .item:hover {
  border: 1px solid #ccc;
  transform: scale(1.03);
}
.paper * {
  margin: 20px 0;
}
.dialog-main {
  height: 60%;
  width: 100%;
  position: absolute;
  bottom: 0px;
  left: 0px;
  background-color: #fff;
  .detail-button {
    display: inline-block;
    width: 150px;
    height: 30px;
    line-height: 30px;
    background: #25bb9b;
    color: #fff;
    cursor: pointer;
  }
}
</style>
