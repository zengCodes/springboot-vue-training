<template>
  <div id="nodeTree"></div>
</template>
<script>
import * as G6 from "@antv/g6";
let graph = null;
export default {
  data() {
    return {
      //保存处理后的数据
      newData: [],
    };
  },
  methods: {
    initComponent() {
      let minWidth = 100;
      let BaseConfig = {
        nameFontSize: 20,
        childCountWidth: 22,
        countMarginLeft: 0,
        itemPadding: 15,
        nameMarginLeft: 4,
        rootPadding: 18,
      };
      /**
       * 自定义节点
       * 节点名称treeNode
       */
      G6.registerNode("treeNode", {
        draw: (cfg, group) => {
          const { id, label, collapsed, selected, children, depth } = cfg;
          const rootNode = depth === 0;
          const hasChildren = children && children.length !== 0;
          const {
            childCountWidth,
            countMarginLeft,
            itemPadding,
            nameMarginLeft,
            rootPadding,
          } = BaseConfig;
          let width = 0;
          const height = 30;
          const x = 0;
          const y = -height / 2;
          // 名称文本
          const text = group.addShape("text", {
            attrs: {
              text: label,
              x: x * 2,
              y,
              textAlign: "left",
              textBaseline: "top",
              fontFamily: "PingFangSC-Regular",
            },
            cursor: "pointer",
            name: "name-text-shape",
          });
          const textWidth = text.getBBox().width;
          width = textWidth + itemPadding + nameMarginLeft;
          width = width < minWidth ? minWidth : width;
          if (!rootNode && hasChildren) {
            width += countMarginLeft;
            width += childCountWidth;
          }
          const keyShapeAttrs = {
            x,
            y,
            width,
            height,
            radius: 4,
          };
          // keyShape根节点选中样式
          if (rootNode && selected) {
            keyShapeAttrs.fill = "#e8f7ff";
            keyShapeAttrs.stroke = "#e8f7ff";
          }
          const keyShape = group.addShape("rect", {
            attrs: keyShapeAttrs,
            name: "root-key-shape-rect-shape",
          });
          if (!rootNode) {
            // 底部横线 线条Path
            group.addShape("path", {
              attrs: {
                path: [
                  ["M", x - 1, 0],
                  ["L", width, 0],
                ],
                stroke: "#AAB7C4",
                lineWidth: 1,
              },
              name: "node-path-shape",
            });
          }
          const mainX = x - 10;
          const mainY = -height + 15;
          if (rootNode) {
            // 根节点矩形样式
            group.addShape("rect", {
              attrs: {
                x: mainX,
                y: mainY,
                width: width + 35,
                height,
                // 定义圆角
                radius: 14,
                fill: "#e8f7ff",
                cursor: "pointer",
              },
              name: "main-shape",
            });
          }
          let nameColor = "rgba(0, 0, 0, .65)";
          if (selected) {
            nameColor = "#40A8FF";
          }
          // 名称
          if (rootNode) {
            // 根节点文本样式
            group.addShape("text", {
              attrs: {
                text: label,
                x: mainX + rootPadding,
                y: 1,
                textAlign: "left",
                textBaseline: "middle",
                fill: nameColor,
                fontSize: 15,
                fontFamily: "PingFangSC-Regular",
                cursor: "pointer",
              },
              name: "root-text-shape",
            });
          } else {
            // 子节点文本样式
            group.addShape("text", {
              attrs: {
                text: label,
                x: selected ? mainX + 6 + nameMarginLeft : mainX + 6,
                y: y - 5,
                textAlign: "start",
                textBaseline: "top",
                fill: nameColor,
                fontSize: 14,
                fontFamily: "PingFangSC-Regular",
                cursor: "pointer",
              },
              name: "not-root-text-shape",
            });
          }
          // 子类数量
          if (hasChildren && !rootNode) {
            const childCountHeight = 12;
            const childCountX = width - childCountWidth;
            const childCountY = -childCountHeight / 2;
            //矩形rect样式
            group.addShape("rect", {
              attrs: {
                width: childCountWidth,
                height: 12,
                stroke: collapsed ? "#1890ff" : "#5CDBD3",
                fill: collapsed ? "#fff" : "#E6FFFB",
                x: childCountX,
                y: childCountY,
                radius: 6,
                cursor: "pointer",
              },
              name: "child-count-rect-shape",
            });
          }
          return keyShape;
        },
      });
      // 自定义边
      G6.registerEdge("smooth", {
        draw(cfg, group) {
          const { startPoint, endPoint } = cfg;
          const hgap = Math.abs(endPoint.x - startPoint.x);
          const path = [
            ["M", startPoint.x, startPoint.y],
            [
              "C",
              startPoint.x + hgap / 4,
              startPoint.y,
              endPoint.x - hgap / 2,
              endPoint.y,
              endPoint.x,
              endPoint.y,
            ],
          ];
          const shape = group.addShape("path", {
            attrs: {
              stroke: "#AAB7C4",
              path,
            },
            name: "smooth-path-shape",
          });
          return shape;
        },
      });
      const container = document.getElementById("nodeTree");
      const width = container.scrollWidth;
      const height = container.scrollHeight || 500;
      graph = new G6.TreeGraph({
        container: container,
        width,
        height,
        modes: {
          default: [
            {
              type: "collapse-expand",
            },
            // "drag-canvas", // drag-canvas拖拽画布
            // "zoom-canvas" // zoom-canvas放缩画布
          ],
        },
        defaultNode: {
          type: "treeNode",
          anchorPoints: [
            [0, 0.5],
            [1, 0.5],
          ],
        },
        defaultEdge: {
          type: "smooth",
        },
        layout: {
          type: "compactBox",
          // 布局设置，默认LR节点在右，往右展开
          direction: "LR",
          getId: function getId(d) {
            return d.id;
          },
          getHeight: function getHeight() {
            return 18;
          },
          getWidth: function getWidth(d) {
            // console.log(d)
            const labelWidth = G6.Util.getTextSize(
              d.label,
              BaseConfig.nameFontSize
            )[0];
            const width =
              BaseConfig.itemPadding +
              BaseConfig.nameMarginLeft +
              labelWidth +
              BaseConfig.rootPadding +
              BaseConfig.childCountWidth;
            return width;
          },
          getVGap: function getVGap() {
            return 15;
          },
          getHGap: function getHGap() {
            return 30;
          },
        },
      });
      //自定义事件
      graph.on("drop", (ev) => {
        console.log(ev);
      });
      // console.log(this.newData[0]);
      graph.data(this.newData[0]);
      graph.render();
      graph.fitView();
      if (typeof window !== "undefined")
        window.onresize = () => {
          if (!graph || graph.get("destroyed")) return;
          if (!container || !container.scrollWidth || !container.scrollHeight)
            return;
          graph.changeSize(container.scrollWidth, container.scrollHeight);
        };
    },
    /**
     * 接收父组件的数据,并处理可使用的格式
     * id必须时String类型，否则会报错
     */
    getTreeData(data) {
      data.forEach((item, i) => {
        let data2 = [];
        for (let i in item.children) {
          let data3 = [];
          for (let j in item.children[i].children) {
            let last = item.children[i].children[j].children;
            let data4 = [];
            for (let k in last) {
              let name = item.children[i].children[j].children[k].name;
              data4.push({
                id: String(item.children[i].children[j].children[k].id),
                label: name,
              });
            }
            let name = item.children[i].children[j].name;
            data3.push({
              id: String(item.children[i].children[j].id),
              label: name,
              children: data4,
            });
          }
          // console.log(data3);
          data2.push({
            id: String(item.children[i].id),
            label: item.children[i].name,
            children: data3,
          });
        }
        // console.log(data2);
        this.newData.push({
          id: String(item.id),
          label: item.name,
          children: data2,
        });
      });
      // console.log(this.newData);
    },
    reloadDrawing() {
      // 销毁画布对象
      graph.destroy();
      // 重新调用数据进行加载
      this.initComponent();
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.initComponent();
    });
  },
  /**
   * 监听数据
   * newData数据更新变化时执行函数
   */
  watch: {
    newData: {
      handler(val) {
        this.reloadDrawing();
      },
      deep: true,
    },
  },
};
</script>
<style scoped>
#nodeTree {
  width: 100%;
  height: 595px;
  margin: 20px auto;
}
</style>
