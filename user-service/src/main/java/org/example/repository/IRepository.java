package org.example.repository;

import org.example.entity.User;
import org.example.entity.UserParam;
import org.example.response.RepositoryData;

import java.util.List;

public interface IRepository {
    RepositoryData<User> save(User user);
    RepositoryData<User> get(UserParam param);
    RepositoryData<List<User>> getList(UserParam param);
    void update(User data);
}
