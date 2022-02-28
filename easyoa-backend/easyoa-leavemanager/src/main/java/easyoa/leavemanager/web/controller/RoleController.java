package easyoa.leavemanager.web.controller;

import easyoa.leavemanager.web.AbstractController;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.dto.RoleDTO;
import easyoa.common.domain.vo.RoleSearch;
import easyoa.common.domain.vo.RoleVO;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.po.user.Role;
import easyoa.core.service.RoleService;
import easyoa.core.service.UserService;
import easyoa.core.utils.EntityMapper;
import easyoa.leavemanager.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-28 - 14:19
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "角色相关接口",description = "角色相关接口")
public class RoleController extends AbstractController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    /**
     * 根据用户名，获取用户角色
     * @param username
     * @return
     */
    @ApiOperation(value = " 根据用户名，获取用户角色",notes = "管理人员专用")
    @GetMapping("{username}")
    @RequiresPermissions("role:view")
    public ApiResponse getUserRole(@NotBlank(message = "{requied}")@PathVariable String username){
        Set<String> roles = userService.getUserRoles(username);
        return ApiResponse.success(roles);
    }

    /**
     * 分页获取角色列表
     * @param entry
     * @return
     */
    @ApiOperation(value = " 分页获取角色列表",notes = "管理人员专用")
    @GetMapping
    @RequiresPermissions("role:view")
    public Map<String, Object> rolePage( PageRequestEntry entry, RoleSearch roleName) {
        Pageable pageable = getPageRequest(entry);
        Page<Role> rolePage = roleService.getRolePageWithParam(pageable, roleName);
        return getMap(EntityMapper.roleDTOPage(rolePage));
    }

    /**
     * 获取所有角色
     * @return
     */
    @ApiOperation(value = " 获取所有角色",notes = "管理人员专用")
    @GetMapping("/all")
    @RequiresPermissions("role:view")
    public Map<String, Object> roleList() {
        List<RoleDTO> roles = roleService.getRoles();
        return getMap(roles);
    }

    /**
     * 检查角色名称是否存在
     * @param roleName
     * @return
     */
    @ApiOperation(value = " 检查角色名称是否存在",notes = "管理人员专用")
    @GetMapping("check/{roleName}")
    public boolean checkRoleName(@NotBlank(message = "{required}") @PathVariable String roleName) {
        Role result = roleService.findbyName(roleName);
        return result == null;
    }

    /**
     * 查看角色对应的菜单列表
     * @param roleId
     * @return
     */
    @ApiOperation(value = " 查看角色对应的菜单列表",notes = "管理人员专用")
    @GetMapping("menu/{roleId}")
    public List<String> getRoleMenus(@NotBlank(message = "{required}") @PathVariable String roleId) {
        Role role = roleService.getRoleById(Integer.valueOf(roleId));
        if(role!= null && role.getMenuList()!= null && role.getMenuList().size()!=0){
            return role.getMenuList().stream().map(m->String.valueOf(m.getMenuId())).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 新增角色
     * @param role
     * @throws BussinessException
     */
    @ApiOperation(value = " 新增角色",notes = "管理人员专用")
    @Log("新增角色")
    @PostMapping
    @RequiresPermissions("role:add")
    public void addRole(@Valid RoleVO role) throws BussinessException {
        try {
            this.roleService.addRole(role);
        } catch (Exception e) {
            log.error("新增角色失败", e);
            throw new BussinessException("新增角色失败");
        }
    }

    /**
     * 删除角色
     * @param roleIds
     * @throws BussinessException
     */
    @ApiOperation(value = " 删除角色",notes = "管理人员专用")
    @Log("删除角色")
    @DeleteMapping("/{roleIds}")
    @RequiresPermissions("role:delete")
    public void deleteRoles(@NotBlank(message = "{required}") @PathVariable String roleIds) throws BussinessException {
        try {
            String[] ids = roleIds.split(StringPool.COMMA);
            ArrayList<String> list = new ArrayList<String>(ids.length);
            Collections.addAll(list,ids);

            this.roleService.deleteRoles(list.stream().map(Integer::valueOf).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("删除角色失败{}", e);
            throw new BussinessException("删除角色失败");
        }
    }

    /**
     * 修改角色包括修改角色所对应的的菜单列表,不允许修改角色的名字
     * @param role
     * @throws BussinessException
     */
    @ApiOperation(value = " 修改角色",notes = "管理人员专用")
    @Log("修改角色")
    @PutMapping
    @RequiresPermissions("role:update")
    public void updateRole(RoleVO role) throws BussinessException {
        try {
            this.roleService.updateRole(role);
        } catch (Exception e) {
            log.error("修改角色失败{}", e);
            throw new BussinessException("修改角色失败");
        }
    }

}
