import request from '@/utils/request'

const commonPrefix = `/business/course/`
// 查询数据列表
export function getListCourse(query) {
  return request({
    url: `${commonPrefix}findCourseList`,
    method: 'get',
    params: query,
  })
}

export function getCourseList(query) {
  return request({
    url: `${commonPrefix}getCourseList`,
    method: 'get',
    params: query,
  })
}

// 查询数据列表
export function getCourse(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 新增数据
export function addCourse(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}
// 修改数据
export function updateCourse(data) {
  return request({
    url: `${commonPrefix}edit`,
    method: 'put',
    data: data,
  })
}

// 删除数据
export function delCourse(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}

// 改变状态
export function changCategoryStatus(query) {
  return request({
    url: `${commonPrefix}updateStatus`,
    method: 'get',
    params: query,
  })
}
