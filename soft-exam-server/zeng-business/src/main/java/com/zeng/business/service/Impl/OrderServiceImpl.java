package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.LessonConverter;
import com.zeng.business.converter.OrderConverter;
import com.zeng.business.dto.OrderDTO;
import com.zeng.business.entity.*;
import com.zeng.business.mapper.LessonLecturerMapper;
import com.zeng.business.mapper.LessonMapper;
import com.zeng.business.mapper.OrderMapper;
import com.zeng.business.mapper.PayLogMapper;
import com.zeng.business.service.OrderService;
import com.zeng.business.utils.OrderNoUtil;
import com.zeng.business.vo.*;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/29 15:13
 **/
@Service("OrderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private LessonMapper lessonMapper;

    @Resource
    private LessonLecturerMapper lessonLecturerMapper;

    @Resource
    private PayLogMapper payLogMapper;

    @Override
    public String createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        // 查询用户
        SysUser sysUser = sysUserMapper.selectUserById(orderDTO.getUser());
        // 查询课程
        Lesson lesson = lessonMapper.selectByPrimaryKey(orderDTO.getLesson());
        // 查询讲师
        LessonLecturer lessonLecturer = lessonLecturerMapper.selectByPrimaryKey(lesson.getUserNo());
        SysUser s = sysUserMapper.selectUserById(lessonLecturer.getLecturerUserNo());
        // 创建订单
        String orderNo = OrderNoUtil.getOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(orderDTO.getUser());
        order.setAccount(SecurityUtils.getUsername());
        order.setCourseId(orderDTO.getLesson());
        order.setNickname(sysUser.getNickName());
        order.setTeacherName(s.getNickName());
        order.setMobile(orderDTO.getPhone());
        order.setCourseTitle(lesson.getName());
        order.setCourseCover(lesson.getLogo());
        order.setTotalFee(orderDTO.getPay());
        order.setIsDeleted(CommonStatusEnum.DISABLE.getStatusCode());
        order.setCreateTime(new Date());
        order.setStatus(CommonStatusEnum.DISABLE.getStatusCode());
        orderMapper.insertSelective(order);
        return orderNo;
    }


    /**
     * 获取订单信息   判断订单是否支付
     *
     * @param id
     * @return
     */
    @Override
    public OrderDetailVO getOrderInfo(Long id) {
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        List<OperateVO> operateVOS = new ArrayList<>();
        PayLog payLog = new PayLog();
        OperateVO operateVO2 = new OperateVO();

        // 查询订单信息
        Order order = orderMapper.selectByPrimaryKey(id);
        // 查询用户信息
        SysUser sysUser = sysUserMapper.selectUserById(order.getUserId());
        // 查询课程信息
        Lesson lesson = lessonMapper.selectByPrimaryKey(order.getCourseId());

        OrderVO orderVO = OrderConverter.converterToOrderVO(order);
        // 操作信息
        OperateVO operateVO1 = new OperateVO();
        operateVO1.setTime(order.getCreateTime());
        operateVO1.setInfo("订单生成时间");
        operateVOS.add(operateVO1);
        // 如果订单已经支付 查询订单日志
        if (order.equals(CommonStatusEnum.AVAILABLE.getStatusCode())) {
            payLog = payLogMapper.selectOne(new PayLog().setOrderNo(order.getOrderNo()));
            operateVO2.setTime(payLog.getCreateTime());
        } else {
            operateVO2.setTime(order.getModifiedTime());
        }
        if (order.getStatus().toString().equals('1')) {
            operateVO2.setInfo("用户付款成功");
            orderVO.setPayTime(order.getModifiedTime());
            orderVO.setType(payLog.getPayType());
        } else if (order.getStatus().toString().equals("2")) {
            operateVO2.setInfo("用户退款");
            orderVO.setPayTime(order.getModifiedTime());
        }
        orderDetailVO.setOrderVO(orderVO);
        orderDetailVO.setLessonVO(LessonConverter.converterToLessonVO(lesson));
        orderDetailVO.setSysUser(sysUser);
        operateVOS.add(operateVO2);
        orderDetailVO.setOperate(operateVOS);
        return orderDetailVO;
    }

    @Override
    public boolean getOrderStatus(Long courseId, Long memberId) {
        Order o = new Order();
        o.setCourseId(courseId);
        o.setUserId(memberId);
        o.setStatus(CommonStatusEnum.AVAILABLE.getStatusCode());
        int count = orderMapper.selectCount(o);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PageVO<OrderVO> getOrderList(Integer pageNum, Integer pageSize, OrderVO orderVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderVO> orderVOS = new ArrayList<>();
        Example o = new Example(Order.class);
        if (StringUtils.isNotNull(orderVO.getOrderNo())) {
            o.createCriteria().andEqualTo("orderNum", orderVO.getOrderNo());
        }
        if (StringUtils.isNotNull(orderVO.getAccount())) {
            o.createCriteria().andEqualTo("account", orderVO.getAccount());
        }
        if (StringUtils.isNotNull(orderVO.getMobile())) {
            o.createCriteria().andEqualTo("mobile", orderVO.getMobile());
        }
        List<Order> orderList = orderMapper.selectByExample(o);
        orderVOS = OrderConverter.converterToVOList(orderList);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        return new PageVO<>(pageInfo.getTotal(), orderVOS);
    }

    @Override
    public int delete(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        order.setModifiedTime(new Date());
        order.setIsDeleted(CommonStatusEnum.AVAILABLE.getStatusCode());
        return orderMapper.updateByPrimaryKey(order);
    }

    @Override
    public OrderVO getOrderInfoByNo(String orderNo) {
        Order order = orderMapper.selectOne(new Order().setOrderNo(orderNo));
        return OrderConverter.converterToOrderVO(order);
    }
}
