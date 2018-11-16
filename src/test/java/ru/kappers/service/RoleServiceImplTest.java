package ru.kappers.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import lombok.extern.log4j.Log4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kappers.KappersApplication;
import ru.kappers.model.Role;


@Log4j
@ActiveProfiles("test")
@ContextConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KappersApplication.class})
@TestExecutionListeners({DbUnitTestExecutionListener.class})
public class RoleServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private RolesService rolesService;

    @Test
    public void getRoleNameById() {
        Role roleNameById = rolesService.getById(1);
        Assert.assertNotNull(roleNameById);
        Assert.assertEquals(roleNameById.getName(), "ROLE_ADMIN");
    }

    @Test
    public void getRoleIdByName() {
        System.out.println(rolesService);
        int roleId = rolesService.getRoleIdByName("ROLE_USER");
        Assert.assertEquals(roleId, 2);
    }

    @Test
    public void getRoleByName() {
        Role role = rolesService.getByName("ROLE_USER");
        Assert.assertEquals(role.getId(), 2);
    }

    @Test
    public void getById() {
        Role role = rolesService.getById(2);
        Assert.assertEquals(role.getName(), "ROLE_USER");
    }

    @Test
    public void testAddDelAndEdit() {
        Role role = Role.builder().name("ROLE_TEST").enabled(true).build();
        Role backRole = rolesService.addRole(role);
        Assert.assertNotNull(backRole);
        Assert.assertEquals(backRole.getName(), role.getName());
        final String newName = "ROLE_TESTED";
        role = backRole;
        role.setName(newName);
        backRole = rolesService.editRole(role);
        Assert.assertEquals(backRole.getName(), newName);
        rolesService.delete(backRole);
        backRole = rolesService.getByName(newName);
        Assert.assertNull(backRole);
    }

}