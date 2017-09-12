package org.koenighotze.txprototype.user.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.vavr.collection.*;
import io.vavr.control.*;
import org.junit.*;
import org.junit.runner.*;
import org.koenighotze.txprototype.user.*;
import org.koenighotze.txprototype.user.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.annotation.*;
import org.springframework.test.context.junit4.*;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(classes = UserAdministrationApplication.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Before
    public void initDb() {
        userRepository.deleteAll();

        userRepository.save(new User("david", "David", "Schmitz", "dschmitz", "dschmitz@foo.bar"));
    }

    @Test
    public void the_repository_returns_all_users_by_lastname() {
        Seq<User> schmitze = userRepository.findByLastname("Schmitz");

        assertThat(schmitze).hasSize(1);
    }

    @Test
    public void the_repository_finds_a_user_by_publicid() {
        Option<User> result = userRepository.findByPublicId("david");

        assertThat(result.isDefined()).isTrue();
        assertThat(result.get().getLastname()).isEqualTo("Schmitz");
    }
}