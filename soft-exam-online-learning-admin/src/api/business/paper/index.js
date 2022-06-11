import request from "@/utils/request";

const commonPrefix = `/business/paper/`;

// 查询数据列表
export function getListPaper(query) {
  return request({
    url: `${commonPrefix}findPaperList`,
    method: "get",
    params: query,
  });
}

// 获取详细试卷信息
export function getPaper(id) {
  return request({
    url: `${commonPrefix}edit/${id}`,
    method: "get",
  });
}

// 导出试题到word
export function exportPaperWord(data) {
  return request({
    url: `${commonPrefix}exportPracticeWord`,
    method: "get",
    params: data,
  });
}

// 导入上午题试卷
export function importMorningPracticeWord(data) {
  return request({
    url: `${commonPrefix}importMorningPracticeWord`,
    method: "post",
    data,
  });
}
// 导入下午题
export function importAfternoonPracticeWord(data) {
  return request({
    url: `${commonPrefix}importAfternoonPracticeWord`,
    method: "post",
    data,
  });
}

// 下载试卷模板
export function downloadTemplate(params) {
  return request({
    url: `${commonPrefix}downloadTemplate`,
    method: "get",
    params,
  });
}

// 修改试卷状态
export function updatePaperStatus(id, status) {
  return request({
    url: `${commonPrefix}updateStatus/` + id + `/` + status,
    method: "put",
  });
}

// 修改试卷
export function updatePaper(id, data) {
  return request({
    url: `${commonPrefix}update/` + id,
    method: "put",
    data: data,
  });
}

// 手动添加试卷
export function addPaper(data) {
  return request({
    url: `${commonPrefix}add`,
    method: "post",
    data: data,
  });
}

// 删除
export function delPaper(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: "delete",
  });
}
