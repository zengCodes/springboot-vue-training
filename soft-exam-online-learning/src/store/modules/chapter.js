import Cookies from 'js-cookie'

const chapter = {
  state: {
    currentChapter: null,
  },

  mutations: {
    SET_CHAPTER: (state, chapter) => {
      state.currentChapter = chapter
      Cookies.set('chapter', chapter)
    },
  },

  actions: {
    // 登录
    setChapterData({ commit }, chapter) {
      commit('SET_CHAPTER', chapter)
    },
  },
}

export default chapter
