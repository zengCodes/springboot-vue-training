<template>
  <div class="video">
    <div class="header">
      <span class="header-title"
        >{{ courseInfo.name }} > {{ currentChapter.title }}</span
      >
    </div>
    <div class="main">
      <div class="left-main"></div>
      <div class="right-main">
        <div class="video-play">
          <video
            id="videoElement"
            class="video"
            controls
            autoplay
            muted
          ></video>
        </div>
        <div class="bottom-main"></div>
      </div>
    </div>
  </div>
</template>
<script>
import { getChapter } from "@/api/business/chapter";
import flvjs from "flv.js/dist/flv.min.js";
import { getLesson } from "@/api/business/lesson/lesson";

export default {
  data() {
    return {
      currentChapter: {
        title: "",
      },
      courseInfo: {
        name: "",
      },
    };
  },
  created() {},
  mounted() {
    let id = this.$route.query.id;
    let lesson = this.$route.query.lesson;
    this.getLessonInfo(lesson);
    this.getChapterData(id);
  },
  methods: {
    // 获取课程信息
    async getLessonInfo(id) {
      let res = await getLesson(id);
      let { code, data } = res;
      if (code === 200) {
        this.courseInfo = data;
      }
    },
    // 获取章节信息
    async getChapterData(id) {
      let res = await getChapter(id);
      let { code, data } = res;
      if (code === 200) {
        this.currentChapter = data;
        this.createVideo(this.currentChapter.video);
      }
    },
    createVideo(video) {
      if (flvjs.isSupported()) {
        var videoElement = document.getElementById("videoElement");
        var flvPlayer = flvjs.createPlayer({
          type: "flv",
          url: video,
        });
        flvPlayer.attachMediaElement(videoElement);
        flvPlayer.load();
        flvPlayer.play();
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  margin-bottom: 1px;
  padding: 10px 16px;
  height: 45px;
  line-height: 45px;
  background: #181a1f;
  .header-title {
    color: #fff;
    margin-left: 80px;
  }
}
.main {
  height: 100vh;
  display: -webkit-flex;
  display: flex;
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  position: relative;
  -webkit-box-align: start;
  -ms-flex-align: start;
  align-items: flex-start;
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  -o-box-sizing: border-box;
  -ms-box-sizing: border-box;
  box-sizing: border-box;
  .left-main {
    width: 80px;
    height: 100%;
    margin-top: -5px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
    background: #181a1f;
    -moz-box-sizing: border-box;
    -webkit-box-sizing: border-box;
    -o-box-sizing: border-box;
    -ms-box-sizing: border-box;
    box-sizing: border-box;
  }
  .right-main {
    margin-top: -5px;
    width: calc(100% - 80px - 2px - 320px);
    height: 100%;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    flex: 1;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
    background: #000000;
    -moz-box-sizing: border-box;
    -webkit-box-sizing: border-box;
    -o-box-sizing: border-box;
    -ms-box-sizing: border-box;
    box-sizing: border-box;
    .video-play {
      position: relative;
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      -webkit-box-orient: vertical;
      -webkit-box-direction: normal;
      -ms-flex-direction: column;
      flex-direction: column;
      min-height: 0;
      background-color: #000;
      background-repeat: no-repeat;
      background-size: 100%;
      background-position: center;
      .video {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 500px;
      }
    }
  }
  .bottom-main {
    position: relative;
    display: -webkit-flex;
    display: flex;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
    height: 180px;
    background: #181a1f;
  }
}
</style>
