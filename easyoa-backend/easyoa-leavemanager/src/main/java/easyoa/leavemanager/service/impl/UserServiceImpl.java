package easyoa.leavemanager.service.impl;

import easyoa.core.domain.po.user.*;
import easyoa.core.service.*;
import easyoa.leavemanager.domain.user.UserPosition;
import easyoa.leavemanager.domain.user.UserReporter;
import easyoa.leavemanager.runner.api.RuleServer;
import easyoa.leavemanager.service.CacheService;
import easyoa.leavemanager.service.UserPositionService;
import easyoa.leavemanager.service.redisson.RedissonService;
import easyoa.leavemanager.utils.ShiroMD5Util;
import easyoa.common.constant.CacheConstant;
import easyoa.common.constant.UserConstant;
import easyoa.common.constant.UserTypeEnum;
import easyoa.common.domain.dto.RoleDTO;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.common.domain.vo.UserSearch;
import easyoa.common.exception.BussinessException;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.UserDepartmentVO;
import easyoa.core.domain.dto.UserExcel;
import easyoa.core.repository.RoleRepository;
import easyoa.core.repository.UserRepository;
import easyoa.core.utils.EntityMapper;
import easyoa.leavemanager.repository.user.UserReporterRepository;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by claire on 2019-06-24 - 08:32
 **/
