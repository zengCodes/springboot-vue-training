/*
 * @Description: 
 * @Author: zenghuiqing
 * @Date: 2022-06-12 16:04:17
 */
export default function drag(el) {
    let dragBox = el; //获取当前元素
    dragBox.onmousedown = e => {
        //算出鼠标相对元素的位置
        let disX = e.clientX - dragBox.offsetLeft;
        let disY = e.clientY - dragBox.offsetTop;
        document.onmousemove = e => {
            //用鼠标的位置减去鼠标相对元素的位置，得到元素的位置
            let left = e.clientX - disX;
            let top = e.clientY - disY;
            //移动当前元素
            dragBox.style.left = left + "px";
            dragBox.style.top = top + "px";
        };
        document.onmouseup = e => {
            //鼠标弹起来的时候不再移动
            document.onmousemove = null;
            //预防鼠标弹起来后还会循环（即预防鼠标放上去的时候还会移动）
            document.onmouseup = null;
        };
    };
}