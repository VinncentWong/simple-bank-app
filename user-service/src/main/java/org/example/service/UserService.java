package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.entity.UserParam;
import org.example.jwt.JwtUtil;
import org.example.mapper.UserMapper;
import org.example.repository.IRepository;
import org.example.response.ServiceData;
import org.example.util.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserService implements IService{

    @Autowired
    private IRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public ServiceData<User> save(User user) {

        log.info("accepting user data on save: {}", user);

        user.setAccountNumber(RandomStringUtils.randomNumeric(10));
        user.setPin(BcryptUtil.encode(user.getPin()));
        user.setIsActive(true);

        var res = this.userRepository
                .save(user);

        return ServiceData
                .<User>builder()
                .data(res.getData())
                .build();
    }

    @Override
    public ServiceData<User> login(User user) {
        var res = this.userRepository
                .get(
                        UserParam
                                .builder()
                                .accountNumber(user.getAccountNumber())
                                .build()
                );

        if(BcryptUtil.isMatch(user.getPin(), res.getData().getPin())){
            var jwtToken = JwtUtil.generateJwtToken(jwtSecret, "ROLE_USER", res.getData().getAccountNumber());
            return ServiceData
                    .<User>builder()
                    .data(res.getData())
                    .metadata(jwtToken)
                    .build();
        } else {
            throw new RuntimeException("unauthorized exception, pin or account number doesn't match!");
        }
    }

    @Override
    public ServiceData<User> get(UserParam param) {
        log.info("accepting user param on get: {}", param);

        var res = this.userRepository
                .get(param);

        return ServiceData
                .<User>builder()
                .data(res.getData())
                .build();
    }

    @Override
    public ServiceData<List<User>> getList(UserParam param) {
        log.info("accepting user param on getList: {}", param);

        var res = this.userRepository
                .getList(param);

        return ServiceData
                .<List<User>>builder()
                .data(res.getData())
                .pg(res.getPg())
                .build();
    }

    @Override
    public void update(UserParam param, User data) {

        log.info("accepting update param: {}", param);

        var res = this.userRepository
                .get(param);

        var updatedData = this.mapper
                .update(data, res.getData());

        this.userRepository.update(updatedData);

        log.info("success update user");
    }

    @Override
    public void activate(UserParam param) {
        log.info("accepting activate param: {}", param);

        var res = this.userRepository
                .get(param);

        var updateData = User
                .builder()
                .deletedAt(null)
                .isActive(true)
                .build();

        var updatedData = this.mapper
                .update(updateData, res.getData());

        this.userRepository.update(updatedData);
    }

    @Override
    public void delete(UserParam param) {
        log.info("accepting activate param: {}", param);

        var res = this.userRepository
                .get(param);

        var updateData = User
                .builder()
                .deletedAt(LocalDateTime.now())
                .isActive(false)
                .build();

        var updatedData = this.mapper
                .update(updateData, res.getData());

        this.userRepository.update(updatedData);
    }
}
