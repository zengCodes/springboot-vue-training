package com.zeng.business.entity;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/4/6 21:11
 **/
@Data
public class Question implements Serializable {
    private static final long serialVersionUID = 799895680052560748L;
    /**
     * 选择题编号
     */
    @Id
    private Long id;
    /**
     * 题目描述
     */
    private String name;
    /**
     * 试题类型
     */
    private Long typeId;
    /**
     * 正确答案
     */
    private String correctAnswer;
    /**
     * 分数
     */
    private Integer score;
    /**
     * 所属试卷编号
     */
    private Long paperId;
    /**
     * 解析备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 难度系数 数字类型:数值越大越难
     */
    private Integer rate;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifiedTime;


}
