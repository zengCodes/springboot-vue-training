package com.zeng.business.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Table;

/**
 * (ExamAnswerQuestion)实体类
 *
 * @author zenghuiqing
 * @since 2021-06-12 15:21:55
 */
@Data
@Table(name = "exam_answer_question")
@EqualsAndHashCode(callSuper = true)
public class AnswerQuestion extends Question {

}
