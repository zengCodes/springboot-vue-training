package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/5/31 13:18
 **/
@Data
@Accessors(chain = true)
public class CategoryTreeNodeVO {

    @Id
    private Long id;

    private String name;

    private String remark;

    private Integer sort;

    private String type;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;

    private Long parentId;

    private int lev;

    private List<CategoryTreeNodeVO> children;

    /*
     * 排序,根据order排序
     */
    public static Comparator<CategoryTreeNodeVO> order() {
        Comparator<CategoryTreeNodeVO> comparator = (o1, o2) -> {
            if (o1.getSort() != o2.getSort()) {
                return (int) (o1.getSort() - o2.getSort());
            }
            return 0;
        };
        return comparator;
    }
}