@Service("userService")
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedissonService redissonService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserPositionService userPositionService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private UserImageService userImageService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RuleServer ruleServer;
    @Autowired
    private UserReporterRepository userReporterRepository;
    @Autowired
    private CompanyService companyService;

    private List<Integer> getUserRoleIds(String username) {
        UserDTO user = null;
        if (redissonService.existKey(CacheConstant.USER_CACHE_NAME, username)) {
            user = redissonService.getMapValues(CacheConstant.USER_CACHE_NAME, username);
        }
        if (user != null) {
            if (user.getRoles() != null) {
                return user.getRoles().stream().map(RoleDTO::getRoleId).distinct().collect(Collectors.toList());
            }
        }
        return null;
    }

    /**
     * ?????????????????????????????????
     *
     * @param username
     * @return
     */
    @Override
    public UserDTO getUserDto(String username) {
        User user = userRepository.findByUserNameAndDeleted(username, false);
        if (user != null) {
            UserDTO userDTO = EntityMapper.userRole2DTO(user);
            if (userDTO != null) {
                RMap<String, UserDTO> userMap = cacheService.getUserMap();
                if (userMap.containsKey(username)) {
                    userMap.replace(username, userDTO);
                } else {
                    userMap.put(username, userDTO);
                }
                return userDTO;
            }

        }
        return null;
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUserNameAndDeleted(username, false);
    }

    @Override
    public User getUserByCode(String userCode) {
        return userRepository.findByUserCodeAndDeleted(userCode, false);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findByUserIdAndDeleted(userId, false);
    }

    @Override
    public List<UserDTO> getUsers() {
        List<User> list = userRepository.findAll();
        if (list != null && list.size() > 0) {
            List<UserDTO> dtos = new ArrayList<>();
            transferUser2DTO(list, dtos);
            return dtos;
        }
        return null;
    }

    @Override
    public List<UserDTO> getUsers(Integer companyId) {
        List<User> list = findByDeletedAndCompanyId(Boolean.FALSE, companyId);
        if (list.size() > 0) {
            List<UserDTO> dtos = new ArrayList<>();
            transferUser2DTO(list, dtos);
            return dtos;
        }
        return null;
    }

    @Override
    public Page<UserDTO> getPageUsers(Pageable pageable) {
        Pageable pageable1 = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "userId");
        Page<User> page = userRepository.findByDeleted(false, pageable1);
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            List<UserDTO> dtos = new ArrayList<>();
            transferUser2DTO(page.getContent(), dtos);
            return new PageImpl<>(dtos, pageable1, page.getTotalElements());
        }
        return null;
    }

    @Override
    public Page<UserDTO> getPageUsersWithSearchParam(Pageable pageable, UserSearch userSearch) {
        Pageable pageable1 = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "userId");
        Page<User> page = userRepository.findPageBySearchParam(userSearch, pageable1);
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            List<UserDTO> dtos = new ArrayList<>();
            transferUser2DTO(page.getContent(), dtos);
            return new PageImpl<>(dtos, pageable1, page.getTotalElements());
        }
        return null;
    }


    private void transferUser2DTO(List<User> users, List<UserDTO> userDTOS) {
        users.forEach(u -> {
            UserDTO userDTO = EntityMapper.userRole2DTO(u);
            if (userDTO != null) {
                if(!CollectionUtils.isEmpty(u.getRoleList())) {
                    Role role = u.getRoleList().get(0);
                    userDTO.setRoleId(role.getRoleId());
                    userDTO.setRoleName(role.getRoleName());
                }
                Long imageId = userImageService.findUserImageByName(userDTO.getUserName());
                if (null != imageId) {
                    userDTO.setAvatar(imageId.toString());
                }
                UserPosition position = userPositionService.findByUserCode(userDTO.getUserCode());
                if (position != null) {
                    userDTO.setPosition(position.getName());
                }
                Department dept = departmentService.findByDeptId(userDTO.getDeptId());
                if (dept != null) {
                    userDTO.setDeptName(dept.getDeptName());
                }
                UserDetail userDetail = userDetailService.findById(userDTO.getUserCode());
                if (userDetail != null) {
                    userDTO.setEmail(userDetail.getEmail());
                    userDTO.setPhone(userDetail.getPhone());
                }
                String userLastLoginTime = getUserLastLoginTime(userDTO.getUserName());
                if (StringUtils.isNotBlank(userLastLoginTime)) {
                    userDTO.setLastLoginTime(userLastLoginTime);
                }

                userDTOS.add(userDTO);
            }
        });
    }

    @Override
    public Set<String> getUserRoles(String username) {
        UserDTO user = getUserDto(username);
        /*if (redissonService.existKey(CacheConstant.USER_CACHE_NAME, username)) {
            user = redissonService.getMapValues(CacheConstant.USER_CACHE_NAME, username);
        }*/

        if (user != null) {
            if (user.getRoles() != null) {
                Set<String> roles = user.getRoles().stream().map(RoleDTO::getRoleName).collect(Collectors.toSet());
                RMap<String, Set<String>> roleMap = redissonService.getSet(CacheConstant.USER_ROLE_CACHE_NAME);
                if (roleMap.containsKey(username)) {
                    roleMap.replace(username, roles);
                } else {
                    roleMap.put(username, roles);
                }
                return roles;
            }
        }
        return null;
    }

    @Override
    public Set<String> getUserPermissions(String username) {
        List<Integer> roleIds = getUserRoleIds(username);
        if (roleIds != null && roleIds.size() > 0) {
            Set<Role> roles = roleRepository.findByDeletedAndRoleIdIn(false, roleIds);
            if (roles != null) {
                Set<String> perms = new HashSet<>();
                roles.forEach(r -> {
                    if (r.getMenuList() != null) {
                        Set<String> permissions = r.getMenuList().stream().filter(m -> null != m.getPermissions()).map(Menu::getPermissions).collect(Collectors.toSet());
                        perms.addAll(permissions);
                    }
                });
                if (perms.size() != 0) {
                    RMap<String, Set<String>> permsMap = redissonService.getSet(CacheConstant.USER_PERMS_CACHE_NAME);
                    if (permsMap.containsKey(username)) {
                        permsMap.replace(username, perms);
                    } else {
                        permsMap.put(username, perms);
                    }
                }
                return perms;
            }
        }
        return null;
    }

    @Override
    public Map<String, Set<String>> getUserRoles(Set<Long> userIds) {
        Map<String, Set<String>> roleMap = new HashMap<>();
        for (Long userId : userIds) {
            User user = getUser(userId);
            if (user != null && user.getRoleList() != null && user.getRoleList().size() > 0) {
                Set<String> roles = user.getRoleList().stream().map(Role::getRoleName).collect(Collectors.toSet());
                roleMap.put(user.getUserName(), roles);
            }
        }
        return roleMap;
    }


    @Override
    public Map<String, Set<String>> getUserPermissions(Set<Long> userIds) {
        Map<String, Set<String>> permsMap = new HashMap<>();
        for (Long userId : userIds) {
            User user = userRepository.findByUserIdAndDeleted(userId, false);
            if (user != null && user.getRoleList() != null && user.getRoleList().size() > 0) {
                List<Integer> roleIds = user.getRoleList().stream().map(Role::getRoleId).collect(Collectors.toList());
                Set<Role> roles = roleRepository.findByDeletedAndRoleIdIn(false, roleIds);
                if (roles != null) {
                    Set<String> perms = new HashSet<>();
                    roles.forEach(r -> {
                        if (r.getMenuList() != null) {
                            perms.addAll(r.getMenuList().stream().map(Menu::getPermissions).collect(Collectors.toSet()));
                        }
                    });
                    permsMap.put(user.getUserName(), perms);
                }
            }
        }
        return permsMap;
    }


    @Override
    public UserConfig getUserConfig(long userId) {
        return userConfigService.getUserConfigById(userId);
    }

    @Override
    public Map<String, Object> initUserInfoVO(User user, String token, String expirtAt) {
        String username = user.getUserName();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("token", token);
        userInfo.put("exipreTime", expirtAt);

        Set<String> roles = getUserRoles(username);
        userInfo.put("roles", roles);

        Set<String> permissions = getUserPermissions(username);
        userInfo.put("permissions", permissions);

        UserConfig userConfig = getUserConfig(user.getUserId());
        userInfo.put("config", userConfig);

        user.setPassword("hello world!");
        if (StringUtils.isNotBlank(user.getAvatar())) {
            String image = userImageService.findUserImageById(Long.valueOf(user.getAvatar()));
            user.setAvatar(image);
        }
        UserDTO userDTO = EntityMapper.userRole2DTO(user);

        UserDetail userDetail = userDetailService.findbyUserId(user.getUserId());
        if (userDetail != null) {
            if (StringUtils.isNotBlank(userDetail.getPhone())) {
                userDTO.setPhone(userDetail.getPhone());
            }
            if (StringUtils.isNotBlank(userDetail.getEmail())) {
                userDTO.setEmail(userDetail.getEmail());
            }
        } else {
            log.error("user:{} ????????????????????????", user.getUserId());
        }

        UserDepartmentVO userDepartment = departmentService.getUserDepartment(user.getUserId());
        if (userDepartment != null) {
            userDTO.setDeptId(userDepartment.getDeptId());
            userDTO.setDeptName(userDepartment.getDeptName());

            Department department = departmentService.findByDeptId(userDepartment.getDeptId());
            String companyName = companyService.findNameById(department.getCompanyId());
            userDTO.setCompanyId(department.getCompanyId());
            userDTO.setCompanyName(companyName);
        }
        UserPosition position = userPositionService.findByUserCode(user.getUserCode());
        if (position != null) {
            userDTO.setPosition(position.getName());
        }
        String userLastLoginTime = getUserLastLoginTime(username);
        if (StringUtils.isNotBlank(userLastLoginTime)) {
            userDTO.setLastLoginTime(userLastLoginTime);
        }
        userInfo.put("user", userDTO);
        return userInfo;
    }

    @Override
    public List<Long> getUserIdsForMenu(int menuId) {
        List<BigInteger> forMenu = userRepository.findUserIdsForMenu(menuId);
        if (forMenu != null) {
            return forMenu.stream().map(b -> b.longValue()).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<BigInteger> getUserIdsForRole(int roleId) {
        return userRepository.findUserIdsForRole(roleId);
    }

    @Override
    public List<String> getUserNamesForRole(int roleId) {
        List<BigInteger> userIds = getUserIdsForRole(roleId);
        if (userIds != null && userIds.size() != 0) {
            List<User> users = userRepository.findByDeletedAndUserIdIn(false, userIds.stream().map(BigInteger::longValue).collect(Collectors.toList()));
            if (users != null && users.size() != 0) {
                return users.stream().map(User::getUserName).distinct().collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public List<Long> getUserIdsForRoles(List<Integer> roles) {
        return userRepository.findUserIdsForRole(roles);
    }

    @Override
    public List<Menu> getUserMenus(String username) {
        List<Menu> list = new ArrayList<>();
        User user = getUser(username);
        if (user != null && user.getRoleList() != null && user.getRoleList().size() != 0) {
            List<Integer> roleIds = user.getRoleList().stream().map(Role::getRoleId).collect(Collectors.toList());
            Set<Role> roles = roleRepository.findByDeletedAndRoleIdIn(false, roleIds);
            if (roles != null && roles.size() != 0) {
                roles.stream().map(Role::getMenuList).forEach(l -> {
                    l.stream().filter(m -> m.getType().equals("0")).forEach(list::add);
                });
            }
        }
        return list;
    }

    @Override
    public Boolean checkUsernameExist(String username) {
        User user = getUser(username);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean registUser(String username, String password, Integer roleId, Integer deptId, Integer companyId, Character sex, Integer status) throws BussinessException {
        User user = new User();
        if (StringUtils.isBlank(password)) {
            password = UserConstant.USER_DEFAULT_PASSWORD;
        }
        user.setPassword(ShiroMD5Util.encrypt(username, password));
        user.setUserName(username);
        user.setNickName(username);
        user.setCreateTime(new Date());
        user.setUpdateTime(user.getCreateTime());
        //user.setUserCode(userCode);
        if (null != status) {
            user.setStatus(status);
        } else {
            user.setStatus(Integer.valueOf(UserConstant.USER_STATUS_VALID));
        }

        user.setAvatar(UserConstant.USER_DEFAULT_AVATAR);
        user.setDeleted(false);
        //????????????
        Integer deptIdDB = departmentService.findDeptIdByUsername(username);
        if (null == deptId) {
            user.setDeptId(deptIdDB);
        } else if (deptId.equals(deptIdDB)) {
            user.setDeptId(deptId);
        } else {
            throw new BussinessException("???????????????????????????????????????????????????????????????");
        }
        user.setCompanyId(companyId);

        //??????userdetail
        UserDetail userDetail = userDetailService.findByEmail(username);
        if (null != userDetail.getSex()) {
            user.setSex(userDetail.getSex() ? 'M' : 'F');
        } else {
            user.setSex(UserConstant.USER_SEX_UNKNOW);
        }
        user.setUserCode(userDetail.getId());

        User newUser = userRepository.save(user);
        userDetail.setUserId(newUser.getUserId());
        userDetailService.saveUserDetail(userDetail);

        if (null != roleId) {
            userRepository.saveUserRole(newUser.getUserId(), roleId);
        } else {
            userRepository.saveUserRole(newUser.getUserId(), 3);//??????2???????????????
        }
        // ????????????????????????????????????
        userConfigService.initDefaultUserConfig(user.getUserId());
        //??????????????????????????????
        ruleServer.saveUserVacation(String.valueOf(newUser.getUserId()));
        //????????????????????????
        ruleServer.saveUserVacationCal(String.valueOf(newUser.getUserId()));
        //??????!!!
        entityManager.clear();
        // ?????????????????????????????? Redis???,get??????????????????????????????
        //??????User
        User user1 = getUser(newUser.getUserName());
        cacheUser(user1);
        getUserRoles(username);
        getUserPermissions(username);
        return true;
    }

    @Override
    public void saveUserPostion(List<UserExcel> list) {
        Map<String, String> map = new HashMap<>();
        list.forEach(u -> {
            map.put(u.getUserCode(), u.getPosition());
        });
        userPositionService.saveUserPostionInfo(map);
    }

    @Override
    public void saveUserDepartment(List<UserExcel> list) {
        Map<String, Integer> map = new HashMap<>();
        list.forEach(u -> {
            int deptId = departmentService.findDeptByDeptNameAndCenter(u.getDeptName(), u.getCenter());
            if (deptId > 0) {
                map.put(u.getEmail(), deptId);
            }
        });
        departmentService.saveUserDepartmentInfo(map);
    }

    @Override
    public void saveUserDetail(List<UserExcel> list) {
        List<UserDetail> details = new ArrayList<>();
        list.forEach(u -> {
            UserDetail d = new UserDetail();
            d.setId(u.getUserCode());
            d.setCenter(u.getCenter());
            d.setPositionName(u.getPosition());
            d.setDeleted(false);

            if (StringUtils.isNotBlank(u.getChineseName())) {
                d.setChineseName(u.getChineseName());
            }
            if (StringUtils.isNotBlank(u.getEnglishName())) {
                d.setEnglishName(u.getEnglishName());
            }
            /*if (StringUtils.isNotBlank(u.getAge())) {
                d.setAge(Integer.valueOf(u.getAge()));
            }*/
            if (StringUtils.isNotBlank(u.getWorkYear())) {
                d.setWorkYear(Double.valueOf(u.getWorkYear()));
            }
            /*if (StringUtils.isNotBlank(u.getWorkInCompony())) {
                d.setWorkInCompony(Double.valueOf(u.getWorkInCompony()));
            }*/
            if (StringUtils.isNotBlank(u.getType())) {
                if (UserTypeEnum.REGULAR.toString().equals(u.getType())) {
                    d.setType(UserTypeEnum.REGULAR.getId());
                } else if (UserTypeEnum.OUTER.toString().equals(u.getType())) {
                    d.setType(UserTypeEnum.OUTER.getId());
                }
            }
            if (StringUtils.isNotBlank(u.getSex())) {
                d.setSex(u.getSex().equals("???"));
            }
            if (StringUtils.isNotBlank(u.getMarriage())) {
                d.setMarriage(u.getMarriage().equals("??????"));
            }
            if (u.getWorkDate() != null) {
                d.setWorkDate(u.getWorkDate());
            }
            if (StringUtils.isNotBlank(u.getPhone())) {
                d.setPhone(u.getPhone());
            }
            if (StringUtils.isNotBlank(u.getEmail())) {
                d.setEmail(u.getEmail());
            }
            if (u.getHireDate() != null) {
                d.setHireDate(u.getHireDate());
            }
            /*if (u.getBirthDate() != null) {
                d.setBirthDate(u.getBirthDate());
            }

            if (StringUtils.isNotBlank(u.getAddress())) {
                d.setAddress(u.getAddress());
            }*/

            details.add(d);

        });

        userDetailService.saveUserDetail(details);

    }

    @Override
    public UserDetail saveUserDetail(UserDetail userDetail) {
        return userDetailService.saveUserDetail(userDetail);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * ????????????
     *
     * @param userRoles
     * @param userPerms
     */
    @Override
    public void refreshCacheForRoleAndPerms(Map<String, Set<String>> userRoles, Map<String, Set<String>> userPerms) {
        RMap<String, Set<String>> roleMap = cacheService.getRoleSet();
        RMap<String, Set<String>> permsMap = cacheService.getPermsSet();

        for (Map.Entry<String, Set<String>> role : userRoles.entrySet()) {
            if (!roleMap.containsKey(role.getKey())) {
                roleMap.fastPut(role.getKey(), role.getValue());
            } else {
                roleMap.fastReplace(role.getKey(), role.getValue());
            }
        }
        for (Map.Entry<String, Set<String>> perms : userPerms.entrySet()) {
            if (!permsMap.containsKey(perms.getKey())) {
                permsMap.put(perms.getKey(), perms.getValue());
            } else {
                permsMap.fastReplace(perms.getKey(), perms.getValue());
            }
        }
    }

    @Override
    public String getUserNameById(Long userId) {
        User user = userRepository.findByUserIdAndDeleted(userId, false);
        return user != null ? user.getUserName() : null;
    }

    @Override
    public void removeActiveUser(UserLoginDTO userLoginDTO) {
        RScoredSortedSet<UserLoginDTO> activeUserSet = cacheService.getActiveUserSet();

        boolean remove = activeUserSet.remove(userLoginDTO);
    }

    @Override
    public void removeUserToken(String key) {
        RMapCache<String, String> tokenMapCache = cacheService.getTokenMapCache();
        String obj = tokenMapCache.remove(key);
    }

    @Override
    @Transactional
    public void removeUser(String[] userIds) {
        String[] usernames = new String[userIds.length];
        Long[] ids = new Long[userIds.length];
        List<UserConfig> configs = new ArrayList<>();
        //??????user
        int i = 0;
        int j = 0;
        for (String userId : userIds) {
            Long id = Long.valueOf(userId);
            ids[j++] = id;
            User user = userRepository.findByUserIdAndDeleted(id, false);
            if (user == null) {
                continue;
            }

            userRepository.delete(user);
            //??????user-role
            userRepository.deleteUserRole(id);
            String username = user.getUserName();
            if (StringUtils.isNotBlank(username)) {
                usernames[i++] = username;
            }
            //?????????????????????
            UserConfig config = userConfigService.getUserConfigById(id);
            if (config != null) {
                configs.add(config);
            }

            //RScoredSortedSet<UserLoginDTO> activeUserSet = redissonService.getActiveUserSet();
            //??????set???????????????????????????????????????????????????????????????????????????
            //RMapCache<String, String> tokenMapCache = redissonService.getTokenMapCache();
            //token???????????????????????????????????????????????????
            UserLoginDTO userLoginDTO = null;
            String now = DateUtil.formatFullTime(LocalDateTime.now());
            Collection<UserLoginDTO> loginUsers = cacheService.getLoginUserListByRange(Double.valueOf(now), Double.MAX_VALUE);
            for (UserLoginDTO dto : loginUsers) {
                if (StringUtils.equals(dto.getUsername(), username)) {
                    userLoginDTO = dto;
                }
            }
            if (userLoginDTO != null) {
                removeActiveUser(userLoginDTO);
                removeUserToken(userLoginDTO.getToken() + "." + userLoginDTO.getIp());
            }
        }
        //??????????????????
        userConfigService.removeUserConfigs(configs);
        //?????????????????????userId??????


        //????????????
        RMap<String, UserDTO> userMap = cacheService.getUserMap();
        userMap.fastRemove(usernames);

        RMap<String, Long> loginTimeMap = cacheService.getUserLastLoginTimeMap();
        loginTimeMap.fastRemove(usernames);

        RMap<Long, UserConfig> userConfigMap = cacheService.getUserConfigMap();
        userConfigMap.fastRemove(ids);

        //??????????????????????????????
        RMap<String, Set<String>> roleMap = cacheService.getRoleSet();
        roleMap.fastRemove(usernames);
        RMap<String, Set<String>> permsMap = cacheService.getPermsSet();
        permsMap.fastRemove(usernames);
    }

    @Override
    public void removeUserRole(List<Integer> roleIds) {
        userRepository.deleteUserRole(roleIds);
    }


    @Override
    public UserDetail findUserDetailByUserId(Long userId) {
        return userDetailService.findbyUserId(userId);
    }

    @Override
    public void refreshCacheForUser(User user, UserDetail userDetail) {
        if (user != null) {
            RMap<String, UserDTO> userMap = cacheService.getUserMap();
            UserDTO userDTO = EntityMapper.userRole2DTO(user);
            if (userDetail != null && StringUtils.isNotBlank(userDetail.getEmail())) {
                userDTO.setEmail(userDetail.getEmail());
            }
            if (userDetail != null && StringUtils.isNotBlank(userDetail.getPhone())) {
                userDTO.setPhone(userDetail.getPhone());
            }
            //avatar
            String imageName = userImageService.findUserImageById(Long.valueOf(userDTO.getAvatar()));
            userDTO.setAvatar(imageName);
            //position
            UserPosition position = userPositionService.findByUserCode(userDTO.getUserCode());
            if (position != null) {
                userDTO.setPosition(position.getName());
            }
            //deptName
            Department dept = departmentService.findByDeptId(userDTO.getDeptId());
            if (dept != null) {
                userDTO.setDeptName(dept.getDeptName());
            }
            //lastLoginTime
            String userLastLoginTime = getUserLastLoginTime(userDTO.getUserName());
            if (StringUtils.isNotBlank(userLastLoginTime)) {
                userDTO.setLastLoginTime(userLastLoginTime);
            }

            if (userMap.containsKey(user.getUserName())) {
                userMap.replace(userDTO.getUserName(), userDTO);
            } else {
                userMap.put(userDTO.getUserName(), userDTO);
            }
        }
    }

    @Override
    public String getUserLastLoginTime(String username) {
        RMap<String, Long> loginTimeMap = cacheService.getUserLastLoginTimeMap();
        if (loginTimeMap.containsKey(username)) {
            return DateUtil.formatTimeStampWothBasePattern(loginTimeMap.get(username));
        }
        return null;
    }

    @Override
    public UserDTO getCurrentUserInfo(String username) {
        RMap<String, UserDTO> userMap = cacheService.getUserMap();
        if (userMap.containsKey(username)) {
            return userMap.get(username);
        }
        return null;
    }

    @Override
    public void updatePassword(String username, String password) {
        User user = getUser(username);
        if (user != null) {
            user.setPassword(ShiroMD5Util.encrypt(username, password));
            saveUser(user);
        }
    }

    @Override
    public void resetPassword(String[] usernames) {
        //??????user
        for (String username : usernames) {
            User user = userRepository.findByUserNameAndDeleted(username, false);
            user.setPassword(ShiroMD5Util.encrypt(username, UserConstant.USER_DEFAULT_PASSWORD));
            saveUser(user);
        }
    }

    @Override
    public void updateUserConfig(UserConfig userConfig) {
        UserConfig config = userConfigService.getUserConfigById(userConfig.getUserId());
        if (config != null) {
            if (StringUtils.isNotBlank(userConfig.getColor())) {
                config.setColor(userConfig.getColor());
            }
            if (StringUtils.isNotBlank(userConfig.getFixHeader())) {
                config.setFixHeader(userConfig.getFixHeader());
            }
            if (StringUtils.isNotBlank(userConfig.getFixSiderbar())) {
                config.setFixSiderbar(userConfig.getFixSiderbar());
            }
            if (StringUtils.isNotBlank(userConfig.getLayout())) {
                config.setLayout(userConfig.getLayout());
            }
            if (StringUtils.isNotBlank(userConfig.getMultiPage())) {
                config.setMultiPage(userConfig.getMultiPage());
            }
            if (StringUtils.isNotBlank(userConfig.getTheme())) {
                config.setTheme(userConfig.getTheme());
            }
            UserConfig newUserConfig = userConfigService.saveUserConfig(config);
            refreshCacheForUserConfig(newUserConfig);
        }

    }

    @Override
    public void updateAvatar(String username, String avatar) {
        Long imageId = userImageService.findUserImageByName(avatar);
        if (null != imageId) {
            User user = getUser(username);
            if (user != null) {
                user.setAvatar(imageId.toString());
                saveUser(user);

                RMap<String, UserDTO> userMap = cacheService.getUserMap();
                if (userMap.containsKey(username)) {
                    UserDTO userDTO = userMap.get(username);
                    userDTO.setAvatar(avatar);
                    userMap.replace(username, userDTO);
                }
            } else {
                log.error("???????????????{}", username);
            }

        } else {
            log.error("????????????ID?????????:{}", avatar);
        }

    }

    @Override
    public void updateUser(UserDTO userDTO) {
        //???????????????
        UserDetail userDetail = userDetailService.findbyUserId(userDTO.getUserId());
        if (userDetail != null) {
            if (StringUtils.isNotBlank(userDTO.getPhone())) {
                userDetail.setPhone(userDTO.getPhone());
            }
            if (StringUtils.isNotBlank(userDTO.getEmail())) {
                userDetail.setEmail(userDTO.getEmail());
            }
            userDetailService.saveUserDetail(userDetail);
        }

        //????????????????????????
        User user = getUser(userDTO.getUserId());
        if (user != null) {
            if (null != userDTO.getDeptId()) {
                user.setDeptId(userDTO.getDeptId());
                if (userDetail != null) {
                    departmentService.saveUserDepartment(userDetail.getEmail(), userDTO.getDeptId());
                }
            }
            if (null != userDTO.getStatus()) {
                user.setStatus(userDTO.getStatus());
            }
            if (null != userDTO.getSex()) {
                user.setSex(userDTO.getSex());
            }
            saveUser(user);
        }
        //??????
        if (null != userDTO.getRoleId()) {
            Role role = roleRepository.findByRoleIdAndDeleted(userDTO.getRoleId(), false);
            if (role != null) {
                userRepository.deleteUserRole(userDTO.getUserId());
                userRepository.saveUserRole(userDTO.getUserId(), role.getRoleId());
            }
        }
        //??????,user role perms
        refreshCacheForUser(user, userDetail);
        //?????????
        getUserRoles(user.getUserName());
        //?????????
        getUserPermissions(user.getUserName());
    }

    @Override
    public List<User> getUsersByDeptAndIds(List<Long> ids, Integer deptId) {
        return userRepository.findByDeletedAndDeptIdAndUserIdIn(false, deptId, ids);
    }

    @Override
    public List<User> getUserByIds(List<Long> userIds) {
        return userRepository.findByDeletedAndUserIdIn(false, userIds);
    }

    @Override
    public UserDetail findById(String id) {
        return userDetailService.findById(id);
    }

    @Override
    public List<String> getAllUserCode() {
        List<UserDetail> all = userDetailService.findAll();
        if (all != null && all.size() != 0) {
            return all.stream().map(UserDetail::getId).distinct().collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<String> checkUserExcelContent(List<UserExcel> list) {
        List<String> errors = new ArrayList<>();
        //??????code??????
        long count = list.stream().map(UserExcel::getUserCode).distinct().count();
        if (count != list.size()) {
            errors.add("???????????????????????????????????????");
        }
        List<String> allUserCode = getAllUserCode();
        long userExistCount = list.stream().filter(u -> allUserCode.contains(u.getUserCode())).count();
        if (userExistCount > 0) {
            errors.add("?????????????????????????????????????????????");
        }

        //?????????????????????????????????????????????
        List<String> centers = list.stream().map(UserExcel::getCenter).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(centers)) {
            errors.add("???????????????????????????????????????");
        } else {
            Integer centerCount = departmentService.countCenterByNames(centers);
            if (centers.size() != centerCount) {
                errors.add("??????????????????????????????????????????");
            }
        }
        //????????????????????????
        long invalidEmailCount = list.stream().filter(d -> !d.getEmail().contains("@")).count();
        if (invalidEmailCount > 0) {
            errors.add("????????????????????????????????????????????????");
        }
        //??????
        long invalidSexCount = list.stream().filter(d -> !"???".equals(d.getSex()) && !"???".equals(d.getSex())).count();
        if (invalidSexCount > 0) {
            errors.add("????????????????????????????????????????????????(???|???)");
        }
        //????????????
        long invalidMarriageCount = list.stream().filter(d -> !"??????".equals(d.getMarriage()) && !"??????".equals(d.getMarriage())).count();
        if (invalidMarriageCount > 0) {
            errors.add("??????????????????????????????????????????????????????(??????|??????)");
        }
        //????????????
        //????????????????????????
        List<String> reporters = list.stream().map(UserExcel::getReporter).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(reporters)) {
            errors.add("???????????????????????????????????????");
        } else {
            Map<String, String> reporterMaps = getUserCodeNameMapByNames(reporters);
            if (reporters.size() != reporterMaps.keySet().size()) {
                errors.add("??????????????????????????????????????????????????????");
            }
        }

        return errors;
    }

    @Override
    public void saveUserReporter(List<UserExcel> list) {
        List<String> names = list.stream().map(UserExcel::getReporter).distinct().collect(Collectors.toList());
        Map<String, String> userCodeNameMap = getUserCodeNameMapByNames(names);
        Map<String, Set<String>> extraReportersMap = new HashMap<>();
        userCodeNameMap.forEach((userName, userCode) -> {
            Set<String> tmpSet = new HashSet<>();
            getExtraReporters(userCode, userName, tmpSet);
            extraReportersMap.put(userCode, tmpSet);
        });
        List<UserReporter> reporterList = new ArrayList<>();
        list.stream().collect(Collectors.groupingBy(d -> d.getUserCode() + StringPool.DASH + d.getChineseName(),
                        Collectors.mapping(UserExcel::getReporter, Collectors.toSet())))
                .forEach((userinfo, reporters) -> {
                    reporters.forEach(reporter -> {
                        if (userCodeNameMap.containsKey(reporter)) {
                            String reporterCode = userCodeNameMap.get(reporter);
                            String userCode = userinfo.split(StringPool.DASH)[0];
                            String userName = userinfo.split(StringPool.DASH)[1];
                            reporterList.add(new UserReporter(userCode, userName, reporterCode, reporter));

                            if (extraReportersMap.containsKey(reporterCode)) {
                                Set<String> moreReporters = extraReportersMap.get(reporterCode);
                                moreReporters.forEach(moreReporter -> {
                                    reporterList.add(new UserReporter(userCode, userName, moreReporter.split(StringPool.DASH)[0], moreReporter.split(StringPool.DASH)[1]));
                                });
                            }
                        }
                    });
                });
        userReporterRepository.saveAll(reporterList);
    }

    @Override
    public Map<String, String> getUserCodeNameMapByNames(List<String> names) {
        List<UserDetail> userDetails = userDetailService.findByChineseNameIn(names);
        if (!CollectionUtils.isEmpty(userDetails)) {
            return userDetails.stream().collect(Collectors.toMap(UserDetail::getChineseName, UserDetail::getId));
        }
        return Collections.emptyMap();
    }

    @Override
    public Integer findCompanyIdByUserName(String userName) {
        User user = userRepository.findByUserNameAndDeleted(userName, Boolean.FALSE);
        return Objects.nonNull(user) ? user.getCompanyId() : null;
    }

    @Override
    public List<Integer> getActiveCompanys() {
        return userRepository.findDistinctByCompanyIdAndDeleted();
    }

    private void refreshCacheForUserConfig(UserConfig userConfig) {
        RMap<Long, UserConfig> userConfigMap = cacheService.getUserConfigMap();
        if (userConfigMap.containsKey(userConfig.getUserId())) {
            userConfigMap.replace(userConfig.getUserId(), userConfig);
        } else {
            userConfigMap.put(userConfig.getUserId(), userConfig);
        }
    }

    private void cacheUser(User user) {
        RMap<String, UserDTO> userMap = cacheService.getUserMap();
        if (user != null) {
            UserDTO userDTO = EntityMapper.userRole2DTO(user);
            if (userDTO != null) {
                if (userMap.containsKey(userDTO.getUserName())) {
                    userMap.replace(userDTO.getUserName(), userDTO);
                } else {
                    userMap.put(userDTO.getUserName(), userDTO);
                }
            }
        }
    }

    private void getExtraReporters(String userCode, String chineseName, Set<String> reporters) {
        List<UserReporter> userReporters = userReporterRepository.findByUserCode(userCode);
        if (!CollectionUtils.isEmpty(userReporters)) {
            userReporters.forEach(userReporter -> {
                reporters.add(userReporter.getReporterCode() + StringPool.DASH + userReporter.getReporterName());
                if (!userReporter.getReporterCode().endsWith("00001")) {
                    getExtraReporters(userReporter.getReporterCode(), userReporter.getReporterName(), reporters);
                }
            });
        }
    }

    private List<User> findByDeletedAndCompanyId(Boolean deleted,Integer companyId){
         return userRepository.findAll((Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
             List<Predicate> predicateList = new ArrayList<>();
             predicateList.add(criteriaBuilder.equal(root.get("deleted").as(Boolean.class), deleted));
             if(Objects.nonNull(companyId)) {
                 predicateList.add(criteriaBuilder.equal(root.get("companyId").as(Integer.class),companyId));
             }
             Predicate[] predicates = new Predicate[predicateList.size()];
             return criteriaBuilder.and(predicateList.toArray(predicates));
         });
    }
}
