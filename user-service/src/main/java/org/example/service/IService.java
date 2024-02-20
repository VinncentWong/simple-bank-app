package org.example.service;

import org.example.entity.User;
import org.example.entity.UserParam;
import org.example.response.ServiceData;

import java.util.List;

public interface IService {
    ServiceData<User> save(User user);
    ServiceData<User> login(User user);
    ServiceData<User> get(UserParam param);
    ServiceData<List<User>> getList(UserParam param);
    void update(UserParam param, User data);
    void activate(UserParam param);
    void delete(UserParam param);
}
