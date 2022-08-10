package com.zeng.common.vo;

import com.zeng.common.core.domain.entity.SysMenu;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author zenghuiqing
 * @since 2021-04-20 18:57:11
 */
@Data
public class MenuNodeVO {

    private Long menuId;

    private Long parentId;

    private String menuName;

    private String path;

    private String icon;

    private Long orderNum;

    private Integer open;

    private boolean disabled;

    private String perms;

    private String menuType;


    private List<SysMenu> children = new ArrayList<>();

    /*
     * 排序,根据order排序
     */
    public static Comparator<MenuNodeVO> order() {
        Comparator<MenuNodeVO> comparator = (o1, o2) -> {
            if (o1.getOrderNum() != o2.getOrderNum()) {
                return (int) (o1.getOrderNum() - o2.getOrderNum());
            }
            return 0;
        };
        return comparator;
    }

}
