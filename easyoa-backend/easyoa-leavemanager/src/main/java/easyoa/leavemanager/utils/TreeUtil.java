package easyoa.leavemanager.utils;

import easyoa.common.constant.AppConstant;
import easyoa.core.model.RouterMeta;
import easyoa.core.model.TreeNode;
import easyoa.core.model.VueRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by claire on 2019-06-26 - 13:46
 **/
public class TreeUtil {

    /**
     * 根据所有的节点，整理出一棵树
     *
     * @param nodes
     * @param <T>
     * @return
     */
    public static <T> TreeNode<T> buildTree(List<TreeNode<T>> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return null;
        }
        List<TreeNode<T>> root = new ArrayList<>();
        nodes.forEach(node -> {
            String parentId = node.getParentId();
            if (null == parentId || AppConstant.ROOT_TREE_ID.equals(parentId)) {
                root.add(node);
                return;
            }
            for (TreeNode<T> n : nodes) {
                String id = n.getId();
                if (null != id && id.equals(parentId)) {
                    if (n.getChildren() == null) {
                        n.initChildren();
                    }
                    n.getChildren().add(node);
                    n.setHasChildren(true);
                    n.setHasParent(true);
                    node.setHasParent(true);
                    return;
                }
            }
            if (root.isEmpty()) {
                root.add(node);
            }
        });

        TreeNode<T> preRoot = new TreeNode<>();
        preRoot.setId("0");
        preRoot.setParentId("");
        preRoot.setHasParent(false);
        preRoot.setHasChildren(true);
        preRoot.setChildren(root);
        preRoot.setText("root");
        return preRoot;
    }


    /**
     * 构造前端路由
     *
     * @param routes routes
     * @param <T>    T
     * @return ArrayList<VueRouter < T>>
     */
    public static <T> ArrayList<VueRouter<T>> buildVueRouter(List<VueRouter<T>> routes) {
        if (routes == null) {
            return null;
        }
        List<VueRouter<T>> topRoutes = new ArrayList<>();
        VueRouter<T> router = new VueRouter<>();
        router.setName("系统主页");
        router.setPath("/home");
        router.setComponent("HomePageView");
        router.setIcon("home");
        router.setChildren(null);
        router.setMeta(new RouterMeta(false, true));
        topRoutes.add(router);

        routes.forEach(route -> {
            String parentId = route.getParentId();
            if (parentId == null || AppConstant.ROOT_TREE_ID.equals(parentId)) {
                topRoutes.add(route);
                return;
            }
            for (VueRouter<T> parent : routes) {
                String id = parent.getId();
                if (id != null && id.equals(parentId)) {
                    if (parent.getChildren() == null)
                        parent.initChildren();
                    parent.getChildren().add(route);
                    parent.setHasChildren(true);
                    route.setHasParent(true);
                    parent.setHasParent(true);
                    return;
                }
            }
        });
        router = new VueRouter<>();
        router.setPath("/profile");
        router.setName("个人中心");
        router.setComponent("personal/Profile");
        router.setIcon("none");
        router.setMeta(new RouterMeta(true, false));
        topRoutes.add(router);

        ArrayList<VueRouter<T>> list = new ArrayList<>();
        VueRouter<T> root = new VueRouter<>();
        root.setName("主页");
        root.setComponent("MenuView");
        root.setIcon("none");
        root.setPath("/");
        root.setRedirect("/home");
        root.setChildren(topRoutes);
        list.add(root);

        root = new VueRouter<>();
        root.setName("404");
        root.setComponent("error/404");
        root.setPath("*");
        list.add(root);

        return list;
    }

}
