import Vue from "vue";
import VueRouter from "vue-router";

const originalPush = VueRouter.prototype.push;

VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => err);
};
Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "login",
    component: (resolve) => require(["@/views/Login"], resolve),
  },
  {
    path: "/home",
    name: "home",
    component: (resolve) => require(["@/views/Home"], resolve),
    redirect: "/welcome",
    children: [
      {
        path: "/welcome",
        name: "welcome",
        component: (resolve) => require(["@/views/Welcome"], resolve),
      },
      {
        path: "/menu",
        name: "menu",
        component: (resolve) => require(["@/views/system/Menu"], resolve),
      },
      {
        path: "/user",
        name: "user",
        component: (resolve) => require(["@/views/system/User"], resolve),
      },
      {
        path: "/profile",
        name: "profile",
        component: (resolve) => require(["@/views/system/profile"], resolve),
      },
      {
        path: "/chat",
        name: "chat",
        component: (resolve) =>
          require(["@/views/business/friendManger/ChatMessage"], resolve),
      },
      {
        path: "/dict",
        name: "dict",
        component: (resolve) => require(["@/views/system/Dict"], resolve),
      },
      {
        path: "/role",
        name: "role",
        component: (resolve) => require(["@/views/system/Role"], resolve),
      },
      {
        path: "/category",
        name: "category",
        component: (resolve) =>
          require(["@/views/business/category/category"], resolve),
      },
      {
        path: "/course",
        name: "course",
        component: (resolve) =>
          require(["@/views/business/course/Course"], resolve),
      },
      {
        path: "/chapter",
        name: "chapter",
        component: (resolve) =>
          require(["@/views/business/chapter/chapter"], resolve),
      },
      {
        path: "/examPaper",
        name: "examPaper",
        component: (resolve) =>
          require(["@/views/business/exam/Paper"], resolve),
      },
      {
        path: "/question",
        name: "question",
        component: (resolve) =>
          require(["@/views/business/exam/Question"], resolve),
      },
      {
        path: "/practice",
        name: "practice",
        component: (resolve) =>
          require(["@/views/business/practice/practice"], resolve),
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
          require(["@/views/business/resource/Resource"], resolve),
      },
      {
        path: "/albums",
        name: "albums",
        component: (resolve) =>
          require(["@/views/business/resource/Albums"], resolve),
      },
      {
        path: "/article",
        name: "article",
        component: (resolve) =>
          require(["@/views/business/article/Article"], resolve),
      },
      {
        path: "/lecturer",
        name: "lecturer",
        component: (resolve) =>
          require(["@/views/business/lecturer/Lecturer"], resolve),
      },
      {
        path: "/lecturerApproval",
        name: "lecturerApproval",
        component: (resolve) =>
          require(["@/views/business/lecturer/LecturerApproval"], resolve),
      },
      {
        path: "/lesson",
        name: "lesson",
        component: (resolve) =>
          require(["@/views/business/lesson/Lesson"], resolve),
      },
      {
        path: "/lessonApproval",
        name: "lessonApproval",
        component: (resolve) =>
          require(["@/views/business/lesson/LessonApproval"], resolve),
      },
      {
        path: "/article",
        name: "article",
        component: (resolve) =>
          require(["@/views/business/article/Article"], resolve),
      },
      {
        path: "/discuss",
        name: "discuss",
        component: (resolve) => require(["@/views/business/discuss"], resolve),
      },
      {
        path: "/order",
        name: "order",
        component: (resolve) => require(["@/views/business/order"], resolve),
      },
      {
        path: "/notice",
        name: "notice",
        component: (resolve) => require(["@/views/system/Notice"], resolve),
      },
      {
        path: "/config",
        name: "config",
        component: (resolve) => require(["@/views/system/Config"], resolve),
      },
      {
        path: "/logs",
        name: "logs",
        component: (resolve) => require(["@/views/monitor/Log"], resolve), //操作日志
      },
      {
        path: "/cache",
        name: "cache",
        component: (resolve) => require(["@/views/monitor/Cache"], resolve),
      },
      {
        path: "/online",
        name: "online",
        component: (resolve) => require(["@/views/monitor/OnLine"], resolve),
      },
      {
        path: "/server",
        name: "server",
        component: (resolve) => require(["@/views/monitor/Server"], resolve),
      },
      {
        path: "/login-log",
        name: "loginLogs",
        component: (resolve) => require(["@/views/monitor/LoginLog"], resolve), //登入日志
      },
      {
        path: "/swagger",
        name: "swaggerUI",
        component: (resolve) => require(["@/views/monitor/SwaggerUI"], resolve), //接口文档
      },
    ],
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
