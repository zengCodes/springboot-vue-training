package com.zeng.business.converter;

import com.zeng.business.dto.PaperDTO;
import com.zeng.business.entity.Category;
import com.zeng.business.entity.Course;
import com.zeng.business.entity.Paper;
import com.zeng.business.mapper.CategoryMapper;
import com.zeng.business.mapper.CourseMapper;
import com.zeng.business.mapper.PaperMapper;
import com.zeng.business.vo.PaperVO;
import com.zeng.common.enums.CommonStatusEnum;
import com.zeng.common.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/17 14:29
 **/
@Component
@Data
public class PaperConverter {


    private PaperConverter paperConverter;

    private static PaperMapper paperMapper;

    private static CategoryMapper categoryMapper;

    private static CourseMapper courseMapper;


    @Autowired
    public void setPaperMapper(PaperMapper paperMapper) {
        paperConverter.paperMapper = paperMapper;
    }

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        paperConverter.categoryMapper = categoryMapper;
    }


    @Autowired
    public void setCourseMapper(CourseMapper courseMapper){
        paperConverter.courseMapper = courseMapper;
    }

    /**
     * 转voList
     *
     * @param papers
     * @return
     */
    public static List<PaperVO> converterToVOList(List<Paper> papers) {
        List<PaperVO> paperVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(papers)) {
            for (Paper paper : papers) {
                PaperVO paperVO = converterToPaperVO(paper);
                BeanUtils.copyProperties(paper, paperVO);
                paperVOS.add(paperVO);

            }
        }
        return paperVOS;
    }


    /***
     * 转VO
     * @param paper
     * @return
     */
    public static PaperVO converterToPaperVO(Paper paper) {
        PaperVO paperVO = new PaperVO();
        BeanUtils.copyProperties(paper, paperVO);
        paperVO.setStatus(paper.getStatus() == CommonStatusEnum.AVAILABLE.getStatusCode());
        // 获取级别名称
        Long id = paper.getCourseId();
        Course course = courseMapper.selectByPrimaryKey(id);
        Long level = course.getOneCategoryId();
        Category category = categoryMapper.selectByPrimaryKey(level);
        paperVO.setLevel(category.getName());
        return paperVO;
    }

}
