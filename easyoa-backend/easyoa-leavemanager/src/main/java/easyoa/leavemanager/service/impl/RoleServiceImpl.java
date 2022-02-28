package easyoa.leavemanager.service.impl;

import easyoa.common.constant.CacheConstant;
import easyoa.common.domain.dto.RoleDTO;
import easyoa.common.domain.vo.RoleSearch;
import easyoa.common.domain.vo.RoleVO;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.po.user.Menu;
import easyoa.core.domain.po.user.Role;
import easyoa.core.repository.RoleRepository;
import easyoa.core.service.MenuService;
import easyoa.core.service.RoleService;
import easyoa.core.service.UserService;
import easyoa.core.utils.EntityMapper;
import easyoa.leavemanager.service.redisson.RedissonService;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-24 - 09:05
 **/
@Slf4j
@Service("roleService")
//@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RedissonService redissonService;

    @Override
    public Role getRoleById(Integer roleId) {
        return roleRepository.findByRoleIdAndDeleted(roleId, false);
    }

    @Override
    public Set<Role> getRoleByIds(List<Integer> ids) {
        return roleRepository.findByDeletedAndRoleIdIn(false, ids);
    }

    @Override
    public Set<Integer> getRoleIdByIds(List<Integer> ids) {
        Set<Role> roleByIds = getRoleByIds(ids);
        if (roleByIds != null) {
            return roleByIds.stream().map(Role::getRoleId).collect(Collectors.toSet());
        }
        return null;
    }

    @Override
    public Page<Role> getRolePageWithParam(Pageable pageable, RoleSearch roleSearch) {
        return roleRepository.findRolePageBySearParam(roleSearch, pageable);
    }

    @Override
    public Page<Role> getRolePage(Pageable pageable, Role role) {
        return null;
    }


    @Override
    public Role findbyName(String roleName) {
        return roleRepository.findByRoleNameAndDeleted(roleName, false);
    }

    @Override
    @Transactional
    public void addRole(RoleVO vo) {
        Role role = EntityMapper.vo2Role(vo);
        if (role != null) {
            role.setCreateTime(new Date());
            role.setUpdateTime(role.getCreateTime());
            role.setDeleted(false);
            role.setDescription(vo.getDescription());
            role.setRoleName(vo.getRoleName());
            Role newRole = roleRepository.save(role);
            saveRoleMenu(vo.getMenuId(), newRole.getRoleId());
        }
    }

    @Override
    public void updateRole(RoleVO role) {
        //删除role
        Role roleById = getRoleById(role.getRoleId());
        roleById.setUpdateTime(new Date());
        if(StringUtils.isNotBlank(role.getDescription())){
            roleById.setDescription(role.getDescription());
        }
        roleRepository.save(roleById);
        //删除role-menu
        roleRepository.deleteRoleMenu(role.getRoleId());
        if (StringUtils.isNotBlank(role.getMenuId())) {
            List<Integer> ids = Arrays.asList(role.getMenuId().split(StringPool.COMMA)).stream().map(Integer::valueOf).collect(Collectors.toList());
            if (ids.size() != 0) {
                List<Menu> menus = menuService.findMenuByIds(ids);
                if (menus != null && menus.size() != 0) {
                    List<Integer> menuIds = menus.stream().map(Menu::getMenuId).distinct().collect(Collectors.toList());
                    //新增menu
                    roleRepository.saveRoleMenu(menuIds,role.getRoleId());
                }
            }
        }
        //获取角色对应的用户
        List<String> usernames = userService.getUserNamesForRole(role.getRoleId());
        if (usernames != null) {
            //刷新perms
            for(String username:usernames){
                //带刷新
                userService.getUserPermissions(username);
            }
        }
    }

    @Override
    public void deleteRole(Integer roleId) {
        //删除role
        Role roleById = getRoleById(roleId);
        roleById.setDeleted(true);
        roleRepository.save(roleById);
        //删除role-menu
        roleRepository.deleteRoleMenu(roleId);

        //获取角色对应的用户
        List<String> usernames = userService.getUserNamesForRole(roleId);
        if (usernames != null) {
            //删除user-role表
            userService.removeUserRole(Collections.singletonList(roleId));
            //刷新perms
            RMap<String, Set<String>> permsMap = redissonService.getSet(CacheConstant.USER_PERMS_CACHE_NAME);
            String[] names = usernames.toArray(new String[usernames.size()]);
            permsMap.fastRemove(names);
        }
    }

    @Override
    public void deleteRoles(List<Integer> roles) {
        for(Integer roleId : roles){
            deleteRole(roleId);
        }
    }

    @Override
    public List<RoleDTO> getRoles() {
        Pageable pageable = new PageRequest(0, 100);
        Page<Role> page = roleRepository.findByDeleted(false,pageable);
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            return EntityMapper.roleDTOList(page.getContent());
        }
        return null;
    }

    @Transactional
    public void saveRoleMenu(String menus, Integer roleId) {
        List<Integer> menuIds = Arrays.asList(menus.split(",")).stream().map(Integer::valueOf).distinct().collect(Collectors.toList());
        List<Menu> menuList = null;
        if (menuIds.size() != 0) {
            menuList = menuService.findMenuByIds(menuIds);
        }

        List<Integer> ids = null;
        if (menuList != null && menuList.size() != 0) {
            ids = menuList.stream().map(Menu::getMenuId).distinct().collect(Collectors.toList());
        }

        try {
            if (ids != null && ids.size() != 0) {
                roleRepository.saveRoleMenu(ids, roleId);
            }
        } catch (Exception e) {
            log.error("保存role menu出现异常{}", e);
            throw new BussinessException("保存角色菜单出现异常");
        }
    }

}
