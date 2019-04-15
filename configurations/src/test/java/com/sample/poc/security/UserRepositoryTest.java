package com.sample.poc.security;

import com.sample.poc.repository.UserRepository;
import com.sample.poc.model.AppUser;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;


    @After
    public void clearDb(){
        userRepository.deleteAll();
    }

    @Test
    public void whenFindByUserName_thenReturnAppUser() {

        AppUser user =  getUser("test");
        entityManager.persist(user);
        entityManager.flush();

        AppUser found = userRepository.findByUsername(user.getUsername());

        assertThat(found.getUsername()).isEqualTo(user.getUsername());
        assertThat(found.getPassword()).isEqualTo(user.getPassword());
    }


    private AppUser getUser(String name) {
        AppUser user = new AppUser();
        user.setPassword(name);
        user.setUsername(name + "_password");
        return user;
    }
}
