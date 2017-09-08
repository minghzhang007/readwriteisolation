package com.lewis.readwriteisolation.service;

import com.lewis.readwriteisolation.entity.User;
import com.lewis.readwriteisolation.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public List<User> testWriteAndRead(User user) {
        userMapper.insert(user);
        List<User> all = userMapper.findAll();
        return all;
    }

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
