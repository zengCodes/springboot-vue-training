export const hasPermission = {
  install(Vue) {
    Vue.directive('hasPermission', {
      bind(el, binding, vnode) {
        var flag = false //默认不显示
        let userInfo = vnode.context.$store.state.user
        var value = binding.value
        if (userInfo.roles[0] === 'admin') {
          //如果是超级管理员
          flag = true
        } else if (userInfo.permissions.indexOf(value) != -1) {
          //如果有该权限按钮显示
          flag = true
        }
        if (!flag) {
          // if (!el.parentNode) {
          //   el.style.display = 'none'
          // } else {
          //   el.parentNode.removeChild(el);
          // }
          el.setAttribute('disabled', true)
          el.classList.add('is-disabled')
        }
      },
    })
  },
}
