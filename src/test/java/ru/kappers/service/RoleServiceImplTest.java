package ru.kappers.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.kappers.AbstractDatabaseTest;
import ru.kappers.assertion.Assertions;
import ru.kappers.model.Role;
import ru.kappers.repository.RolesRepository;
import ru.kappers.service.impl.RolesServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

@ContextConfiguration(classes = RoleServiceImplTest.Configuration.class)
public class RoleServiceImplTest extends AbstractDatabaseTest {
    @Autowired
    private RolesService rolesService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void getRoleNameById() {
        Role role = rolesService.getById(1);
        Assertions.assertThat(role).isNotNull()
                .hasName(Role.Names.ADMIN);
    }

    @Test
    public void getRoleIdByName() {
        int roleId = rolesService.getRoleIdByName(Role.Names.USER);
        assertThat(roleId).isEqualTo(2);
    }

    @Test
    public void getRoleByName() {
        Role role = rolesService.getByName(Role.Names.USER);
        Assertions.assertThat(role).hasId(2);
    }

    @Test
    public void getById() {
        Role role = rolesService.getById(2);
        Assertions.assertThat(role).hasName(Role.Names.USER);
    }

    @Test
    public void testAddDelAndEdit() {
        deleteFromTables(jdbcTemplate, "users", "roles");

        Role role = createRoleWithName("ROLE_TEST");
        Role backRole = rolesService.addRole(role);
        Assertions.assertThat(backRole).isNotNull()
                .hasName(role.getName());

        final String newName = "ROLE_TESTED";
        backRole = rolesService.editRole(backRole.toBuilder()
                .name(newName)
                .build());
        Assertions.assertThat(backRole).hasName(newName);
        rolesService.delete(backRole);
        backRole = rolesService.getByName(newName);
        Assertions.assertThat(backRole).isNull();
    }

    private Role createRoleWithName(String name) {
        return Role.builder()
                .name(name)
                .enabled(true)
                .build();
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public RolesService rolesService(RolesRepository repository) {
            return new RolesServiceImpl(repository);
        }
    }
}