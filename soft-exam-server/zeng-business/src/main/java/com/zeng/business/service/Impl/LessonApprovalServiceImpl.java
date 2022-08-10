package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.LecturerApprovalConverter;
import com.zeng.business.converter.LessonApprovalConverter;
import com.zeng.business.converter.LessonLecturerConverter;
import com.zeng.business.dto.ApprovalDTO;
import com.zeng.business.entity.LecturerApproval;
import com.zeng.business.entity.Lesson;
import com.zeng.business.entity.LessonApproval;
import com.zeng.business.entity.LessonLecturer;
import com.zeng.business.mapper.LessonApprovalMapper;
import com.zeng.business.mapper.LessonLecturerMapper;
import com.zeng.business.service.LessonApprovalService;
import com.zeng.business.vo.LecturerApprovalVO;
import com.zeng.business.vo.LessonApprovalVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/9 17:47
 **/
@Service("LessonApprovalService")
public class LessonApprovalServiceImpl implements LessonApprovalService {

    @Resource
    private LessonApprovalMapper lessonApprovalMapper;


    @Override
    public int updateApprovalStatus(Long id, ApprovalDTO approvalDTO) {
        LessonApproval lecturerApproval = lessonApprovalMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(lecturerApproval)) {
            throw new CustomException("要审核的课程不存在");
        } else {
            lecturerApproval.setApprovalBy(SecurityUtils.getUserId())
                    .setApprovalStatus(approvalDTO.getApprovalStatus())
                    .setCommon(approvalDTO.getComment())
                    .setModifiedTime(new Date());
            int n = lessonApprovalMapper.updateByPrimaryKey(lecturerApproval);
            return n;
        }
    }

    @Override
    public PageVO<LessonApprovalVO> getLessonApprovalList(Integer pageNum, Integer pageSize, LessonApprovalVO lecturerApproval) {
        PageHelper.startPage(pageNum, pageSize);
        Integer status = lecturerApproval.getApprovalStatus();
        List<LessonApproval> lessonApprovals;
        Example o = new Example(LessonApproval.class);
        Example.Criteria criteria = o.createCriteria();
        if (StringUtils.isNotNull(status)) {
            criteria.andEqualTo("approvalStatus", status);
        }
        lessonApprovals = lessonApprovalMapper.selectByExample(o);
        List<LessonApprovalVO> lessonApprovalVOList = LessonApprovalConverter.converterToVOList(lessonApprovals);
        PageInfo<LessonApproval> info = new PageInfo<>(lessonApprovals);
        return new PageVO<>(info.getTotal(), lessonApprovalVOList);
    }

    @Override
    public int delete(Long id) {
        LessonApproval lessonApproval = lessonApprovalMapper.selectByPrimaryKey(id);
        if (StringUtils.isNull(lessonApproval)) {
            throw new CustomException("审核记录不存在！");
        } else {
            return lessonApprovalMapper.deleteByPrimaryKey(id);
        }
    }


}
