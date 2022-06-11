package com.zeng.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/11/18 13:42
 **/
@Slf4j
public class PaperUtil {


    /**
     * 返回选项内容
     *
     * @param paragraph
     * @return
     */
    public static String getGoalOption(String paragraph) {
        String[] optionList = paragraph.split("[.]");
        String optionRegex = "[A-D][A-Da-d]*";
        String goalStr = "";
        for (String item : optionList) {
            if (!item.matches(optionRegex)) {
                goalStr = item;
            }
        }
        return goalStr;
    }


    /**
     * 年度-->时间
     *
     * @param time
     * @return
     */
    public static String getYear(String time) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int index = time.indexOf("年");
        String year = time.substring(0, index);
        String nd = time.substring(index);
        if (nd.equals("上半年")) {
            calendar.set(Integer.valueOf(year), 4, 29);
        } else {
            calendar.set(Integer.valueOf(year), 10, 7);
        }
        return dateFormat.format(calendar.getTime());
    }

}
