package com.zeng.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/7 23:22
 **/
@Data
public class LessonVO {

    @Id
    private Long id;

    private Long type;

    private Long userNo;

    private Integer status;

    private Integer sort;
    /**
     * 讲师名称
     */
    private String nickName;

    /**
     * 课程类型名称
     */
    private String typeName;

    /**
     * 课程名称
     */
    private String name;

    private String logo;

    private String introduce;

    private Integer isFree;

    private BigDecimal discount;

    private BigDecimal original;

    private Double discountNum;

    private Double total;

    private Integer buyCount;

    private Integer studyCount;

    private Boolean isPutAway;

    private Integer periodTotal;

    private Integer approvalStatus;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date modifiedTime;

    /**
     * 授课时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date teachTime;
    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
    private Date endTime;

}
