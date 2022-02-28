package easyoa.leavemanager.web.controller;

import easyoa.common.domain.vo.MenuSearch;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.po.user.Menu;
import easyoa.core.model.VueRouter;
import easyoa.core.service.MenuService;
import easyoa.leavemanager.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-06-26 - 09:08
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/menu")
@Api(value = "菜单相关接口",description = "菜单相关接口")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取用户菜单router
     * @param username
     * @return
     */
    @GetMapping("/{username}")
    @RequiresPermissions("menu:view")
    @ApiOperation(value = "获取用户菜单router",notes = "获取用户菜单router")
    @ApiImplicitParam(required = true,name = "username",value ="用户名",dataType = "String")
    public List<VueRouter<Menu>> getMenuRouter(@NotBlank(message = "{required}") @PathVariable String username) {
        return menuService.findUserMenuRouter(username);
    }

    /**
     * 查看菜单中的具体内容
     *
     * @param menuSearch
     * @return
     */
    @GetMapping
    @RequiresPermissions("menu:view")
    @ApiOperation(value = "查看菜单内容",notes = "")
    @ApiImplicitParam(required = true,name = "menu",value = "菜单对象",dataType = "Menu")
    public Map<String, Object> getMenuInfo(MenuSearch menuSearch) {
        return menuService.getMenusWithParam(menuSearch);
    }



    /**
     * 新增菜单、按钮
     *
     * @param menu
     */
    @Log("新增菜单、新增按钮")
    @PostMapping
    @RequiresPermissions("menu:add")
    @ApiOperation(value = "新增",notes = "新增")
    @ApiImplicitParam(required = true,name = "menu",value = "菜单对象",dataType = "Menu")
    public void addMenu(@Valid Menu menu) {
        try {
            menuService.addMenu(menu);
        } catch (Exception e) {
            log.error("添加菜单/按钮出现异常，请检查menuService.addMenu(menu)", e);
            throw new BussinessException("添加菜单/按钮出现异常");
        }
    }

    /**
     * 删除菜单、按钮
     *
     * @param menuIds
     */
    @Log("删除菜单/删除按钮")
    @DeleteMapping("/{menuIds}")
    @RequiresPermissions("menu:delete")
    @ApiOperation(value = "删除",notes = "删除")
    @ApiImplicitParam(required = true,name = "menuIds",value = "菜单对象ID",dataType = "String")
    public void deleteMenus(@NotBlank(message = "{required}") @PathVariable String menuIds) {
        //以逗号隔离的菜单ids
        String[] menus = menuIds.split(StringPool.COMMA);
        try {
            menuService.deleteMenus(menus);
        }catch (Exception e){
            log.error("删除菜单/按钮失败，请检查menuService.deleteMenus(menus)",e);
            throw new BussinessException("删除菜单/按钮失败");
        }
    }

    /**
     * 修改菜单、按钮
     * menuName: 新增申请
     * parentId: 148
     * type: 1
     * menuId: 149
     *
     * @param menu
     */
    @Log("修改菜单/修改按钮")
    @PutMapping
    @RequiresPermissions("menu:update")
    @ApiOperation(value = "更新",notes = "更新")
    @ApiImplicitParam(required = true,name = "menu",value = "菜单对象",dataType = "Menu")
    public void updateMenu(@Valid Menu menu) {
       try{
           menuService.updateMenu(menu);
       }catch (Exception e){
           log.error("更新菜单/按钮失败,请检查menuService.updateMenu(menu)",e);
           throw new BussinessException("更新菜单/按钮失败");
       }
    }




}
