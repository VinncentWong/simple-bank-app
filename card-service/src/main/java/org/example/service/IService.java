package org.example.service;

import org.example.entity.Card;
import org.example.entity.CardParam;
import org.example.response.ServiceData;

import java.util.List;

public interface IService {
    ServiceData<Card> save(String accountNumber, Card card);
    ServiceData<Card> get(CardParam param);
    ServiceData<List<Card>> getList(CardParam param);
    void update(CardParam param, Card updateData);
    void activate(CardParam param);
    void delete(CardParam param);
}
