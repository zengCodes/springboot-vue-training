import Cookies from 'js-cookie'

const paper = {
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
    // 保存试卷
    setPaperData({ commit }, paper) {
      commit('SET_PAPER', paper)
    },
  },
}

export default paper
