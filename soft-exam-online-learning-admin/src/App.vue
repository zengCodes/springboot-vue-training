<template>
  <div id="app">
    <router-view />
  </div>
</template>
<style>
html,
body {
  margin: 0px;
  padding: 0px;
  width: 100%;
  height: 100%;
}
li {
  list-style: none;
}
#app {
  width: 100%;
  height: 100% !important;
  margin: 0px;
  padding: 0px;
  position: absolute;
  background: #2d3a4b;
}
#nprogress .bar {
  background: rgb(96, 47, 231) !important;
}
/* 滚动槽 */
::-webkit-scrollbar {
  width: 3px;
  height: 3px;
}
::-webkit-scrollbar-track {
  border-radius: 3px;
  background: rgba(0, 0, 0, 0.06);
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.08);
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: rgba(0, 0, 0, 0.12);
  -webkit-box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.2);
}
</style>
