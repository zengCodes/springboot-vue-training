import request from '@/utils/request'

const commonPrefix = `/business/choiceQuestion/`

// 查询数据列表
export function getListChoice(query) {
  return request({
    url: `${commonPrefix}findQuestionList`,
    method: 'get',
    params: query,
  })
}


// 获取详细试卷信息
export function getChoice(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 修改试卷状态
export function updateChoice(id, data) {
  return request({
    url: `${commonPrefix}update/` + id,
    method: 'put',
    data: data,
  })
}

// 手动添加试卷
export function addChoice(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 删除试卷
export function delChoice(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}
