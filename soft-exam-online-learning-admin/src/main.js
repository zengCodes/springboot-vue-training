import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import ZkTable from "vue-table-with-tree-grid";
import { hasPermission } from "./utils/permissionDirect";
// md文档编辑
import mavonEditor from "mavon-editor";
import * as echarts from "echarts";
// 使用断点上传
import uploader from "vue-simple-uploader";
import { getDicts } from "@/api/system/dict";
import { getConfigKey } from "@/api/system/config";
import {
  parseTime,
  selectDictLabel,
  selectDictLabels,
  downloadResource,
} from "@/utils/zeng";
import "./permission";
import "@/assets/styles/user.scss";
import "@/assets/styles/index.scss";
import "mavon-editor/dist/css/index.css";

//树形插件
Vue.use(ZkTable);
Vue.use(ElementUI);
Vue.use(uploader);
Vue.use(mavonEditor);
// 全局方法挂载
Vue.prototype.selectDictLabel = selectDictLabel;
Vue.prototype.selectDictLabels = selectDictLabels;
Vue.prototype.parseTime = parseTime;
Vue.prototype.downloadResource = downloadResource;
Vue.config.productionTip = false;
Vue.prototype.$echarts = echarts;
Vue.prototype.getDicts = getDicts;
Vue.prototype.getConfigKey = getConfigKey;

Vue.prototype.Notification = function (title, message, showClose) {
  this.$notify.success({
    title,
    message,
    showClose,
  });
};

Vue.prototype.msgSuccess = function (msg) {
  this.$message({ showClose: true, message: msg, type: "success" });
};
Vue.prototype.msgError = function (msg) {
  this.$message({ showClose: true, message: msg, type: "error" });
};
Vue.prototype.msgInfo = function (msg) {
  this.$message.info(msg);
};

Vue.prototype.msgWarning = function (msg) {
  this.$message.warning(msg);
};

const Plugins = [hasPermission];

Plugins.map((plugin) => {
  Vue.use(plugin);
});
new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app");
