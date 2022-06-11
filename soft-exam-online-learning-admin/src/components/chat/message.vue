<template>
  <div id="message" v-scroll-bottom="chatMsgs">
    <div v-if="currentSession && currentSession.friendAccount != '群聊'">
      <ul>
        <li v-for="entry in chatMsgs[msgKey]" :key="entry.mid">
          <p class="time">
            <span>{{ entry.mcreateTime | time }}</span>
          </p>
          <div :class="entry.mfromUser === user.userName ? 'self' : 'main'">
            <span
              class="username"
              :class="
                entry.mfromUser === user.userName ? 'one-right' : 'one-left'
              "
              >{{ entry.mfromUser }}</span
            >
            <img
              class="avatar"
              :src="
                entry.mfromUser === user.userName
                  ? user.avatar
                  : entry.mfromAvatar
              "
              alt=""
            />
            <p v-if="entry.mtype === 1" class="text">
              {{ entry.mmsg }}
            </p>
            <img v-if="entry.mtype === 2" :src="entry.mmsg" class="img" />
          </div>
        </li>
      </ul>
    </div>
    <div v-else>
      <ul>
        <li v-for="entry in chatMsgs['群聊']" :key="entry.mid">
          <p class="time">
            <span>{{ entry.mcreateTime | time }}</span>
          </p>
          <div
            class="main"
            :class="{ self: entry.mfromUser === user.userName }"
          >
            <span class="username">{{ entry.mfromUser }}</span>
            <img
              @dblclick="takeAShot"
              class="avatar"
              :src="
                entry.mfromUser === user.userName
                  ? user.avatar
                  : entry.mfromAvatar
              "
              alt=""
            />
            <div v-if="entry.mtype == 1">
              <p class="text" v-html="entry.mmsg"></p>
            </div>
            <div v-else>
              <!--图片预览与无法加载图片的图标-->
              <el-image
                :src="entry.mmsg"
                :preview-src-list="[entry.mmsg]"
                class="img"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "message",
  computed: {
    user() {
      return this.userInfo.user;
    },
    msgKey() {
      if (this.user.admin) {
        return this.user.userName + "#" + this.currentSession.userAccount;
      } else {
        return this.user.userName + "#" + this.currentSession.friendAccount;
      }
    },
    ...mapState({
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
  directives: {
    /*这个是vue的自定义指令,官方文档有详细说明*/
    // 发送消息后滚动到底部,这里无法使用原作者的方法，也未找到合理的方法解决，暂用setTimeout的方法模拟
    "scroll-bottom"(el) {
      //console.log(el.scrollTop);
      setTimeout(function () {
        el.scrollTop += 9999;
      }, 1);
    },
  },
  methods: {
    takeAShot(fromName, toName) {
      console.log("拍了一怕");
      let s = fromName + "拍了拍" + toName;
    },
  },
};
</script>

<style lang="scss" scoped>
#message {
  padding: 15px;
  height: 60%;
  max-height: 63%;
  overflow-y: scroll;
  overflow-x: hidden;
  ul {
    list-style-type: none;
    padding-left: 0px;
    li {
      margin-bottom: 15px;
    }
  }
  .time {
    text-align: center;
    margin: 7px 0;
    > span {
      display: inline-block;
      padding: 0 18px;
      font-size: 12px;
      color: #fff;
      background-color: #dcdcdc;
      border-radius: 2px;
    }
  }
  .main {
    .avatar {
      float: left;
      margin: 0 10px 0 0;
      border-radius: 3px;
      width: 30px;
      height: 30px;
    }
    .text {
      display: inline-block;
      padding: 0 10px;
      max-width: 80%;
      background-color: #fafafa;
      border-radius: 4px;
      line-height: 30px;
      font-size: 14px;
    }
    .img {
      display: inline-block;
      height: 100px;
      width: 100px;
      margin-top: 15px;
    }
    .username {
      position: relative;
      // left: 35px;
      top: 11px;
      margin: 0 0;
      padding: 0 0;
      border-radius: 4px;
      line-height: 15px;
      font-size: 10px;
      color: grey;
    }
    .one-left {
      top: -30px !important;
      left: 10px !important;
    }
  }
  .self {
    text-align: right;
    .avatar {
      float: right;
      margin: 0 0 0 10px;
      border-radius: 3px;
      width: 30px;
      height: 30px;
    }
    .text {
      display: inline-block;
      padding: 0 10px;
      max-width: 80%;
      background-color: #b2e281;
      border-radius: 4px;
      line-height: 30px;
    }
    .img {
      display: inline-block;
      height: 100px;
      width: 100px;
      margin-top: 15px;
    }
    .username {
      position: relative;
      margin: 0 0;
      padding: 0 0;
      width: 200px;
      line-height: 15px;
      font-size: 10px;
      color: grey;
    }
    .one-right {
      right: -50px !important;
      top: -30px;
    }
  }
}
</style>
