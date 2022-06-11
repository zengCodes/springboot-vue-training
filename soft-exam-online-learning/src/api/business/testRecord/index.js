import request from "@/utils/request";

const commonPrefix = `/business/testRecord/`;

// 获取考试记录列表
export function getRecordList(params) {
  return request({
    url: `${commonPrefix}getRecordList`,
    method: "get",
    params,
  });
}

// 获取用户考试题目信息
export function getPracticeList(params) {
  return request({
    url: `${commonPrefix}getPracticeList`,
    method: "get",
    params,
  });
}

// 保存考试记录
export function saveTestRecord(data) {
  return request({
    url: `${commonPrefix}saveTestRecord`,
    method: "post",
    data,
  });
}

// 保存练习记录
export function saveExerciseRecord(data) {
  return request({
    url: `${commonPrefix}saveExerciseRecord`,
    method: "post",
    data,
  });
}

export function getTestInfo(id) {
  return request({
    url: `${commonPrefix}getTestInfo/` + id,
    method: "get",
  });
}


