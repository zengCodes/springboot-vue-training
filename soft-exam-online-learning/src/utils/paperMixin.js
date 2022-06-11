export default {
  name: 'testPaper-mixin',
  data() {
    return {}
  },

  methods: {
    //导航题目类型名称
    topicTypeName_mixin(type) {
      switch (type) {
        case 1:
          return '单选题'
        case 3:
          return '多选题'
        case 4:
          return '判断题'
        case 5:
          return '填空题'
        case 2:
          return '简答题'
      }
    },
    //获取答题的题号顺序
    bigQuestionIndex(index) {
      var bigQuestion = ['一、', '二、', '三、', '四、', '五、', '六、']
      var nav_i = 0
      for (let i = 0; i < index; i++) {
        if (this.sortedTopics[i].topic_content.length != 0) {
          nav_i += 1
        }
      }
      return bigQuestion[nav_i]
    },
    //答题类型名称
    bigQuestionName_mixin(type, index) {
      let nav_i = this.bigQuestionIndex(index)
      switch (type) {
        case 1:
          return nav_i + '单项选择题'
        case 3:
          return nav_i + '多项选择题'
        case 4:
          return nav_i + '判断题'
        case 5:
          return nav_i + '填空题'
        case 2:
          return nav_i + '简答题'
      }
    },
    //回到顶部
    backTop_mixin() {
      document.body.scrollTop = document.documentElement.scrollTop = 0
    },
    //计算題号
    topicNavIndex_mixin(Topics_index, index) {
      var navIndex = 0
      for (let i = 0; i < Topics_index; i++) {
        navIndex = navIndex + this.sortedTopics[i].topic_content.length
      }
      return navIndex + index + 1
    },
  },
}
