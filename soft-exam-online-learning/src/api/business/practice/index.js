import request from '@/utils/request'

const commonPrefix = `/business/exam/`

export function sendPracticeList(id,testDTO) {
  return request({
    url: `${commonPrefix}sendPractice`,
    method: 'post',
    data: {
      id,
      testDTO
    }
  })
}



