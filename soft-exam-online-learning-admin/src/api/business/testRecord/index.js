import request from "@/utils/request";

const commonPrefix = `/business/testRecord/`;

// 查询数据列表
export function getRecordList(query) {
  return request({
    url: `${commonPrefix}getRecordList`,
    method: "get",
    params: query,
  });
}

// 删除试卷
export function delPaper(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: "delete",
  });
}
