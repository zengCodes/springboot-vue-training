package com.zeng.business.dto;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2022/5/10 19:35
 **/
@Data
public class SendTestDTO {

    Long id;

    ArrayList<TestDTO> testDTO;
}
