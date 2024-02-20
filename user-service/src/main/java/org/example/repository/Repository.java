package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.entity.UserParam;
import org.example.mapper.UserMapper;
import org.example.response.HttpResponse;
import org.example.response.RepositoryData;
import org.example.util.QueryUtil;

import java.util.List;

@org.springframework.stereotype.Repository
@Slf4j
public class Repository implements IRepository{

    @PersistenceContext
    private EntityManager em;

    private final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public RepositoryData<User> save(User user) {
        this.em.persist(user);
        log.info("successfully save user");
        return RepositoryData
                .<User>builder()
                .data(user)
                .build();
    }

    @Override
    public RepositoryData<User> get(UserParam param) {
        var query = QueryUtil
                .generateQuery(em, User.class, param);

        var res = query.getSingleResult();

        log.info("catch get result: {}", res);

        return RepositoryData
                .<User>builder()
                .data(res)
                .build();
    }

    @Override
    public RepositoryData<List<User>> getList(UserParam param) {
        var query = QueryUtil
                .generateQuery(em, User.class, param);

        var queryCount = QueryUtil
                .generateQueryCount(em, User.class, param);

        var res = query.getResultList();
        var resCount = queryCount.getSingleResult();

        log.info("catch getList result: {}", res);
        log.info("catch getList count: {}", resCount);

        Long totalPage = null;

        if(param.getPgParam().getLimit() != null){
            totalPage = (long)(res.size() / param.getPgParam().getLimit()) + 1;
        }

        var pg =  HttpResponse.Pagination
                .builder()
                .currentPage(param.getPgParam().getOffset())
                .currentElements((long)res.size())
                .totalPage(totalPage)
                .totalElements(resCount)
                .build();

        return RepositoryData
                .<List<User>>builder()
                .data(res)
                .pg(
                       param.getPgParam().getLimit() == null
                        &&
                       param.getPgParam().getOffset() == null
                        ?
                       null : pg
                )
                .build();
    }

    @Override
    public void update(User data) {

        this.em.persist(data);

        log.info("successfully update user data");
    }
}
