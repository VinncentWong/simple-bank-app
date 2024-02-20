package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Card;
import org.example.entity.CardParam;
import org.example.response.HttpResponse;
import org.example.response.RepositoryData;
import org.example.util.QueryUtil;

import java.util.List;

@org.springframework.stereotype.Repository
@Slf4j
public class Repository implements IRepository{

    @PersistenceContext
    private EntityManager em;

    @Override
    public RepositoryData<Card> save(Card card) {
        em.persist(card);
        log.info("successfully insert card data");
        return RepositoryData
                .<Card>builder()
                .data(card)
                .build();
    }

    @Override
    public RepositoryData<Card> get(CardParam param) {
        var res = QueryUtil
                .generateQuery(em, Card.class, param);

        var data = res.getSingleResult();
        log.info("get card data: {}", data);

        return RepositoryData
                .<Card>builder()
                .data(data)
                .build();
    }

    @Override
    public RepositoryData<List<Card>> getList(CardParam param) {
        var res = QueryUtil
                .generateQuery(em, Card.class, param);

        var resCount = QueryUtil
                .generateQueryCount(em, Card.class, param);

        var data = res.getResultList();
        var count = resCount.getSingleResult();

        log.info("getList data: {}", data);
        log.info("count data: {}", count);

        Long totalPage = null;

        if(param.getPgParam().getLimit() != null){
            totalPage = (count / param.getPgParam().getLimit()) + 1;
        }

        return RepositoryData
                .<List<Card>>builder()
                .data(data)
                .pg(
                        HttpResponse.Pagination
                                .builder()
                                .currentPage(param.getPgParam().getOffset())
                                .currentElements((long)data.size())
                                .totalElements(count)
                                .totalPage(totalPage)
                                .build()
                )
                .build();
    }

    @Override
    public void update(Card updatedData) {
        this.em.persist(updatedData);
        log.info("successfully update data");
    }
}
