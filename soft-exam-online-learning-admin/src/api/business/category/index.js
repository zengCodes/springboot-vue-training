import request from '@/utils/request'

const commonPrefix = `/business/category/`

export function findCategoryList(query) {
  return request({
    url: `${commonPrefix}findCategoryList`,
    method: 'get',
    params: query,
  })
}

// 查询数据列表
export function getListCategory(query) {
  return request({
    url: `${commonPrefix}categoryTree`,
    method: 'get',
    params: query,
  })
}

export function getParentCategoryTree(params) {
  return request({
    url: `${commonPrefix}getParentCategoryTree`,
    method: 'get',
    params,
  })
}

// 查询数据列表
export function getCategory(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 新增数据
export function addCategory(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 修改数据
export function updateCategory(id, data) {
  return request({
    url: `${commonPrefix}update/${id}`,
    method: 'put',
    data: data,
  })
}

// 删除字典数据
export function delCategory(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}

// 导出字典数据
export function changCategoryStatus(query) {
  return request({
    url: `${commonPrefix}updateStatus`,
    method: 'get',
    params: query,
  })
}

// 获取科目分类树
export function getCategoryTree() {
  return request({
    url: `${commonPrefix}getCategoryTree`,
    method: 'get',
  })
}
