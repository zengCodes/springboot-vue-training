import request from '@/utils/request'

const commonPrefix = `/business/lessonType/`
// 查询数据列表
export function getListLessonType(query) {
  return request({
    url: `${commonPrefix}getLessonTypeList`,
    method: 'get',
    params: query,
  })
}

// 新增字典数据
export function addLessonType(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 修改字典数据
export function updateLessonType(id, data) {
  return request({
    url: `${commonPrefix}update/${id}`,
    method: 'put',
    data: data,
  })
}

// 删除字典数据
export function delLessonType(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}
