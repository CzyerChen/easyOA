package easyoa.leavemanager;

import easyoa.core.domain.po.user.Role;
import easyoa.core.repository.RoleRepository;
import easyoa.core.service.RoleService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by claire on 2019-06-28 - 15:41
 **/
public class RoleTest extends  AbstractApplicationTest{

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testRoleQuery(){
        Role role = roleService.findbyName("admin");
        Assert.assertNotNull(role);
    }

    @Test
    public void testDeleteRoles(){
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);

        Set<Role> set = roleRepository.findByDeletedAndRoleIdIn(false, list);
        set.forEach(r -> r.setDeleted(true));
        roleRepository.saveAll(set);
    }

    @Test
    public void testSaveRoleMenu(){
        roleService.saveRoleMenu("1,3,4,5",100);
    }
}
