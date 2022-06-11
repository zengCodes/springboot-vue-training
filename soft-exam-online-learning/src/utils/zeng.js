/**
 * 保存
 * @param  {Blob} blob
 * @param  {String} filename 想要保存的文件名称
 */

function saveAs(blob, filename) {
  if (window.navigator.msSaveOrOpenBlob) {
    navigator.msSaveBlob(blob, filename);
  } else {
    var link = document.createElement("a");
    var body = document.querySelector("body");

    link.href = window.URL.createObjectURL(blob);
    link.download = filename;

    // fix Firefox
    link.style.display = "none";
    body.appendChild(link);

    link.click();
    body.removeChild(link);

    window.URL.revokeObjectURL(link.href);
  }
}

/**
 * 获取 blob
 * @param  {String} url 目标文件地址
 * @return {cb}
 */
function getBlob(url, cb) {
  var xhr = new XMLHttpRequest();
  xhr.open("GET", url, true);
  xhr.responseType = "blob";
  xhr.onload = function () {
    if (xhr.status === 200) {
      cb(xhr.response);
    }
  };
  xhr.send();
}

// 通用下载方法
export function download(file_url, fileName) {
  getBlob(process.env.VUE_APP_BASE_API + file_url, function (blob) {
    saveAs(blob, fileName);
  });
}

export function getUserStatus() {
  return [
    {
      label: "启用",
      value: 1,
    },
    {
      label: "禁用",
      value: 0,
    },
  ];
}

export function getSexOption() {
  return [
    {
      label: "男",
      value: 0,
    },
    {
      label: "女",
      value: 1,
    },
  ];
}

export function getApprovalStatus(status) {
  switch (status) {
    case 0:
      return "待审核";
    case 1:
      return "审核已通过";
    case 2:
      return "审核不通过";
    default:
      return "";
  }
}

// 转换字符串，undefined,null等转化为""
export function praseStrEmpty(str) {
  if (!str || str == "undefined" || str == "null") {
    return "";
  }
  return str;
}

// 回显数据字典
export function selectDictLabel(datas, value) {
  var actions = [];
  Object.keys(datas).some((key) => {
    if (datas[key].dictValue == "" + value) {
      actions.push(datas[key].dictLabel);
      return true;
    }
  });
  return actions.join("");
}

// 回显数据字典（字符串数组）
export function selectDictLabels(datas, value, separator) {
  var actions = [];
  var currentSeparator = undefined === separator ? "," : separator;
  var temp = value.split(currentSeparator);
  Object.keys(value.split(currentSeparator)).some((val) => {
    Object.keys(datas).some((key) => {
      if (datas[key].dictValue == "" + temp[val]) {
        actions.push(datas[key].dictLabel + currentSeparator);
      }
    });
  });
  return actions.join("").substring(0, actions.join("").length - 1);
}

// 日期格式化
export function parseTime(time, pattern) {
  if (arguments.length === 0 || !time) {
    return null;
  }
  const format = pattern || "{y}-{m}-{d} {h}:{i}:{s}";
  let date;
  if (typeof time === "object") {
    date = time;
  } else {
    if (typeof time === "string" && /^[0-9]+$/.test(time)) {
      time = parseInt(time);
    } else if (typeof time === "string") {
      time = time.replace(new RegExp(/-/gm), "/");
    }
    if (typeof time === "number" && time.toString().length === 10) {
      time = time * 1000;
    }
    date = new Date(time);
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay(),
  };
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key];
    // Note: getDay() returns 0 on Sunday
    if (key === "a") {
      return ["日", "一", "二", "三", "四", "五", "六"][value];
    }
    if (result.length > 0 && value < 10) {
      value = "0" + value;
    }
    return value || 0;
  });
  return time_str;
}

// 下载方法
export function downloadResource(name, path) {
  var elemIF = document.createElement("iframe");
  elemIF.src =
    process.env.VUE_APP_BASE_API +
    "/common/download?&filename=" +
    encodeURI(name) +
    "&location=" +
    path;
  elemIF.style.display = "none";
  document.body.appendChild(elemIF);
}

export const unitConverter = (num) => {
  if (isNaN(num)) {
    return "请传入数值格式的数据";
  }
  // 此处为防止字符串形式的数值进来，因为toFixed方法只能用于数值型数
  num = Number(num);
  if (Math.abs(num) > 1000) {
    return (num / 1000).toFixed(2) + "k";
  } else if (Math.abs(num) > 10000) {
    return (num / 10000).toFixed(2) + "w";
  } else {
    return num;
  }
};

export function getOrderStatusText(status) {
  switch (status) {
    case 0:
      return "待支付";
    case 1:
      return "已支付";
    case 2:
      return "已取消";
  }
}

// 更具数组对象指定属性去重
export function arrByKeyUnique(arr, u_key) {
  let map = new Map();
  arr.forEach((item) => {
    if (!map.has(item[u_key])) {
      map.set(item[u_key], item);
    }
  });
  return [...map.values()];
}

/** 将数组进行分页，返回新的分页数组
 * @param {Object} pageSize 每页大小
 * @param {Object} arr 数组
 */
export function returnAllPageFunc(pageSize, arr) {
  let pageNum = 1;
  let pageObj = {
    pageNum: 1,
    list: [],
  };
  let pageResult = [];

  let newArr = JSON.parse(JSON.stringify(arr));
  let totalPage = newArr.length ? Math.ceil(arr.length / pageSize) : 0; // 计算总页数

  for (let i = 1; i <= totalPage; i++) {
    if (totalPage == 1) {
      pageNum += 1;
      pageObj.list = newArr.splice(0, arr.length);
    } else if (i <= totalPage) {
      pageNum += 1;
      pageObj.list = newArr.splice(0, pageSize);
    } else {
      pageObj.list = newArr.splice(0, arr.length % pageSize);
    }
    pageResult.push(pageObj);
    pageObj = {
      pageNum: pageNum,
      list: [],
    };
  }
  // console.log(`分页：${JSON.stringify(pageResult)}`)
  return pageResult;
}

export function pagination(pageNo, pageSize, array) {
  var offset = (pageNo - 1) * pageSize;
  return offset + pageSize >= array.length
    ? array.slice(offset, array.length)
    : array.slice(offset, offset + pageSize);
}
// let arr = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22]
// returnAllPageFunc(10, arr)
/**
 * 生成一个从 start 到 end 的连续数组
 * @param {*} start
 * @param {*} end
 * @returns
 */
export function generateArray(start, end) {
  return Array.from(new Array(end + 1).keys()).slice(start);
}

//多条件过滤，array是传入的数据，filters是过滤条件
export function multiFilter(array, filters) {
  filters = removeEmpty(filters);
  // console.log("条件：", filters);
  const filterKeys = Object.keys(filters);
  return array.filter((item) => {
    return filterKeys.every((key) => {
      // console.log(filters[key].toString(), item[key].toString());
      if (!filters[key].length) return true;
      return !!~item[key].indexOf(filters[key]);
    });
  });
}
//去掉过滤条件值空的键
export function removeEmpty(obj) {
  let v;
  obj = JSON.parse(JSON.stringify(obj));
  for (let k in obj) {
    v = obj[k];
    if (v == "" || v == null) {
      delete obj[k];
    } else if (typeof v == "object") {
      this.removeEmpty(v);
    }
  }
  return obj;
}
