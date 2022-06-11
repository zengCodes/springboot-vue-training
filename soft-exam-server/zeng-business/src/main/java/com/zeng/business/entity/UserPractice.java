package com.zeng.business.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * (UserPractice)实体类
 *
 * @author zenghuiqing
 * @since 2021-11-16 14:08:24
 */

@Data
@Table(name = "exam_user_practice")
@Accessors(chain = true)
public class UserPractice implements Serializable {
    private static final long serialVersionUID = -26005835322687358L;
    /**
     * 自增id
     */
    @Id
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 考试id
     */
    private Long testId;
    /**
     * 试题id
     */
    private Long questionId;
    /**
     * 试题类型 1:选择题  2:简答题
     */
    private Long questionType;

    /**
     * 回答情况
     */
    private String userResponse;

}
