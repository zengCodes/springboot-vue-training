package com.zeng.business.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeng.business.converter.LessonLecturerConverter;
import com.zeng.business.entity.LecturerApproval;
import com.zeng.business.entity.LessonLecturer;
import com.zeng.business.mapper.LecturerApprovalMapper;
import com.zeng.business.mapper.LessonLecturerMapper;
import com.zeng.business.service.LessonLecturerService;
import com.zeng.business.vo.LessonLecturerVO;
import com.zeng.business.vo.PageVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.exception.CustomException;
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
 * @Date create in 2021/11/25 23:03
 **/
@Service("LessonLecturerService")
public class LessonLecturerServiceImpl implements LessonLecturerService {


    @Resource
    private LessonLecturerMapper lessonLecturerDao;

    @Resource
    private LecturerApprovalMapper lecturerApprovalMapper;


    @Override
    public PageVO<LessonLecturerVO> findLessonLecturerList(Integer pageNum, Integer pageSize, LessonLecturerVO lessonLecturerVO) {
        PageHelper.startPage(pageNum, pageSize);
        Example o = new Example(LessonLecturer.class);
        Example.Criteria criteria = o.createCriteria();
        // 根据名称查询
        if (StringUtils.isNotEmpty(lessonLecturerVO.getLecturerName())) {
            criteria.andEqualTo("lecturerName", lessonLecturerVO.getLecturerName());
        }
        if (StringUtils.isNotNull(lessonLecturerVO.getLecturerUserNo())) {
            criteria.andEqualTo("lecturerUserNo", lessonLecturerVO.getLecturerUserNo());
        }
        // 邮箱查询
        if (StringUtils.isNotEmpty(lessonLecturerVO.getLecturerEmail())) {
            criteria.andEqualTo("lecturerEmail", lessonLecturerVO.getLecturerEmail());
        }
        // 状态查询
        if (StringUtils.isNotNull(lessonLecturerVO.getStatus())) {
            criteria.andEqualTo("status", lessonLecturerVO.getStatus());
        }
        List<LessonLecturer> lessonLecturers = lessonLecturerDao.selectByExample(o);
        List<LessonLecturerVO> lessonLecturerVOS = LessonLecturerConverter.converterToVOList(lessonLecturers);
        PageInfo<LessonLecturerVO> lessonLecturerPageInfo = new PageInfo<>(lessonLecturerVOS);
        return new PageVO<>(lessonLecturerPageInfo.getTotal(), lessonLecturerVOS);
    }


    /**
     * 添加讲师
     *
     * @param lessonLecturerVO
     */
    @Override
    public int add(LessonLecturerVO lessonLecturerVO) {
        LessonLecturer lessonLecturer = new LessonLecturer();
        BeanUtils.copyProperties(lessonLecturerVO, lessonLecturer);
        lessonLecturer.setStatus(CommonStatusEnum.DISABLE.getStatusCode())
                .setCreateTime(new Date());
        int n = lessonLecturerDao.insert(lessonLecturer);
        // 审核状态为待审核
        LecturerApproval lecturerApproval = new LecturerApproval()
                .setLecturer(lessonLecturer.getId())
                .setApprovalStatus(CommonStatusEnum.DISABLE.getStatusCode());
        lecturerApprovalMapper.insert(lecturerApproval);
        return n;
    }

    /**
     * 更新讲师
     *
     * @param id
     * @param lessonLecturerVO
     */
    @Override
    public int update(Long id, LessonLecturerVO lessonLecturerVO) {
        LessonLecturer lessonLecturer = new LessonLecturer();
        BeanUtils.copyProperties(lessonLecturerVO, lessonLecturer);
        if (lessonLecturerVO.getCoverImg().indexOf("dev-api") > 0) {
            lessonLecturer.setCoverImg(lessonLecturerVO.getCoverImg().replaceAll("/dev-api", ""));
        }
        lessonLecturer.setId(id)
                .setStatus(lessonLecturerVO.getStatus() ? CommonStatusEnum.AVAILABLE.getStatusCode() : CommonStatusEnum.DISABLE.getStatusCode())
                .setModifiedTime(new Date());
        int n = lessonLecturerDao.updateByPrimaryKey(lessonLecturer);
        return n;
    }


    /**
     * 更新禁用状态
     *
     * @param id
     * @param status
     */
    @Override
    public int updateStatus(Long id, Boolean status) {
        LessonLecturer lessonLecturer = lessonLecturerDao.selectByPrimaryKey(id);
        if (StringUtils.isNull(lessonLecturer)) {
            throw new CustomException("要更新状态的讲师不存在");
        } else {
            lessonLecturer.setStatus(status ? CommonStatusEnum.AVAILABLE.getStatusCode() :
                    CommonStatusEnum.DISABLE.getStatusCode());
            int n = lessonLecturerDao.updateByPrimaryKey(lessonLecturer);
            return n;
        }
    }

    /**
     * 删除讲师
     *
     * @param id
     */
    @Override
    public int delete(Long id) {
        LessonLecturer lessonLecturer = lessonLecturerDao.selectByPrimaryKey(id);
        if (lessonLecturer.getStatus() != 0) {
            throw new CustomException("讲师状态不可删");
        } else {
            return lessonLecturerDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public LessonLecturerVO edit(Long id) {
        LessonLecturer lessonLecturer = lessonLecturerDao.selectByPrimaryKey(id);
        return LessonLecturerConverter.converterToLessonLecturerVO(lessonLecturer);
    }
}
