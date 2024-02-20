package org.example.repository;

import org.example.entity.Card;
import org.example.entity.CardParam;
import org.example.response.RepositoryData;

@org.springframework.stereotype.Repository
public class Repository implements IRepository{


    @Override
    public RepositoryData<Card> save(Card card) {
        return null;
    }

    @Override
    public RepositoryData<Card> get(CardParam param) {
        return null;
    }

    @Override
    public RepositoryData<Card> getList(CardParam param) {
        return null;
    }

    @Override
    public void update(CardParam param, Card updatedData) {

    }
}
