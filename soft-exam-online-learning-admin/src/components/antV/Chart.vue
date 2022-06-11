<template>
  <div id="chart"></div>
</template>
<script>
import { Chart } from "@antv/g2";
let chart = null;
export default {
  data() {
    return {
      chartData: {},
      newData: [],
    };
  },
  props: {
    antVData: {
      type: Object,
      default: () => {},
    },
  },
  methods: {
    initComponent() {
      chart = new Chart({
        container: "chart",
        autoFit: true,
        height: "25rem",
        width: "18.75rem",
      });
      // console.log(this.newData);
      chart.data(this.newData);
      chart.scale("count", {
        nice: true,
        alias: "登入数",
      });
      chart.tooltip({
        showMarkers: false,
        shared: true,
      });
      chart
        .interval()
        .position("days*count")
        .color("type")
        .adjust([
          {
            type: "dodge",
            marginRatio: 0,
          },
        ]);
      chart.interaction("element-highlight-by-x");
      chart.render();
    },
    //接收父组件的数据
    getChartData(data) {
      this.chartData = data;
      // console.log(this.chartData);
      this.chartData.all.forEach((el) => {
        let obj = {};
        obj["type"] = "全部";
        obj["count"] = el.count;
        obj["days"] = el.days;
        this.newData.push(obj);
      });
      this.chartData.me.forEach((el) => {
        let obj = {};
        obj["type"] = "我";
        obj["count"] = el.count;
        obj["days"] = el.days;
        this.newData.push(obj);
      });
      // console.log(this.testData);
    },
    reloadDrawing() {
      //销毁画布对象
      chart.destroy();
      //重新调用数据进行加载
      this.initComponent();
    },
  },
  created() {
    this.chartData = this.antvData;
  },
  mounted() {
    setTimeout(() => {
      this.initComponent();
    });
  },
  /**
   * 监听数据
   * newData数据更新变化时执行函数
   */
  watch: {
    newData: "reloadDrawing",
  },
};
</script>
<style lang="scss" scoped>
#chart {
  width: 400px;
  height: 350px;
}
</style>
