package com.zeng.business.utils;


import com.zeng.business.vo.CategoryTreeNodeVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhangyukang on 2020/2/6 15:34
 */
public class CategoryTreeBuilder {

    private static int count = 1;

    /**
     * 构建多级
     *
     * @param nodes
     * @return
     */
    public static List<CategoryTreeNodeVO> build(List<CategoryTreeNodeVO> nodes) {
        //根节点
        List<CategoryTreeNodeVO> rootMenu = new ArrayList<>();
        for (CategoryTreeNodeVO nav : nodes) {
            if (nav.getParentId() == 0) {
                nav.setLev(1);
                rootMenu.add(nav);
            }
        }
        /* 根据Menu类的order排序 */
        Collections.sort(rootMenu, CategoryTreeNodeVO.order());
        /*为根菜单设置子菜单，getChild是递归调用的*/
        for (CategoryTreeNodeVO nav : rootMenu) {
            /* 获取根节点下的所有子节点 使用getChild方法*/
            List<CategoryTreeNodeVO> childList = getChild(nav, nodes);
            nav.setChildren(childList);//给根节点设置子节点
        }
        return rootMenu;
    }

    /**
     * 获取子菜单
     *
     * @param pNode
     * @param nodes
     * @return
     */
    private static List<CategoryTreeNodeVO> getChild(CategoryTreeNodeVO pNode, List<CategoryTreeNodeVO> nodes) {
        //子菜单
        List<CategoryTreeNodeVO> childList = new ArrayList<>();
        for (CategoryTreeNodeVO nav : nodes) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            //相等说明：为该根节点的子节点。
            if (nav.getParentId().equals(pNode.getId())) {
                nav.setLev(pNode.getLev() + 1);
                childList.add(nav);
            }
        }
        //递归
        for (CategoryTreeNodeVO nav : childList) {
            nav.setChildren(getChild(nav, nodes));
        }
        Collections.sort(childList, CategoryTreeNodeVO.order());//排序
        //如果节点下没有子节点，返回一个空List（递归退出）
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

//    获取二级父级分类

    public static List<CategoryTreeNodeVO> buildParent(List<CategoryTreeNodeVO> nodes) {
        //根节点
        List<CategoryTreeNodeVO> rootMenu = new ArrayList<>();
        for (CategoryTreeNodeVO nav : nodes) {
            if (nav.getParentId() == 0) {
                nav.setLev(1);
                rootMenu.add(nav);
            }
        }
        /* 根据Menu类的order排序 */
        Collections.sort(rootMenu, CategoryTreeNodeVO.order());
        /*为根菜单设置子菜单，getChild是递归调用的*/
        for (CategoryTreeNodeVO nav : rootMenu) {
            /* 获取根节点下的所有子节点 使用getChild方法*/
            List<CategoryTreeNodeVO> childList = getParentChild(nav, nodes);
            nav.setChildren(childList);//给根节点设置子节点
        }
        return rootMenu;
    }

    private static List<CategoryTreeNodeVO> getParentChild(CategoryTreeNodeVO pNode, List<CategoryTreeNodeVO> nodes) {
        //子菜单
        List<CategoryTreeNodeVO> childList = new ArrayList<>();
        for (CategoryTreeNodeVO nav : nodes) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            //相等说明：为该根节点的子节点。
            if (nav.getParentId().equals(pNode.getId())) {
                nav.setLev(2);
                childList.add(nav);
            }
        }
        return childList;
    }


}
