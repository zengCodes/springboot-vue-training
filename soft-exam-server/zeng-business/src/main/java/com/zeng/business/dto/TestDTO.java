package com.zeng.business.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/19 15:05
 **/
@Data
@ApiModel(value = "试卷提交对象")
public class TestDTO {

    private List<PracticeDTO> data;

    private Integer type;

    private Long testId;
}
