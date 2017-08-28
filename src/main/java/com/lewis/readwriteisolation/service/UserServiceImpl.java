package com.lewis.readwriteisolation.service;

import com.lewis.readwriteisolation.entity.User;
import com.lewis.readwriteisolation.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    public boolean insert(User u) {
        return userMapper.insert(u) > 0 ? true : false;
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public List<User> findByUserIds(List<Integer> ids) {
        return userMapper.findByUserIds(ids);
    }

    public void transactionTestSucess() {

    }

    public void transactionTestFailure() throws IllegalAccessException {

    }
}
