package com.zeng.business.utils;


import com.zeng.common.core.domain.entity.SysMenu;
import com.zeng.common.vo.MenuNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 该类用于递归构建树形菜单
 * Created by zenghuiqing on 2021/4/16 15:34
 */
@Slf4j
public class MenuTreeBuilder {


    /**
     * 构建多级菜单树
     *
     * @param nodes
     * @return
     */
    public static List<MenuNodeVO> build(List<MenuNodeVO> nodes) {
        //根节点
        List<MenuNodeVO> rootMenu = new ArrayList<>();
        for (MenuNodeVO nav : nodes) {
            if (nav.getParentId() == 0) {
                rootMenu.add(nav);
            }
        }
        /* 根据Menu类的order排序 */
        Collections.sort(rootMenu, MenuNodeVO.order());
        /*为根菜单设置子菜单，getChild是递归调用的*/
        for (MenuNodeVO nav : rootMenu) {
//            log.info("rootMenu--"+rootMenu);
            /* 获取根节点下的所有子节点 使用getChild方法*/
            List<SysMenu> childList = getChild(nav.getMenuId(), nodes);
            nav.setChildren(childList);//给根节点设置子节点
        }
        return rootMenu;
    }

    /**
     * 获取子菜单
     *
     * @param id
     * @param nodes
     * @return
     */
    private static List<SysMenu> getChild(Long id, List<MenuNodeVO> nodes) {
        //子菜单
        List<SysMenu> childList = new ArrayList<>();
        for (MenuNodeVO nav : nodes) {
            SysMenu sysMenu = new SysMenu();
            BeanUtils.copyProperties(nav, sysMenu);
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            //相等说明：为该根节点的子节点。
            if (nav.getParentId().equals(id)) {
                childList.add(sysMenu);
            }
        }
        //递归
        for (SysMenu nav : childList) {
            nav.setChildren(getChild(nav.getMenuId(), nodes));
        }
        Collections.sort(childList, SysMenu.order());//排序
        //如果节点下没有子节点，返回一个空List（递归退出）
        if (childList.size() == 0) {
            return new ArrayList<SysMenu>();
        }
        return childList;
    }


}
