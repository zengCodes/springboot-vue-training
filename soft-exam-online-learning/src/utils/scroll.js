'use strict'
import IScroll from 'iscroll'

class Scroll extends IScroll {
  domPotision = []
  beforeNode = null

  constructor(node, options) {
    super(node, options)
  }

  /**
   * @description: 更新并保存数据
   * @param { nodes, dataList } params //传入要保存的节点和要保存的数据
   * @return {*}
   */
  saveNodes (params) {
    const {nodes, dataList } = params
    let result = []
    const previous = this.domPotision
    nodes.forEach((node, index) => {
      const top = node.offsetTop
      const data = dataList[index]
      const dataStr = JSON.stringify(data)

      let item = {
        top,
        node,
        read: false,
        data: dataStr 
      }

      // 是否是存在的数据
      const resultKey = this.isBing(dataStr)
      if(resultKey>-1){
        const { read: preRead = false } = previous[resultKey] || {}
        item.read = preRead
      }
            
      result.push(item)
    })
    this.domPotision = result

    // 找到最后一次保存的节点，并把节点之前的数据设置为已读
    if(!this.beforeNode) return
    const { data } = this.beforeNode
    const resultKey = this.isBing(data)
    this.read(resultKey)
  }

  get unread () {
    let result = 0
    this.domPotision.forEach(i => {
      const { read } = i
      if (!read) {
        result += 1
      }
    })
    return result
  }

  get isTop () {
    let top = false
    let { y } = this
    if (y === 0) top = true
    return top
  }

  isBing(flag){
    let result = -1
    this.domPotision.forEach((i, k) => {
      const {data} = i
      if(data===flag) return result = k
    })
    return result
  }

  read (index) {
    const key = index || this.scrollPositionDom()
    let bottom = this.isBottom

    this.domPotision.forEach((i, j) => {
      if (bottom || key > j) {
        i.read = true
      }
    })
  }

  get isBottom () {
    let result = false
    const { y, maxScrollY } = this
    result = Math.abs(y) >= Math.abs(maxScrollY)
    return result
  }

  /**
   * @description: 判断当前的滚动位置是处于哪个元素内
   * @param {*}
   * @return {*}
   */
  scrollPositionDom () {
    const { y } = this
    let currentTop = Math.abs(y)
    if (currentTop == 0) return 0
    const doms = this.domPotision
    let result = -1

    doms.forEach((i, j) => {
      const { top } = i
      if (result === -1 || currentTop >= top) {
        result = j + 1
      }
    })

    return result
  }

  savePosition () {
    const nodeIndex = this.scrollPositionDom()
    this.beforeNode = this.domPotision[nodeIndex]
    this.read()
  }
}

export default Scroll
