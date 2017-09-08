package com.lewis.readwriteisolation.service;


import com.lewis.readwriteisolation.entity.User;

import java.util.List;

public interface UserService {

    List<User> testWriteAndRead(User user);
    
    public boolean insert(User u);
    
    public List<User> findAll();
    
    public List<User> findByUserIds(List<Integer> ids);
    
    public void transactionTestSucess();
    
    public void transactionTestFailure() throws IllegalAccessException;

}
