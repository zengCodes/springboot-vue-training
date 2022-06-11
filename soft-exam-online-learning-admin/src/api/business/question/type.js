import request from '@/utils/request'

const commonPrefix = `/business/questionType/`

// 查询数据列表
export function getListType(query) {
  return request({
    url: `${commonPrefix}findQuestionTypeList`,
    method: 'get',
    params: query,
  })
}


// 获取详细试卷信息
export function getType(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 修改
export function updateType(id, data) {
  return request({
    url: `${commonPrefix}update/` + id,
    method: 'put',
    data: data,
  })
}

// 手动添加试卷
export function addType(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 删除试卷
export function delType(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}
