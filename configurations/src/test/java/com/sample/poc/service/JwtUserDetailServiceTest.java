package com.sample.poc.service;


import com.sample.poc.repository.UserRepository;
import com.sample.poc.model.AppUser;
import com.sample.poc.service.UsersDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class JwtUserDetailServiceTest {

    @Autowired
    private UsersDetailsService userService;

    @MockBean
    private UserRepository userRepository;


    @TestConfiguration
    static class JwtUserDetailServiceTestContextConfiguration {
        @Bean
        public UsersDetailsService jwtUserDetailsService() {
            return new UsersDetailsService();
        }
    }


    @Before
    public void setUp() {
        AppUser user = new AppUser();
        user.setUsername("user");
        user.setPassword("password");
        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(user);
    }



    @Test
    public void whenValidName_thenUserFound() {
        String name = "user";
        UserDetails found = userService.loadUserByUsername(name);
        assertThat(found.getUsername()).isEqualTo(name);

    }


    @Test(expected= UsernameNotFoundException.class)
    public void whenNotValidName_thenThrowException() {
        String name = "user1";
        UserDetails found = userService.loadUserByUsername(name);
        fail();
    }


}
