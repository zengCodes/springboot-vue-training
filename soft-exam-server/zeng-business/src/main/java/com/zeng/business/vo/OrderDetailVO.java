package com.zeng.business.vo;

import com.zeng.common.core.domain.entity.SysUser;
import lombok.Data;

import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/1/8 13:21
 **/
@Data
public class OrderDetailVO {

    private OrderVO orderVO;

    private LessonVO lessonVO;

    private SysUser sysUser;

    private List<OperateVO> operate;

}
