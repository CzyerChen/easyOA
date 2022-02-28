package easyoa.core.service;

import easyoa.common.domain.vo.MenuSearch;
import easyoa.core.domain.po.user.Menu;
import easyoa.core.model.VueRouter;

import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-06-24 - 09:10
 **/
public interface MenuService {
    public void addMenu(Menu menu);

    public void deleteMenus(String[] menuIds);

    public List<Menu> findAllMenuByParentId(List<Integer> parentIds);

    public List<Menu> findAllMenuByParentIdWithIn(List<Integer> parentIds);

    public List<Menu> findMenuByIds(List<Integer> menuIds);

    public void deleteAllChildrenMenu(List<Integer> menus);

    public void updateMenu(Menu menu);

    public Map<String,Object> findMenuInfo(Menu menu);

    public List<VueRouter<Menu>> findUserMenuRouter(String username);

    public Map<String,Object> getMenusWithParam(MenuSearch menuSearch);


}
