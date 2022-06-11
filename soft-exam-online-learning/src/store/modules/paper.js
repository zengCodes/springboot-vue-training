import Cookies from 'js-cookie'

export default {
  state: {
    paperData: null,
  },

  mutations: {
    SET_PAPER: (state, paper) => {
      state.paperData = paper
      Cookies.set('paper', paper)
    },
  },

  actions: {
    // 登录
    setPaperData({ commit }, paper) {
      commit('SET_PAPER', paper)
    },
  },
}
