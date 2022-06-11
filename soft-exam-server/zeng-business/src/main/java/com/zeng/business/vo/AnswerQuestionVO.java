package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/24 0:27
 **/
@Data
public class AnswerQuestionVO {

    @Id
    private Long id;

    @NotNull(message = "试题题干不能为空")
    private String name;

    private Long typeId;

    private String correctAnswer;

    private Integer score;

    private Integer rate;

    @NotNull(message = "所属试卷不能为空")
    private Long paperId;

    private Long sort;

    private String remark;

    private String paperName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;
}
