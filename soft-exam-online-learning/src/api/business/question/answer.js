import request from '@/utils/request'

const commonPrefix = `/business/answerQuestion/`

// 查询数据列表
export function getListAnswer(query) {
  return request({
    url: `${commonPrefix}findQuestionList`,
    method: 'get',
    params: query,
  })
}

// 所有类型数据
export function getAllListAnswer() {
  return request({
    url: `${commonPrefix}findAll`,
    method: 'get',
  })
}

// 获取详细试卷信息
export function getAnswer(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 修改试卷状态
export function updateAnswer(id, data) {
  return request({
    url: `${commonPrefix}update/` + id,
    method: 'put',
    data: data,
  })
}

// 手动添加试卷
export function addAnswer(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 删除试卷
export function delAnswer(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}
