import request from '@/utils/request'

const commonPrefix = `/business/lesson/`
// 查询数据列表
export function getListLesson(query) {
  return request({
    url: `${commonPrefix}getLessonList`,
    method: 'get',
    params: query,
  })
}

export function getLessonList(query) {
  return request({
    url: `${commonPrefix}getLessonList`,
    method: 'get',
    params: query,
  })
}

// 查询数据信息
export function getLesson(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 查询课程树
export function lessonTree(status) {
  return request({
    url: `${commonPrefix}lessonTree`,
    method: 'get',
    params: {
      status
    }
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

// 保存学习记录
export function saveLessonStudy(id) {
  return request({
    url: `${commonPrefix}saveLessonStudy/` + id,
    method: 'put',
  })
}
