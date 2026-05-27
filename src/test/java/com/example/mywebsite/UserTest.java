package com.example.mywebsite;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void constructorSetsFields() {
        User user = new User("alice", "secret");
        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getPassword()).isEqualTo("secret");
    }

    @Test
    void defaultValues() {
        User user = new User();
        assertThat(user.getIsAdmin()).isEqualTo(0);
        assertThat(user.getIsDeleted()).isEqualTo(0);
    }

    @Test
    void settersAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");
        user.setPassword("pwd");
        user.setIsAdmin(1);
        user.setIsDeleted(1);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("bob");
        assertThat(user.getPassword()).isEqualTo("pwd");
        assertThat(user.getIsAdmin()).isEqualTo(1);
        assertThat(user.getIsDeleted()).isEqualTo(1);
    }
}