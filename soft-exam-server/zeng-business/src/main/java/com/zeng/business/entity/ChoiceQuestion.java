package com.zeng.business.entity;

import com.zeng.business.vo.QuestionVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * (ChoiceQuestion)实体类
 *
 * @author zenghuiiqng
 * @since 2021-04-13 22:09:28
 */
@Data
@Table(name = "exam_choice_question")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ChoiceQuestion extends Question {
    /**
     * 选项A
     */
    private String selectA;
    /**
     * 选项B
     */
    private String selectB;
    /**
     * 选项c
     */
    private String selectC;
    /**
     * 选项D
     */
    private String selectD;

}
