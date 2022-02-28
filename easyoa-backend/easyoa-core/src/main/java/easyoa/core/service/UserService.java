package easyoa.core.service;

import easyoa.core.domain.dto.UserExcel;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.common.domain.vo.UserSearch;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.po.user.Menu;
import easyoa.core.domain.po.user.User;
import easyoa.core.domain.po.user.UserConfig;
import easyoa.core.domain.po.user.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by claire on 2019-06-24 - 08:32
 **/
public interface UserService {

  UserDTO getUserDto(String username);

  User getUser(String username);

  User getUserByCode(String userCode);

  User getUser(Long userId);

  List<UserDTO> getUsers();

  List<UserDTO> getUsers(Integer companyId);

  Page<UserDTO> getPageUsers(Pageable pageable);

  Page<UserDTO> getPageUsersWithSearchParam(Pageable pageable, UserSearch userSearch);

  Set<String> getUserRoles(String username);

  Set<String> getUserPermissions(String username);

  Map<String,Set<String>> getUserRoles(Set<Long> userIds);

  Map<String,Set<String>> getUserPermissions(Set<Long> userIds);

  UserConfig getUserConfig(long userId);

  Map<String, Object> initUserInfoVO(User user,String token,String expirtAt);

  List<Long> getUserIdsForMenu(int menuId);

  List<BigInteger> getUserIdsForRole(int roleId);

  List<String> getUserNamesForRole(int roleId);

  List<Long> getUserIdsForRoles(List<Integer> roles);

  List<Menu> getUserMenus(String username);

  Boolean checkUsernameExist(String username);

  Boolean registUser(String username,String password,Integer roleId,Integer deptId,Integer companyId,Character sex,Integer status)throws BussinessException;

  void saveUserPostion(List<UserExcel> list);

  void saveUserDepartment(List<UserExcel> list);

  void saveUserDetail(List<UserExcel> list);

  UserDetail saveUserDetail(UserDetail userDetail);

  User saveUser(User user);

  void refreshCacheForRoleAndPerms(Map<String, Set<String>> userRoles, Map<String, Set<String>> userPerms);

  String getUserNameById(Long userId);

  void removeActiveUser(UserLoginDTO userLoginDTO);

  void removeUserToken(String key);

  void removeUser(String[] userIds);

  void removeUserRole(List<Integer> roleIds);

  UserDetail findUserDetailByUserId(Long userId);

  void refreshCacheForUser(User user,UserDetail userDetail);

  String getUserLastLoginTime(String username);

  UserDTO getCurrentUserInfo(String username);

  void updatePassword(String username,String password);

  void resetPassword(String[] usernames);

  void updateUserConfig(UserConfig userConfig);

  void updateAvatar(String username,String avatar);

  void updateUser(UserDTO userDTO);

  List<User> getUsersByDeptAndIds(List<Long> ids,Integer deptId);

  List<User> getUserByIds(List<Long> userIds);

  UserDetail findById(String id);

  List<String> getAllUserCode();

  List<String> checkUserExcelContent( List<UserExcel> list);

  void saveUserReporter( List<UserExcel> list);

  Map<String,String> getUserCodeNameMapByNames(List<String> names);

  Integer findCompanyIdByUserName(String userName);

  List<Integer> getActiveCompanys();
}
