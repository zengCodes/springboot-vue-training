import request from '@/utils/request'

const commonPrefix = `/business/lessonLecturer/`
// 查询数据列表
export function getListLecturer(query) {
  return request({
    url: `${commonPrefix}getLessonLecturerList`,
    method: 'get',
    params: query,
  })
}

export function approvalLecturerList(id, data) {
  return request({
    url: `${commonPrefix}updateApprovalStatus/${id}`,
    method: 'put',
    params: data,
  })
}

// 新增字典数据
export function addLecturer(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 修改数据
export function updateLecturer(data) {
  return request({
    url: `${commonPrefix}edit`,
    method: 'put',
    data: data,
  })
}

// 删除数据
export function delLecturer(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}

// 启用-禁用
export function changLecturerStatus(id, status) {
  return request({
    url: `${commonPrefix}updateStatus/${id}/${status}`,
    method: 'put',
  })
}

// 获取详细信息
export function getLecturerData(id) {
  return request({
    url: `${commonPrefix}edit/${id}`,
    method: 'get',
  })
}
