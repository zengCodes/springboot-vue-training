import Vue from "vue";
import VueRouter from "vue-router";

const originalPush = VueRouter.prototype.push;

VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => err);
};
Vue.use(VueRouter);

export const constantRoutes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "login",
    component: (resolve) => require(["@/views/login"], resolve),
  },
  {
    path: "/home",
    name: "home",
    component: (resolve) => require(["@/views/home"], resolve),
    redirect: "/welcome",
    children: [
      {
        path: "/welcome",
        name: "welcome",
        component: (resolve) => require(["@/views/welcome"], resolve),
      },
      {
        path: "/menu",
        name: "menu",
        component: (resolve) => require(["@/views/system/menu/index"], resolve),
      },
      {
        path: "/user",
        name: "user",
        component: (resolve) => require(["@/views/system/user/index"], resolve),
      },
      {
        path: "/profile",
        name: "profile",
        component: (resolve) => require(["@/views/system/profile"], resolve),
      },
      {
        path: "/noticeMsg",
        name: "chat",
        component: (resolve) =>
          require(["@/views/business/friendManger"], resolve),
      },
      {
        path: "/dict",
        name: "dict",
        component: (resolve) => require(["@/views/system/dict/index"], resolve),
      },
      {
        path: "/role",
        name: "role",
        component: (resolve) => require(["@/views/system/role/index"], resolve),
      },
      {
        path: "/category",
        name: "category",
        component: (resolve) =>
          require(["@/views/business/category"], resolve),
      },
      {
        path: "/courseList",
        name: "course",
        component: (resolve) =>
          require(["@/views/business/course"], resolve),
      },
      {
        path: "/chapter",
        name: "chapter",
        component: (resolve) =>
          require(["@/views/business/chapter"], resolve),
      },
      {
        path: "/examPaper",
        name: "examPaper",
        component: (resolve) =>
          require(["@/views/business/exam/paper"], resolve),
      },
      {
        path: "/question",
        name: "question",
        component: (resolve) =>
          require(["@/views/business/exam/question"], resolve),
      },
      {
        path: "/practice",
        name: "practice",
        component: (resolve) =>
          require(["@/views/business/practice"], resolve),
      },
      {
        path: "/userPaper",
        name: "userPaper",
        component: (resolve) =>
          require(["@/views/business/practice/userPaper"], resolve),
      },
      {
        path: "/resource",
        name: "resource",
        component: (resolve) =>
          require(["@/views/business/resource"], resolve),
      },
      {
        path: "/albums",
        name: "albums",
        component: (resolve) =>
          require(["@/views/business/resource/albums/index"], resolve),
      },
      {
        path: "/article",
        name: "article",
        component: (resolve) =>
          require(["@/views/business/article"], resolve),
      },
      {
        path: "/lecturerList",
        name: "lecturer",
        component: (resolve) =>
          require(["@/views/business/lecturer/index"], resolve),
      },
      {
        path: "/lecturerApproval",
        name: "lecturerApproval",
        component: (resolve) =>
          require(["@/views/business/lecturer/lecturerApproval"], resolve),
      },
      {
        path: "/lessonList",
        name: "lesson",
        component: (resolve) =>
          require(["@/views/business/lesson"], resolve),
      },
      {
        path: "/lessonApproval",
        name: "lessonApproval",
        component: (resolve) =>
          require(["@/views/business/lesson/lessonApproval"], resolve),
      },
      {
        path: "/article",
        name: "article",
        component: (resolve) =>
          require(["@/views/business/article"], resolve),
      },
      {
        path: "/discuss",
        name: "discuss",
        component: (resolve) => require(["@/views/business/discuss/index"], resolve),
      },
      {
        path: "/order",
        name: "order",
        component: (resolve) => require(["@/views/business/order/index"], resolve),
      },
      {
        path: "/notice",
        name: "notice",
        component: (resolve) => require(["@/views/system/notice/index"], resolve),
      },
      {
        path: "/config",
        name: "config",
        component: (resolve) => require(["@/views/system/config/index"], resolve),
      },
      {
        path: "/logs",
        name: "logs",
        component: (resolve) => require(["@/views/monitor/log/index"], resolve), //操作日志
      },
      {
        path: "/cache",
        name: "cache",
        component: (resolve) => require(["@/views/monitor/cache/index"], resolve),
      },
      {
        path: "/online",
        name: "online",
        component: (resolve) => require(["@/views/monitor/online/index"], resolve),
      },
      {
        path: "/server",
        name: "server",
        component: (resolve) => require(["@/views/monitor/server/index"], resolve),
      },
      {
        path: "/login-log",
        name: "loginLogs",
        component: (resolve) => require(["@/views/monitor/loginLog/index"], resolve), //登入日志
      },
      {
        path: "/swagger",
        name: "swaggerUI",
        component: (resolve) => require(["@/views/monitor/swagger/index"], resolve), //接口文档
      },
    ],
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes: constantRoutes,
});

export default router;
