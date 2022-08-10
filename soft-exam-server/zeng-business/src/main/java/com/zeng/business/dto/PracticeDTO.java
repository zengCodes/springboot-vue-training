package com.zeng.business.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/17 23:49
 **/
@Data
@ApiModel(value = "试题提交对象")
public class PracticeDTO {

    private Long id;

    private Long testId;

    private Long type;

    private Integer score;

    private String correctAnswer;

    private String userResponse;

}
