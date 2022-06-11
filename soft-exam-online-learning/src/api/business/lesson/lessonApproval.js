import request from '@/utils/request'

const commonPrefix = `/business/lessonApproval/`
// 查询数据列表
export function getListLessonApproval(query) {
  return request({
    url: `${commonPrefix}getLessonApprovalList`,
    method: 'get',
    params: query,
  })
}

export function approvalLesson(id, data) {
  return request({
    url: `${commonPrefix}updateApprovalStatus/${id}`,
    method: 'put',
    params: data,
  })
}
