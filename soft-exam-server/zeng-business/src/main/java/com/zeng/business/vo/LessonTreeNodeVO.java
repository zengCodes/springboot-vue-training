package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zeng.business.entity.Lesson;
import lombok.Data;

import javax.persistence.Id;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/10 0:58
 **/
@Data
public class LessonTreeNodeVO {

    @Id
    private Long id;

    /**
     * 课程分类/课程名称
     */
    private String name;

    /**
     * 课程排序
     */
    private Integer sort;
    /**
     * 父类id
     */
    private Long typeId;

    private List<Lesson> children;

    /*
     * 排序,根据order排序
     */
    public static Comparator<LessonTreeNodeVO> order() {
        Comparator<LessonTreeNodeVO> comparator = (o1, o2) -> {
            if (o1.getSort() != o2.getSort()) {
                return (int) (o1.getSort() - o2.getSort());
            }
            return 0;
        };
        return comparator;
    }


}
