import request from '@/utils/request'

const commonPrefix = `/business/category/`

// 查询数据列表
export function getListCategory(query) {
  return request({
    url: `${commonPrefix}/categoryTree`,
    method: 'get',
    params: query,
  })
}

export function getParentCategoryTree() {
  return request({
    url: `${commonPrefix}getParentCategoryTree`,
    method: 'get',
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
export function updateCategory(data) {
  return request({
    url: `${commonPrefix}edit`,
    method: 'put',
    data: data,
  })
}

// 删除字典数据
export function delCategory(id) {
  return request({
    url: `${commonPrefix}delete` + id,
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
