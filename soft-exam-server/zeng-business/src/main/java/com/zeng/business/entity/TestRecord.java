package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/19 15:26
 **/
@Data
@Table(name = "exam_test_record")
@Accessors(chain = true)
public class TestRecord implements Serializable {
    private static final long serialVersionUID = -96413333817648131L;
    /**
     * 自增id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
    /**
     * 考试用户
     */
    private Long userId;
    /**
     * 试卷id
     */
    private Long paperId;
    /**
     * 真实姓名
     */
    private String nickName;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 类型
     * 0：练习
     * 1：考试
     */
    private Integer type;
    /**
     * 练习次数
     */
    private Integer num;

    private Integer total;
    /**
     * 考试时间
     */
    private Date createTime;
}
