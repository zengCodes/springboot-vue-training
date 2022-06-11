import request from '@/utils/request'

const commonPrefix = `/business/chat/`

// 获取好友列表
export function getFriends(data) {
  return request({
    url: `${commonPrefix}getFriends`,
    method: 'get',
    params: data,
  })
}

export function getAllMsgByAccount(data) {
  return request({
    url: `${commonPrefix}getAllMsgByAccount`,
    method: 'post',
    data,
  })
}

export function getAllMsg() {
  return request({
    url: `${commonPrefix}getAllMsg`,
    method: 'get',
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
