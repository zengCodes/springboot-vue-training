import request from '@/utils/request'

const commonPrefix = `/system/menu/`

// 查询菜单列表
export function getMenuList(query) {
  return request({
    url: `${commonPrefix}list`,
    method: 'get',
    params: query,
  })
}

// 查询菜单详细
export function getEditMenu(menuId) {
  return request({
    url: `${commonPrefix}${menuId}`,
    method: 'get',
  })
}

// 查询菜单下拉树结构
export function treeSelect() {
  return request({
    url: `${commonPrefix}treeselect`,
    method: 'get',
  })
}

// 根据角色ID查询菜单下拉树结构
export function roleMenuTreeSelect(roleId) {
  return request({
    url: `${commonPrefix}roleMenuTreeselect/${roleId}`,
    method: 'get',
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: `${commonPrefix}`,
    method: 'post',
    data: data,
  })
}

// 修改菜单
export function updateMenu(data) {
  return request({
    url: `${commonPrefix}`,
    method: 'put',
    data: data,
  })
}

// 删除菜单
export function delMenu(menuId) {
  return request({
    url: `${commonPrefix}` + menuId,
    method: 'delete',
  })
}
