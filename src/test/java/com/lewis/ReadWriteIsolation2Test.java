package com.lewis;

import com.lewis.readwriteisolation.entity.User;
import com.lewis.readwriteisolation.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/spring/readwriteisolation2/spring-datasource.xml", "classpath:config/spring/readwriteisolation2/spring-mybatis-config.xml"})
public class ReadWriteIsolation2Test {

    @Resource
    public UserService userService;

    @Test
    public void testUserInsert() {
        User u = new User();
        u.setUserId(17);
        u.setAge(25);
        u.setName("war3");
        Assert.assertEquals(userService.insert(u), true);

        List<User> all = userService.findAll();
        System.out.println(all.toString());
    }
}
