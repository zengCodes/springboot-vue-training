<template>
  <div id="toolbar">
    <el-image
      class="imgProfile"
      :src="userInfo.avatar"
      :preview-src-list="[userInfo.avatar]"
      :alt="userInfo.nickName"
    >
      <div slot="error" class="image-slot">
        <i class="el-icon-picture-outline"></i>
      </div>
    </el-image>
    <div id="btnBar">
      <div class="topBtnBar">
        <el-tooltip
          class="item"
          effect="dark"
          content="进入群聊"
          placement="right"
        >
          <el-button
            @click="chooseChatList('群聊')"
            class="toolBtn"
            size="small"
            ><i class="iconfont icon-qunliao" aria-hidden="true"></i
          ></el-button>
        </el-tooltip>
        <el-tooltip
          class="item"
          effect="dark"
          content="用户列表"
          placement="right"
        >
          <el-button
            @click="chooseChatList('私聊')"
            class="toolBtn"
            size="small"
            ><i class="iconfont icon-weixin" aria-hidden="true"></i
          ></el-button>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "toolbar",
  data() {
    return {};
  },
  computed: {
    ...mapState({
      userInfo: (state) => state.user.user,
    }),
  },
  methods: {
    //选择聊天列表
    chooseChatList(listName) {
      this.$store.commit("changeCurrentList", listName);
    },
  },
};
</script>

<style lang="scss" scoped>
#toolbar {
  width: 100%;
  height: 100%;
  #btnBar {
    width: 100%;
    height: 82%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .imgProfile {
    width: 40px;
    height: 40px;
    vertical-align: middle;
    margin: 25px 10px;
  }
  .toolBtn {
    background-color: #2e3238;
    border: 0;
    margin: 5px 5px;
  }
  .feedbackDialog {
    width: 1000px;
    height: 800px;
    margin: 10px auto;
  }
  .feedbackInput {
    width: 450px;
    height: 200px;
    resize: none;
    padding: 0;
    margin: 0;
  }
}

#moreList {
  margin: 0px;
  padding: 0px;
  background-color: #2e3238;
  li {
    padding-top: 14px;
    padding-bottom: 14px;
    padding-left: 5px;
    list-style: none;
    cursor: pointer;
    &:hover {
      background-color: #abaaaa;
    }
  }
}
.iconfont {
  font-size: 2em;
}
</style>
<style lang="scss">
/* el-popover是和app同级的，所以scoped的局部属性设置了无效 */
/* 需要设置全局style */
.el-popover.moreListPopoverClass {
  height: 150px;
  width: 150px;
  // margin: 0px;
  margin-left: 10px;
  padding: 0px;
  overflow-x: hidden;
  overflow-y: hidden;
  background-color: #2e3238;
  border: none;
}
</style>
