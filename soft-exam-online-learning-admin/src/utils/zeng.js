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
  return [{
      label: "启用",
      value: 1,
    },
    {
      label: "禁用",
      value: 0,
    },
  ];
}

export function getApprovalState() {
  return [{
      label: "已通过",
      value: 1,
    },
    {
      label: "不通过",
      value: 2,
    },
    {
      label: "待审核",
      value: 0,
    },
  ];
}

export function getAlbumsType() {
  return [{
      typeId: 1,
      typeName: "轮播展示",
    },
    {
      typeId: 2,
      typeName: "其他",
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

export function getCategoryType() {
  return [{
      typeId: "course",
      typeName: "课程",
    },
    {
      typeId: "album",
      typeName: "相册",
    },
  ];
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
export function downloadResource(id) {
  var elemIF = document.createElement("iframe");
  elemIF.src =
    process.env.VUE_APP_BASE_API +
    "/common/download?id=" + id;
  elemIF.style.display = "none";
  document.body.appendChild(elemIF);
}

export function getOrderStatusText(status) {
  switch (status) {
    case 0:
      return "待付款";
    case 1:
      return "已支付";
    case 2:
      return "已取消";
  }
}


export function awaitWrapper(promise) {
  return promise.then((res) => [null, res]).catch((err) => [err, null]);
}


/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export function handleTree(data, id, parentId, children) {
  console.log(data, id, parentId, children);
  let config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  };

  var childrenListMap = {};
  var nodeIds = {};
  var tree = [];

  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (let t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o) {
    console.log(o);
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}



export function handleArrayTree(arr=[]) {
  let data = arr.map(item => {
    item.id?item.id:item.id= item.menuId
    item.value?item.value:item.value = item.menuId
    item.label?item.label:item.label = item.menuName
    if (item.children.length > 0) {
      handleArrayTree(item.children)
    } 
    return item
  })
  return data
}