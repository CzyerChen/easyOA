package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.service.redisson.RedissonService;
import easyoa.leavemanager.utils.TreeUtil;
import easyoa.common.domain.vo.MenuSearch;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.Menu;
import easyoa.core.model.RouterMeta;
import easyoa.core.model.TreeNode;
import easyoa.core.model.VueRouter;
import easyoa.core.repository.MenuRepository;
import easyoa.core.service.MenuService;
import easyoa.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-24 - 09:10
 **/
@Slf4j
@Service("menuService")
//spring的事务，不是hibernate的事务
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl implements MenuService {
    @Autowired
    private RedissonService redissonService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserService userService;

    private void initMenu(Menu menu) {
        //根节点父节点为0
        if (null == menu.getParentId()) {
            menu.setParentId(0);
        }
        //如果是按钮，没有path icon 和组件
        if (Menu.TYPE_BUTTON.equals(menu.getType())) {
            menu.setPath(null);
            menu.setIcon(null);
            menu.setComponent(null);
        }
        menu.setCreateTime(new Date());
        menu.setUpdateTime(new Date());
    }

    @Override
    @Transactional
    public void addMenu(Menu menu) {
        initMenu(menu);
        menuRepository.save(menu);
    }

    @Override
    @Transactional
    public void deleteMenus(String[] menuIds) {
        Set<Long> users = new HashSet<>();
        List<Integer> parentMenus = new ArrayList<>();
        for (String menuId : menuIds) {
            //查找菜单关联的用户
            List<Long> userIds = userService.getUserIdsForMenu(Integer.valueOf(menuId));
            users.addAll(userIds);
            parentMenus.add(Integer.valueOf(menuId));
        }

        //删除以此菜单为首的所有子菜单
        deleteAllChildrenMenu(parentMenus);

        //刷新缓存中用户权限和角色信息
        Map<String, Set<String>> userRoles = userService.getUserRoles(users);
        Map<String, Set<String>> userPermissions = userService.getUserPermissions(users);
        userService.refreshCacheForRoleAndPerms(userRoles, userPermissions);

    }


    /**
     * 根据主键查找菜单
     *
     * @param menuId
     * @return
     */
    public Menu findByMenuId(int menuId) {
        return menuRepository.findByMenuId(menuId);
    }


    /**
     * 根据id 查找子菜单
     *
     * @param menuIds
     * @return
     */
    public List<Menu> findByMenuIdIn(List<Integer> menuIds) {
        return menuRepository.findByMenuIdIn(menuIds);
    }

    /**
     * 删除所有子菜单
     *
     * @param menus
     */
    public void deleteAllChildrenMenu(List<Integer> menus) {
        List<Menu> allMenus = findAllMenuByParentIdWithIn(menus);
        menuRepository.deleteAll(allMenus);
    }

    @Override
    public void updateMenu(Menu menu) {
        //更新menu
        initMenu(menu);
        menu.setUpdateTime(new Date());
        menuRepository.save(menu);

        //找到所有用户
        List<Long> userIds = userService.getUserIdsForMenu(menu.getMenuId());
        //刷新权限
        Set<Long> userSet = new HashSet<>();
        userSet.addAll(userIds);
        Map<String, Set<String>> userRoles = userService.getUserRoles(userSet);
        Map<String, Set<String>> userPermissions = userService.getUserPermissions(userSet);
        userService.refreshCacheForRoleAndPerms(userRoles, userPermissions);
    }

    /**
     * 返回前端所需的Menu树的信息
     *
     * @param menu
     * @return
     */
    @Override
    public Map<String, Object> findMenuInfo(Menu menu) {
        Map<String, Object> info = new HashMap<>();
        try {

            Map<String, Object> conditions = new HashMap<>();
            if (StringUtils.isNotBlank(menu.getMenuName())) {
                conditions.put("menuName", menu.getMenuName());
            }
            if (null != menu.getType()) {
                conditions.put("type", menu.getType());
            }
            List<Menu> menus = menuRepository.findMenusByCondition(conditions);
            //ids，找到传入menu对象所对应的menu的IDS，并将menu转化为树
            ArrayList<TreeNode<Menu>> treeNodes = new ArrayList<>();
            ArrayList<String> ids = new ArrayList<>();
            menuToTree(treeNodes, ids, menus);
            info.put("ids", ids);

            //tree，判断如果是菜单，需要进一步树化，挂载上子节点，是否有孩子节点的信息
            if (StringUtils.equals(menu.getType() + "", Menu.TYPE_BUTTON)) {
                info.put("rows", treeNodes);
            }else if (StringUtils.equals(menu.getType() + "", Menu.TYPE_MENU)) {
                info.put("rows", TreeUtil.buildTree(treeNodes));
            }else {
                info.put("rows", TreeUtil.buildTree(treeNodes));
            }
            info.put("total", menus.size());
        } catch (NumberFormatException e) {
            log.error("查询菜单失败", e);
            info.put("rows", null);
            info.put("total", 0);
        }
        return info;
    }

    @Override
    public List<VueRouter<Menu>> findUserMenuRouter(String username) {
        List<VueRouter<Menu>> userRouters = new ArrayList<>();
        List<Menu> userMenus = userService.getUserMenus(username);
        userMenus.forEach(menu -> {
            VueRouter<Menu> route = new VueRouter<>();
            route.setId(menu.getMenuId().toString());
            route.setParentId(menu.getParentId().toString());
            route.setIcon(menu.getIcon());
            route.setPath(menu.getPath());
            route.setComponent(menu.getComponent());
            route.setName(menu.getMenuName());
            route.setMeta(new RouterMeta(true, null));
            userRouters.add(route);
        });
        return TreeUtil.buildVueRouter(userRouters);
    }

    @Override
    public Map<String, Object> getMenusWithParam(MenuSearch menuSearch) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Menu> menus = menuRepository.findMenusBySearchParam(menuSearch);
            List<TreeNode<Menu>> trees = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            menuToTree(trees, ids,menus);

            result.put("ids", ids);
            if (StringUtils.equals(menuSearch.getType(), Menu.TYPE_BUTTON)) {
                result.put("rows", trees);
            } else {
                TreeNode<Menu> menuTree = TreeUtil.buildTree(trees);
                result.put("rows", menuTree);
            }
            result.put("total", menus.size());
        } catch (NumberFormatException e) {
            log.error("查询菜单失败", e);
            result.put("rows", null);
            result.put("total", 0);
        }
        return result;
    }

    private void menuToTree(List<TreeNode<Menu>> treeNodes, List<String> ids, List<Menu> menus) {
        menus.forEach(m -> {
            ids.add(m.getMenuId().toString());
            TreeNode<Menu> node = new TreeNode<>();
            node.setId(m.getMenuId().toString());
            node.setKey(node.getId());
            node.setParentId(m.getParentId().toString());
            node.setText(m.getMenuName());
            node.setTitle(node.getText());
            node.setIcon(m.getIcon());
            node.setComponent(m.getComponent());
            node.setCreateTime(DateUtil.format(m.getCreateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()));
            node.setUpdateTime(DateUtil.format(m.getUpdateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()));
            node.setPath(m.getPath());
            node.setOrderNum(m.getOrderNum());
            node.setPermissions(m.getPermissions());
            node.setType(m.getType() + "");
            treeNodes.add(node);
        });
    }

    /**
     * 查找所有子菜单
     *
     * @param parentIds
     * @return
     */
    public List<Menu> findAllMenuByParentId(List<Integer> parentIds) {
        List<Menu> parents = menuRepository.findByParentIdIn(parentIds);
        if (parents == null || parents.size() == 0) {
            return new ArrayList<>();
        }

        List<Integer> list = parents.stream().map(Menu::getMenuId).distinct().collect(Collectors.toList());
        List<Menu> menuList = findAllMenuByParentId(list);
        menuList.addAll(parents);
        return menuList;

    }

    @Override
    public List<Menu> findAllMenuByParentIdWithIn(List<Integer> parentIds) {
        List<Menu> parentMenus = menuRepository.findByMenuIdIn(parentIds);
        List<Menu> childMenus = findAllMenuByParentId(parentIds);
        parentMenus.addAll(childMenus);
        return parentMenus.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Menu> findMenuByIds(List<Integer> menuIds) {
        return menuRepository.findByMenuIdIn(menuIds);
    }


}
