package org.example.service;

import centwong.dubbo.clazz.DubboUserServiceTriple;
import centwong.dubbo.clazz.ListUser;
import centwong.dubbo.clazz.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.example.entity.User;
import org.example.entity.UserParam;
import org.example.jwt.JwtUtil;
import org.example.mapper.UserMapper;
import org.example.repository.IRepository;
import org.example.response.HttpResponse;
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
@DubboService
public class UserService
        extends DubboUserServiceTriple.UserServiceImplBase
        implements IService{

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

    @Override
    public centwong.dubbo.clazz.User get(centwong.dubbo.clazz.UserParam request) {
        var param = convertParamPbIntoParamClass(request);
        log.info("catch param dubbo get(): {}", param);

        var res = this.userRepository.get(param);

        var data = res.getData();

        return convertUserEntityIntoUserPb(data);
    }

    @Override
    public ListUser batchGet(centwong.dubbo.clazz.UserParam request) {
        var param = convertParamPbIntoParamClass(request);

        log.info("catch param dubbo batchGet(): {}", param);

        var res = this.userRepository.getList(param);

        var data = res.getData();

        var pg = res.getPg();

        return ListUser
                .newBuilder()
                .addAllUser(
                        data.stream()
                                .map(this::convertUserEntityIntoUserPb)
                                .toList()
                )
                .setPg(convertPgEntityIntoPgPb(pg))
                .build();
    }

    private UserParam convertParamPbIntoParamClass(centwong.dubbo.clazz.UserParam param){
        return UserParam
                .builder()
                .accountNumber(param.getAccountNumber())
                .accountNumbers(param.getAccountNumbersList())
                .isActive(param.getIsActive())
                .pgParam(
                        HttpResponse.PaginationParam
                                .builder()
                                .offset(param.getPgParam().getOffset())
                                .limit((int)param.getPgParam().getLimit())
                                .build()
                )
                .build();
    }

    private centwong.dubbo.clazz.User convertUserEntityIntoUserPb(User user){
        return centwong.dubbo.clazz.User
                .newBuilder()
                .setAccountNumber(user.getAccountNumber())
                .setPin(user.getPin())
                .setBalance(user.getBalance().longValue())
                .setIsActive(user.getIsActive())
                .setName(user.getName())
                .build();
    }

    private centwong.dubbo.clazz.Pagination convertPgEntityIntoPgPb(HttpResponse.Pagination pg){
        return Pagination
                .newBuilder()
                .setCurrentElement(pg.getCurrentElements())
                .setCurrentPage(pg.getCurrentPage())
                .setTotalElement(pg.getTotalElements())
                .setTotalPage(pg.getTotalPage())
                .build();
    }
}
