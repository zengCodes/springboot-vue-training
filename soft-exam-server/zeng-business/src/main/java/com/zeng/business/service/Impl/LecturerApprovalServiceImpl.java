package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.LecturerApprovalConverter;
import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.entity.LecturerApproval;
import com.zeng.business.entity.LessonLecturer;
import com.zeng.business.mapper.LecturerApprovalMapper;
import com.zeng.business.service.LecturerApprovalService;
import com.zeng.business.vo.LecturerApprovalVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import com.zeng.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/6 18:22
 **/
@Service("LecturerApprovalService")
public class LecturerApprovalServiceImpl implements LecturerApprovalService {

    @Resource
    private LecturerApprovalMapper lecturerApprovalMapper;

    @Override
    public PageVO<LecturerApprovalVO> getLecturerApprovalList(Integer pageNum, Integer pageSize, LecturerApprovalVO lecturerApprovalVO) {
        PageHelper.startPage(pageNum, pageSize);
        Integer status = lecturerApprovalVO.getApprovalStatus();
        String lecturerName = lecturerApprovalVO.getLecturerName();
        List<LecturerApproval> lecturerApprovalVOS;
        Example o = new Example(LecturerApproval.class);
        Example.Criteria criteria = o.createCriteria();
        if (StringUtils.isNotEmpty(lecturerName)) {
            criteria.andEqualTo("lecturerName", lecturerName);
        }
        if (StringUtils.isNotNull(status)) {
            criteria.andEqualTo("approvalStatus", status);
        }
        lecturerApprovalVOS = lecturerApprovalMapper.selectByExample(o);
        List<LecturerApprovalVO> lecturerApprovals = LecturerApprovalConverter.converterToVOList(lecturerApprovalVOS);
        PageInfo<LecturerApprovalVO> info = new PageInfo<>(lecturerApprovals);
        return new PageVO<>(info.getTotal(), lecturerApprovals);
    }


    @Override
    public int updateApprovalStatus(Long id, ApprovalDTO approvalDTO) {
        LecturerApproval lecturerApproval = lecturerApprovalMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(lecturerApproval)) {
            throw new CustomException("要审核的讲师不存在");
        } else {
            lecturerApproval.setApprovalStatus(approvalDTO.getApprovalStatus())
                    .setCommon(approvalDTO.getComment())
                    .setApprovalTime(new Date())
                    .setApprovalBy(SecurityUtils.getUserId());
            int n = lecturerApprovalMapper.updateByPrimaryKey(lecturerApproval);
            return n;
        }
    }

}
