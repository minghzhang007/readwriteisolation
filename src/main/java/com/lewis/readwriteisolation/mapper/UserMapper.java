package com.lewis.readwriteisolation.mapper;


import com.lewis.readwriteisolation.core.RoutingDB;
import com.lewis.readwriteisolation.core.enums.RoutingEnum;
import com.lewis.readwriteisolation.entity.User;

import java.util.List;

public interface UserMapper {

    @RoutingDB(routingType = RoutingEnum.MASTER)
    Integer insert(User u);

    @RoutingDB(routingType = RoutingEnum.SLAVE)
    List<User> findAll();

    @RoutingDB(routingType = RoutingEnum.SLAVE)
    List<User> findByUserIds(List<Integer> userIds);
    

}
