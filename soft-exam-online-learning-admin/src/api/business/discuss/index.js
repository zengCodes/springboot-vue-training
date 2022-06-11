import request from '@/utils/request'

const commonPrefix = `/business/discuss/`
// 查询数据列表
export function getDiscussList(params) {
  return request({
    url: `${commonPrefix}getDiscussList`,
    method: 'get',
    params,
  })
}

export function getInfo(id) {
  return request({
    url: `${commonPrefix}info/${id}`,
    method: 'get',
  })
}

// 新增数据
export function addDiscuss(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data,
  })
}
// 修改数据
export function updateDiscuss(data) {
  return request({
    url: `${commonPrefix}edit`,
    method: 'put',
    data,
  })
}

// 删除数据
export function delDiscuss(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}

// 改变状态
export function changCategoryStatus(params) {
  return request({
    url: `${commonPrefix}updateStatus`,
    method: 'get',
    params,
  })
}

// 根据名称模糊查询
export function getQuestionData(params) {
  return request({
    url: `${commonPrefix}getQuestionData`,
    method: 'get',
    params,
  })
}

export function getQuestionInfo(params) {
  return request({
    url: `${commonPrefix}getQuestionInfo`,
    method: 'get',
    params,
  })
}
