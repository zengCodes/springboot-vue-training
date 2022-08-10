package com.zeng.business.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description 试卷题目
 * @Date create in 2021/7/25 0:03
 **/
@Data
public class PaperQuestionVO {

    private String name;
    private String time;
    private String year;
    private String count;
    private List<Object> children = new ArrayList<>();
}
