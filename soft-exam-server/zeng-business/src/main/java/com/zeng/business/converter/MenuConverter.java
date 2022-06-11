package com.zeng.business.converter;


import com.zeng.common.vo.MenuNodeVO;
import com.zeng.common.core.domain.entity.SysMenu;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/4/20 18:08
 **/
public class MenuConverter {

    /**
     * 转成menuVO(只包含菜单)List
     *
     * @param menus
     * @return
     */
    public static List<SysMenu> converterToMenuNodeVO(List<SysMenu> menus) {
        //先过滤出用户的菜单
        if (!CollectionUtils.isEmpty(menus)) {
            for (SysMenu menu : menus) {
                if (menu.getMenuType() == "M") {
                    menu.setDisabled(menu.getVisible() == "0");
                    menus.add(menu);
                }
            }
        }
        return menus;
    }


    /**
     * 转成menuVO(菜单和按钮）
     *
     * @param menus
     * @return
     */
    public static List<MenuNodeVO> converterToALLMenuNodeVO(List<SysMenu> menus) {
        //先过滤出用户的菜单
        List<MenuNodeVO> menuNodeVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(menus)) {
            for (SysMenu menu : menus) {
                MenuNodeVO menuNodeVO = new MenuNodeVO();
                BeanUtils.copyProperties(menu, menuNodeVO);
                menuNodeVO.setDisabled(menu.getVisible() == "0");
                menuNodeVO.setChildren(menu.getChildren());
                menuNodeVOS.add(menuNodeVO);
            }
        }
        return menuNodeVOS;
    }
}
