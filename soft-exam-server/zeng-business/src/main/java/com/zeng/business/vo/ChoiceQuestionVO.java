package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/6/13 1:26
 **/
@Data
public class ChoiceQuestionVO {

    @Id
    private Long id;

    @NotNull(message = "试题题干不能为空")
    private String name;

    private Long typeId;

    private String selectA;

    private String selectB;

    private String selectC;

    private String selectD;

    private String correctAnswer;

    private Integer score;

    @NotNull(message = "所属试卷不能为空")
    private Long paperId;

    private Integer sort;

    private Integer rate;

    private String remark;

    private String paperName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date modifiedTime;

}
