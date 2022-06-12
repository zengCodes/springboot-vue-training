/*
 * @Description: 
 * @Author: zenghuiqing
 * @Date: 2022-06-11 15:26:28
 */
import hasRole from './permission/hasRole'
import hasPermission from './permission/hasPermission'
import drag from "./drag";

const install = function (Vue) {
  Vue.directive("divDrag", drag);
  Vue.directive('hasRole', hasRole)
  Vue.directive('hasPermission', hasPermission)
}

if (window.Vue) {
  window['hasRole'] = hasRole
  window['hasPermission'] = hasPermission
  window['divDrag'] = divDrag
  Vue.use(install) 
}

export default install
