package org.example.repository;

import org.example.entity.Card;
import org.example.entity.CardParam;
import org.example.response.RepositoryData;

import java.util.List;

public interface IRepository {
    RepositoryData<Card> save(Card card);
    RepositoryData<Card> get(CardParam param);
    RepositoryData<List<Card>> getList(CardParam param);
    void update(Card updatedData);
}
