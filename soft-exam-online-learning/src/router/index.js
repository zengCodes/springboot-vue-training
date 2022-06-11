import Vue from 'vue'
import VueRouter from 'vue-router'

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/index',
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login.vue'),
  },
  {
    path: '/logout',
    redirect: '/',
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/about'),
  },
  {
    path: '/index',
    name: 'index',
    component: () => import('@/views/index.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'home',
        component: () => import('@/views/home'),
      },
      {
        path: '/exam',
        name: 'exam',
        component: () => import('@/views/exam'),
      },
      {
        path: '/exercise',
        name: 'exercise',
        component: () => import('@/views/exercise'),
      },
      {
        path: '/practice',
        name: 'practice',
        component: () => import('@/views/practice'),
      },
      {
        path: '/record',
        name: 'record',
        component: () => import('@/views/record'),
      },
      {
        path: '/community',
        name: 'community',
        component: () => import('@/views/community'),
      },
      {
        path: '/discuss',
        name: 'discuss',
        component: () => import('@/views/discuss'),
      },
      {
        path: '/lesson',
        name: 'lesson',
        component: () => import('@/views/lesson/lesson'),
      },
      {
        path: '/order',
        name: 'order',
        component: () => import('@/views/order'),
      },
      {
        path: '/pay',
        name: 'pay',
        component: () => import('@/views/order/pay'),
      },
      {
        path: '/lessonIntroduce',
        name: 'lessonIntroduce',
        component: () => import('@/views/lesson/introduce'),
      },
      {
        path: '/paper',
        name: 'paper',
        component: () => import('@/views/paper/paper'),
      },
      {
        path: '/paperInformation',
        name: 'paperInformation',
        component: () => import('@/views/paper/information'),
      },
      {
        path: '/paperDetail',
        name: 'paperDetail',
        component: () => import('@/views/paper/detail'),
      },
      {
        path: '/video',
        name: 'video',
        component: () => import('@/views/lesson/video'),
      },
      {
        path: '/userCenter',
        name: 'userCenter',
        component: () => import('@/views/user'),
      },
      {
        path: '/error/401',
        component: () => import('@/views/error/401.vue'),
      },
      {
        path: '/error/404',
        component: () => import('@/views/error/404.vue'),
      },
    ],
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
})

export default router
