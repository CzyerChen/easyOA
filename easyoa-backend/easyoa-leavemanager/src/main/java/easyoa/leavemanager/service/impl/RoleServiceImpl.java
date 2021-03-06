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
        //??????role
        Role roleById = getRoleById(role.getRoleId());
        roleById.setUpdateTime(new Date());
        if(StringUtils.isNotBlank(role.getDescription())){
            roleById.setDescription(role.getDescription());
        }
        roleRepository.save(roleById);
        //??????role-menu
        roleRepository.deleteRoleMenu(role.getRoleId());
        if (StringUtils.isNotBlank(role.getMenuId())) {
            List<Integer> ids = Arrays.asList(role.getMenuId().split(StringPool.COMMA)).stream().map(Integer::valueOf).collect(Collectors.toList());
            if (ids.size() != 0) {
                List<Menu> menus = menuService.findMenuByIds(ids);
                if (menus != null && menus.size() != 0) {
                    List<Integer> menuIds = menus.stream().map(Menu::getMenuId).distinct().collect(Collectors.toList());
                    //??????menu
                    roleRepository.saveRoleMenu(menuIds,role.getRoleId());
                }
            }
        }
        //???????????????????????????
        List<String> usernames = userService.getUserNamesForRole(role.getRoleId());
        if (usernames != null) {
            //??????perms
            for(String username:usernames){
                //?????????
                userService.getUserPermissions(username);
            }
        }
    }

    @Override
    public void deleteRole(Integer roleId) {
        //??????role
        Role roleById = getRoleById(roleId);
        roleById.setDeleted(true);
        roleRepository.save(roleById);
        //??????role-menu
        roleRepository.deleteRoleMenu(roleId);

        //???????????????????????????
        List<String> usernames = userService.getUserNamesForRole(roleId);
        if (usernames != null) {
            //??????user-role???
            userService.removeUserRole(Collections.singletonList(roleId));
            //??????perms
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
            log.error("??????role menu????????????{}", e);
            throw new BussinessException("??????????????????????????????");
        }
    }

}
