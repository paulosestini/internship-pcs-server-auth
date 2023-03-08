package com.poli.internship;

import com.poli.internship.data.entity.UserEntity;
import com.poli.internship.data.repository.UserRepository;
import com.poli.internship.domain.models.UserModel;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class UserTest {
    @Autowired
    private GraphQlTester tester;
    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void beforeEach(){
        this.repository.deleteAll();
    }

    @Test
    public void createUser() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("name", "Paulo");
        input.put("password", "abcd123");

        UserModel user = this.tester.documentName("createUser")
                .variable("input", input)
                .execute()
                .path("createUser")
                .entity(UserModel.class)
                .get();
        UserEntity userEntity = this.repository.findAll().iterator().next();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo(input.get("name"));
        assertThat(user).hasOnlyFields("id", "name");
        assertThat(userEntity.getId()).isEqualTo(Long.parseLong(user.getId()));
        assertThat(userEntity.getName()).isEqualTo(input.get("name"));
    }

    @Test
    public void getUser() {
        UserEntity userEntity = this.repository.save(new UserEntity("Paulo", "abcd1234"));
        String id = userEntity.getId().toString();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("id", id);

        UserModel user = this.tester.documentName("getUser")
                .variable("input", input)
                .execute()
                .path("getUser")
                .entity(UserModel.class)
                .get();

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo(userEntity.getName());
        assertThat(user).hasOnlyFields("id", "name");
    }

}
