import request from '@/utils/request'

const commonPrefix = `/business/chat/`

// 获取好友列表
export function getFriends(params) {
  return request({
    url: `${commonPrefix}getFriends`,
    method: 'get',
    params,
  })
}

// 添加好友请求
export function appendFriend(data) {
  return request({
    url: `${commonPrefix}appendFriend`,
    method: 'post',
    data: data,
  })
}

// 获取聊天信息
export function getChatMsg(data) {
  return request({
    url: `${commonPrefix}getChatMsg`,
    method: 'post',
    data,
  })
}

export function getAllMsgByAccount(params) {
  return request({
    url: `${commonPrefix}getAllMsgByAccount`,
    method: 'get',
    params,
  })
}

// 获取所有管理员
export function selectAllAdminFriends() {
  return request({
    url: `${commonPrefix}selectAllAdminFriends`,
    method: 'get',
  })
}

// 发送消息
export function insertMessage(data) {
  return request({
    url: `${commonPrefix}getChatMsg`,
    method: 'post',
    data: data,
  })
}


export function addMailMsg(data) {
  return request({
    url: `/mail/feedback`,
    method: 'post',
    data,
  })
}
