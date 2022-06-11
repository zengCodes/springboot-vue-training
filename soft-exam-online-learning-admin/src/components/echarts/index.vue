<template>
  <Echart :options="options" :height="'40vh'" :width="'100%'"></Echart>
</template>

<script>
import Echart from "@/components/echarts/common";
function graphicFactory(side) {
  return {
    type: "text",
    bottom: "8",
    ...side,
    style: {
      text: "",
      textAlign: "center",
      fill: "#4E5969",
      fontSize: 12,
    },
  };
}
export default {
  data() {
    return {
      options: {},
      graphicElements: [
        graphicFactory({ left: "2.6%" }),
        graphicFactory({ right: 0 }),
      ],
    };
  },
  components: {
    Echart,
  },
  props: {
    lineData: {
      type: Object,
      default: () => ({}),
    },
  },
  methods: {},
  watch: {
    lineData: {
      handler(lineData) {
        console.log(lineData);
        this.options = {
          grid: {
            left: "2.6%",
            right: "0",
            top: "10",
            bottom: "30",
          },
          xAxis: {
            type: "category",
            offset: 2,
            data: lineData?.days,
            boundaryGap: false,
            axisLabel: {
              color: "#4E5969",
              formatter(value, idx) {
                console.log(value, idx);
                if (idx === 0) return "";
                if (idx === value.length - 1) return "";
                return `${value}`;
              },
            },
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            splitLine: {
              show: true,
              interval: (value, idx) => {
                console.log(value, idx);
                if (idx === 0) return false;
                if (idx === value.length - 1) return false;
                return true;
              },
              lineStyle: {
                color: "#E5E8EF",
              },
            },
            axisPointer: {
              show: true,
              lineStyle: {
                color: "#23ADFF",
                width: 2,
              },
            },
          },
          yAxis: {
            type: "value",
            axisLine: {
              show: false,
            },
            axisLabel: {
              formatter(value, idx) {
                if (idx === 0) return value;
                return `${value}次`;
              },
            },
            splitLine: {
              show: true,
              lineStyle: {
                type: "dashed",
                color: "#E5E8EF",
              },
            },
          },
          tooltip: {
            trigger: "axis",
            formatter(params) {
              let dataStr = `<div><p class="tooltip-title">${params[0].name}</p></div>`;
              params.forEach((item, index) => {
                dataStr += `<div class="content-panel" style="margin: 0 8px;">
            <span style="color:${params[index].color};">登录统计</span><span class="tooltip-value">${params[index].seriesName}</span>
            <span style="float:right;color:#000000;margin-left:10px;font-size:12px;">${params[index].value}起</span>
            </div>`;
              });
              dataStr += `<div></div>`;
              return dataStr;
            },
            className: "echarts-tooltip-diy",
          },
          graphic: {
            elements: this.graphicElements,
          },
          series: [
            {
              data: lineData?.count?.all,
              name: "所有",
              type: "line",
              smooth: true,
              symbolSize: 12,
              emphasis: {
                focus: "series",
                itemStyle: {
                  borderWidth: 2,
                },
              },
              lineStyle: {
                width: 3,
                color: new this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  {
                    offset: 0,
                    color: "rgba(30, 231, 255, 1)",
                  },
                  {
                    offset: 0.5,
                    color: "rgba(36, 154, 255, 1)",
                  },
                  {
                    offset: 1,
                    color: "rgba(111, 66, 251, 1)",
                  },
                ]),
              },
              showSymbol: false,
              areaStyle: {
                opacity: 0.8,
                color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: "rgba(17, 126, 255, 0.2)",
                  },
                  {
                    offset: 1,
                    color: "rgba(17, 128, 255, 0)",
                  },
                ]),
              },
            },
            {
              data: lineData?.count?.me,
              type: "line",
              name: "自己",
              smooth: true,
              symbolSize: 12,
              emphasis: {
                focus: "series",
                itemStyle: {
                  borderWidth: 2,
                },
              },
              lineStyle: {
                width: 3,
                color: new this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  {
                    offset: 0,
                    color: "rgba(98, 232, 132, 1)",
                  },
                  {
                    offset: 0.5,
                    color: "rgba(98, 232, 132, 1)",
                  },
                  {
                    offset: 1,
                    color: "rgba(98, 232, 132, 1)",
                  },
                ]),
              },
              showSymbol: false,
              areaStyle: {
                opacity: 0.8,
                color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: "rgba(98, 232, 132, 0.2)",
                  },
                  {
                    offset: 1,
                    color: "rgba(98, 232, 132, 0)",
                  },
                ]),
              },
            },
          ],
        };
      },
      // 深度监听
      deep: true,
      immediate: true,
    },
  },
};
</script>
<style lang="scss" scoped>
* {
  margin: 0;
  padding: 0;
  list-style: none;
}
::v-deep .echarts-tooltip-diy {
  background: linear-gradient(
    304.17deg,
    rgba(253, 254, 255, 0.6) -6.04%,
    rgba(244, 247, 252, 0.6) 85.2%
  ) !important;
  border: none !important;
  backdrop-filter: blur(10px) !important;
  /* Note: backdrop-filter has minimal browser support */

  border-radius: 6px !important;
  .content-panel {
    display: flex;
    justify-content: space-between;
    padding: 0 9px;
    background: rgba(255, 255, 255, 0.8);
    width: 164px;
    height: 32px;
    line-height: 32px;
    box-shadow: 6px 0px 20px rgba(34, 87, 188, 0.1);
    border-radius: 4px;
    margin-bottom: 4px;
  }
  .tooltip-title {
    margin: 0 0 10px 0;
  }
  p {
    margin: 0;
  }
  .tooltip-title,
  .tooltip-value {
    font-size: 13px;
    line-height: 15px;
    display: flex;
    align-items: center;
    text-align: right;
    color: #1d2129;
    font-weight: bold;
  }
  .tooltip-item-icon {
    display: inline-block;
    margin-right: 8px;
    width: 10px;
    height: 10px;
    border-radius: 50%;
  }
}
</style>
