<template>
  <div class="msg-list">
    <div
      v-for="item in chatMsgs[user.userName + '#' + currentSession.userName]"
      :key="item.mid"
      :class="[item.mfromUser === user.userName ? 'mine' : 'other']"
    >
      <div class="user">
        <img
          :src="
            item.mfromUser === user.userName ? user.avatar : item.mfromAvatar
          "
          alt
        />
        <cite class="cite">
          {{ item.mfromUser }}
          <i>{{ item.mcreateTime | time }}</i>
        </cite>
      </div>
      <div class="content">
        <div class="content-arrow"></div>
        <span class="content-text">{{ item.mmsg }}</span>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";

export default {
  data() {
    return {};
  },
  methods: {},
  computed: {
    user() {
      return this.userInfo.user;
    },
    ...mapState({
      isToBottom: (state) => state.chat.isToBottom,
      userInfo: (state) => state.user,
      chatMsgs: (state) => state.chat.chatMsgs,
      currentSession: (state) => state.chat.currentSession,
    }),
  },
  filters: {
    time(date) {
      if (date) {
        date = new Date(date);
      }
      //当前的时间
      let currentDate = new Date();
      //与当前时间的日期间隔
      let timeInterval = currentDate.getDate() - date.getDate();
      //星期数组
      let weekdays = [
        "星期日",
        "星期一",
        "星期二",
        "星期三",
        "星期四",
        "星期五",
        "星期六",
      ];
      //时间范围
      let timeRange = "上午";
      if (date.getHours() > 12) {
        timeRange = "下午";
      }
      //如果与当前时间同日
      if (
        date.getMonth() == currentDate.getMonth() &&
        date.getDate() == currentDate.getDate()
      ) {
        return `${timeRange} ${date.getHours()}:${date.getMinutes()}`;
      }
      //在当前时间同一年且日期间隔在7天内
      if (
        date.getFullYear() == currentDate.getFullYear() &&
        timeInterval <= 6 &&
        timeInterval >= 1
      ) {
        //当前时间的前一天
        if (timeInterval == 1) {
          return `昨天 ${timeRange} ${date.getHours()}:${date.getMinutes()}`;
        } else {
          return `${
            weekdays[date.getDay()]
          } ${timeRange} ${date.getHours()}:${date.getMinutes()}`;
        }
      }
      //如果日期超过7天
      else
        return `${date.getFullYear()}-${
          date.getMonth() + 1
        }-${date.getDate()} ${timeRange}  ${date.getHours()}:${date.getMinutes()}`;
    },
  },
  watch: {
    isToBottom: {
      handler(val) {
        if (val) {
          setTimeout(() => {
            let length =
              this.chatMsgs[
                this.user.userName + "#" + this.currentSession.userName
              ]?.length;
            document
              .getElementsByClassName("msg-list")[0]
              .children[length - 1]?.scrollIntoView(true);
            this.$store.commit("scrollBottom", false);
          }, 300);
        }
      },
    },
  },
};
</script>
<style scoped lang="scss">
.msg-list {
  // position: relative;
  width: 100%;
  height: 300px;
  overflow-y: scroll;
  overflow-x: hidden;
  background-color: #f5f5f5;
  touch-action: none;
  text-size-adjust: none;
  .other {
    height: 60px;
    position: relative;
    span {
      background: #409eff;
      padding: 5px 30px;
      font-size: 14px;
      border-radius: 6px;
      color: #fff;
      position: absolute;
      top: 25px;
      left: 75px;
    }
    .user,
    .content {
      display: inline-block;
      vertical-align: top;
      font-size: 14px;
    }
    .user {
      // cursor: pointer;
      img {
        position: absolute;
        left: 12px;
        top: 5px;
        width: 40px;
        height: 40px;
        border-radius: 100%;
      }
      cite {
        position: absolute;
        left: 65px;
        /* width: 500px; */
        line-height: 24px;
        font-size: 12px;
        white-space: nowrap;
        color: #999;
        text-align: left;
        font-style: normal;
        i {
          padding-left: 5px;
          font-style: normal;
        }
      }
    }
    .content-arrow {
      top: 12px;
      left: -6px;
      position: absolute;
      display: block;
      width: 0;
      height: 0;
      border-color: transparent;
      border-style: solid;
      border-width: 6px;
      border-left-width: 0;
      border-right-color: #409eff;
      &::after {
        content: " ";
        top: 10px;
        left: 76px;
        position: absolute;
        display: block;
        width: 0;
        height: 0;
        border-color: transparent;
        border-style: solid;
        border-width: 7px;
        border-left-width: 0;
        border-right-color: #409eff;
      }
    }
  }
  .mine {
    position: relative;
    height: 75px;
    span {
      background: #409eff;
      padding: 5px 30px;
      font-size: 14px;
      border-radius: 6px;
      color: #fff;
      position: absolute;
      top: 25px;
      right: 75px;
    }
    .user,
    .content {
      display: inline-block;
      vertical-align: top;
      font-size: 14px;
    }
    .user {
      // cursor: pointer;
      img {
        position: absolute;
        right: 12px;
        top: 5px;
        width: 40px;
        height: 40px;
        border-radius: 100%;
      }
      cite {
        position: absolute;
        right: 65px;
        /* width: 500px; */
        line-height: 24px;
        font-size: 12px;
        white-space: nowrap;
        color: #999;
        text-align: left;
        font-style: normal;
        i {
          padding-right: 5px;
          font-style: normal;
        }
      }
    }
    .content-arrow {
      top: 12px;
      right: -6px;
      position: absolute;
      display: block;
      width: 0;
      height: 0;
      border-color: transparent;
      border-style: solid;
      border-width: 8px;
      border-right-width: 0;
      border-left-color: #ebeef5;
      &::after {
        content: " ";
        top: 10px;
        right: 76px;
        position: absolute;
        display: block;
        width: 0;
        height: 0;
        border-color: transparent;
        border-style: solid;
        border-width: 7px;
        border-right-width: 0;
        border-left-color: #409eff;
      }
    }
  }
  .enter-box {
    z-index: 999;
    // width: 100%;
  }
}
</style>
