<template>
  <div>
    <template v-for="item in menuList">
      <el-submenu
        :disabled="item.disabled"
        :index="item.path"
        v-if="item.menuType === 'M'"
        :key="item.menuId + ''"
      >
        <template slot="title" style="padding-left: 10px">
          <i :class="'el-icon-' + item.icon"></i>
          <span slot="title">{{ item.menuName }}</span>
        </template>
        <MenuTree :menuList="item.children"></MenuTree>
      </el-submenu>
      <el-menu-item
        v-else
        :disabled="item.disabled"
        :index="item.path"
        :route="item.path"
        @click="savePath(item.path)"
        :key="item.menuId + ''"
        style="padding-left: 50px"
      >
        <i :class="'el-icon-' + item.icon"></i>
        <span>{{ item.menuName }}</span>
      </el-menu-item>
    </template>
  </div>
</template>

<script>
export default {
  name: 'MenuTree', //模板名称
  data() {
    return {}
  },
  beforeMount() {},
  props: ['menuList', 'tagList'],
  methods: {
    //保存激活路径
    savePath(path) {
      window.sessionStorage.setItem('activePath', path)
      this.$emit('currentActivePath', path)
    },
  },
  created() {},
}
</script>
<style>
.el-menu--collapse span,
.el-menu--collapse i.el-submenu__icon-arrow {
  height: 0;
  width: 0;
  overflow: hidden;
  visibility: hidden;
  display: inline-block;
}
</style>
