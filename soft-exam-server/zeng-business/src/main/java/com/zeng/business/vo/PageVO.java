package com.zeng.business.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zenghuiqing
 * @since 2021-04-20 18:57:11
 */
@Data
public class PageVO<T> {
    private long total;

    private List<T> rows = new ArrayList<>();

    public PageVO(long total, List<T> data) {
        this.total = total;
        this.rows = data;
    }
}
