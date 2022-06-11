package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/25 16:49
 **/
@Data
public class LessonTreeNode {

    @Id
    private Long id;

    /**
     * 章节标题名称
     */
    private String title;

    /**
     * 课程排序
     */
    private Integer sort;
    /**
     * 父类课程id
     */
    private Long parentId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date modifiedTime;

    private List<LessonTreeNode> children;

    /*
     * 排序,根据order排序
     */
    public static Comparator<LessonTreeNode> order() {
        Comparator<LessonTreeNode> comparator = (o1, o2) -> {
            if (o1.getSort() != o2.getSort()) {
                return (int) (o1.getSort() - o2.getSort());
            }
            return 0;
        };
        return comparator;
    }

}
