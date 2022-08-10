/*
 * @Description: 
 * @Author: zenghuiqing
 * @Date: 2021-12-24 19:30:32
 */
const login = {
  state: {
    showLogin: false,
    action: '',
  },

  mutations: {
    SET_SHOW_LOGIN: (state, showLogin) => {
      state.showLogin = showLogin
    },
    SET_LOGIN_ACTION: (state, action) => {
      state.action = action
    },
  },
  actions: {
    setShowLogin({ commit }, login) {
      commit('SET_SHOW_LOGIN', login)
    },
    setLoginAction({ commit }, action) {
      console.log(action)
      commit('SET_LOGIN_ACTION', action)
    },
  },
}

export default login
