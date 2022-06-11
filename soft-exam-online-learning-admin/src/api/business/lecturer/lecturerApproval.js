import request from '@/utils/request'

const commonPrefix = `/business/lecturerApproval/`
// 查询数据列表
export function getListLecturerApproval(query) {
  return request({
    url: `${commonPrefix}getLecturerApprovalList`,
    method: 'get',
    params: query,
  })
}

export function approvalLecturerStatus(id, data) {
  return request({
    url: `${commonPrefix}updateApprovalStatus/${id}`,
    method: 'put',
    data: data,
  })
}

// 新增字典数据
export function addLecturerApproval(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 修改字典数据
export function updateLecturerApproval(data) {
  return request({
    url: `${commonPrefix}edit`,
    method: 'put',
    data: data,
  })
}

// 删除数据
export function delLecturerApproval(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}
