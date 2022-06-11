import request from '@/utils/request'

const commonPrefix = `/business/lesson/`
// 查询数据列表
export function getListLesson(params) {
  return request({
    url: `${commonPrefix}getLessonList`,
    method: 'get',
    params,
  })
}

// 查询数据信息
export function getLessonInfo(id) {
  return request({
    url: `${commonPrefix}getLessonInfo/` + id,
    method: 'get',
  })
}

// 新增数据
export function addLesson(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 修改数据
export function updateLesson(id, data) {
  return request({
    url: `${commonPrefix}update/${id}`,
    method: 'put',
    data: data,
  })
}

// 删除数据
export function delLesson(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}

// 课程上下架
export function updateStatus(id, status) {
  return request({
    url: `${commonPrefix}updateStatus/` + id + `/` + status,
    method: 'put',
  })
}

export function updateLessonStatus(id, status) {
  return request({
    url: `${commonPrefix}updateLessonStatus/` + id + `/` + status,
    method: 'put',
  })
}
