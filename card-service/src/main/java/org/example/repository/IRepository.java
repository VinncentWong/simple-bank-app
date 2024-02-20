package org.example.repository;

import org.example.entity.Card;
import org.example.entity.CardParam;
import org.example.response.RepositoryData;

public interface IRepository {
    RepositoryData<Card> save(Card card);
    RepositoryData<Card> get(CardParam param);
    RepositoryData<Card> getList(CardParam param);
    void update(CardParam param, Card updatedData);
}
