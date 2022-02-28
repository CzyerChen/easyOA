package easyoa.leavemanager;

import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.dto.UserLoginDTO;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.User;
import easyoa.core.repository.UserRepository;
import easyoa.core.service.UserService;
import easyoa.leavemanager.domain.user.UserReporter;
import easyoa.leavemanager.repository.user.UserReporterRepository;
import easyoa.leavemanager.service.CacheService;
import easyoa.leavemanager.service.redisson.RedissonService;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.api.RScoredSortedSet;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by claire on 2019-06-24 - 09:33
 **/
public class UserTest extends AbstractApplicationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private RedissonService redissonService;
    @Autowired
    private UserReporterRepository userReporterRepository;

    @Test
    public void testGetUser() {
        UserDTO user = userService.getUserDto("admin");
        Assert.assertNotNull(user);
        Set<String> roles = userService.getUserRoles("admin");
        Assert.assertNotNull(roles);
        Set<String> permissions = userService.getUserPermissions("admin");
        Assert.assertNotNull(permissions);
    }

    @Test
    public void findUserIdsByMenu() {
        List<Long> userIds = userService.getUserIdsForMenu(1);
        Assert.assertNotNull(userIds);
    }

    @Test
    public void testSaveUserRole() {
        userRepository.saveUserRole(2L, 2);
    }

    @Test
    public void testQueryUserRole() {
        List<BigInteger> userIdsForRole = userService.getUserIdsForRole(2);
        Assert.assertNotNull(userIdsForRole);
    }

    @Test
    public void testQueryUserRoles() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        List<Long> userIdsForRoles = userService.getUserIdsForRoles(list);
        Assert.assertNotNull(userIdsForRoles);
    }

    @Test
    public void testDeleteUserRole() {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);

        userRepository.deleteUserRole(list);
        //userRepository.test1(list);
    }

    @Test
    public void testLogout() {
        RScoredSortedSet<UserLoginDTO> activeUserSet = cacheService.getActiveUserSet();
        String now = DateUtil.formatFullTime(LocalDateTime.now());
        Collection<ScoredEntry<UserLoginDTO>> scoredEntries = activeUserSet.entryRange(Double.valueOf(now), true, Double.MAX_VALUE, true);
        Collection<UserLoginDTO> loginUserListByRange = cacheService.getLoginUserListByRange(Double.valueOf(now), Double.MAX_VALUE);
        Assert.assertNotNull(scoredEntries);
    }


    @Test
    public void testFindRoleIds() {
        List<Long> list = new ArrayList<>();
        list.add(7L);
        list.add(4L);
        List<User> users = userRepository.findByDeletedAndUserIdIn(false, list);
        Assert.assertNotNull(users);
    }

    @Test
    public void testFindUser() {
        User user = userRepository.findByUserIdAndDeleted(14L, false);
        Assert.assertNotNull(user);
    }

    @Test
    public void testDownload() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        /**
         getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
         */
        URL url = classLoader.getResource("easypoiExample.xls");
        /**
         * url.getFile() 得到这个文件的绝对路径
         */
        System.out.println(url.getFile());
        File file = new File(url.getFile());
        System.out.println(file.exists());
    }


}
