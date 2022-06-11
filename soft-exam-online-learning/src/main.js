import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import "@/utils/directives.js";
import { hasPermission } from "./utils/permissionDirect";
import "./permission";
import VueParticles from "vue-particles";
import VueAwesomeSwiper from "vue-awesome-swiper";
// md文档编辑
import mavonEditor from "mavon-editor";

//引入样式
import "swiper/css/swiper.css";
import "@/assets/styles/index.scss";
import divDrag from "./utils/divDrag.js";
import "mavon-editor/dist/css/index.css";

Vue.directive("divDrag", divDrag);
Vue.use(VueAwesomeSwiper);
//引入线条背景
Vue.use(VueParticles);
Vue.use(ElementUI);
Vue.use(mavonEditor);

Vue.config.productionTip = false;

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
