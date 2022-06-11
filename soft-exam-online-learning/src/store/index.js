import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import user from './modules/user'
import getters from './getters'
import permission from './modules/permission'
import paper from './modules/paper'
import login from './modules/login'
import chapter from './modules/chapter'
import enumItem from './modules/enumItem'
import chat from './modules/chat'
Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    user,
    permission,
    paper,
    enumItem,
    login,
    chapter,
    chat,
  },
  getters,
})
/**
 * 监听state.sessions，有变化就重新保存到local Storage中chat-session中
 */
store.watch(
  function (state) {
    return state.chatMsgs
  },
  function (val) {
    console.log('CHANGE: ', val)
    localStorage.setItem('chat-session', JSON.stringify(val))
  },
  {
    deep: true,
  }
)
export default store
