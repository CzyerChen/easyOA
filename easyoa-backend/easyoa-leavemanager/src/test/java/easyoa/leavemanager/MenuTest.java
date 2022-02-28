package easyoa.leavemanager;

import easyoa.core.domain.po.user.Menu;
import easyoa.core.repository.MenuRepository;
import easyoa.core.service.MenuService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-06-26 - 09:46
 **/
public class MenuTest extends AbstractApplicationTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;


    @Test
    public void testFindMenu() {
        List<Integer> list  = new ArrayList<>();
        list.add(1);
        List<Menu> menu = menuService.findAllMenuByParentId(list);
        Assert.assertNotNull(menu);
    }

    @Test
    public void testDeleteMenus(){
        List<Integer> list  = new ArrayList<>();
        list.add(1);
        menuService.deleteAllChildrenMenu(list);
    }

    @Test
    public void testFindByCondition(){
        Map<String,Object> map = new HashMap<>();
        map.put("menuName","用户详情");
        map.put("type",0);
        List<Menu> menusByCondition = menuRepository.findMenusByCondition(map);
        Assert.assertNotNull(menusByCondition);
    }

}
