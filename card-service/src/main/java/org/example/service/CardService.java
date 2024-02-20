package org.example.service;

import centwong.dubbo.clazz.UserParam;
import centwong.dubbo.clazz.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.example.entity.Card;
import org.example.entity.CardParam;
import org.example.mapper.CardMapper;
import org.example.repository.IRepository;
import org.example.response.ServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CardService implements IService{

    @Autowired
    private IRepository repository;

    @DubboReference
    private UserService userService;

    private final CardMapper mapper = CardMapper.INSTANCE;

    @Override
    public ServiceData<Card> save(String accountNumber, Card card) {
        var user = this.userService
                .get(
                        UserParam
                                .newBuilder()
                                .addAccountNumbers(accountNumber)
                                .build()
                );

        card.setIsActive(true);
        card.setCardType(card.getCardType() + String.format("_%s_%s", user.getName(), user.getBalance()));

        var data = this.repository.save(card);
        return ServiceData
                .<Card>builder()
                .data(data.getData())
                .build();
    }

    @Override
    public ServiceData<Card> get(CardParam param) {
        var data = this.repository.get(param);

        return ServiceData
                .<Card>builder()
                .data(data.getData())
                .build();
    }

    @Override
    public ServiceData<List<Card>> getList(CardParam param) {
        var data = this.repository.getList(param);

        return ServiceData
                .<List<Card>>builder()
                .data(data.getData())
                .pg(data.getPg())
                .build();
    }

    @Override
    public void update(CardParam param, Card updateData) {
        var dataToBeFind = this.repository.get(param).getData();

        var updatedData = this.mapper.update(updateData, dataToBeFind);

        this.repository.update(updatedData);
    }

    @Override
    public void activate(CardParam param) {
        var dataToBeFind = this.repository.get(param).getData();

        dataToBeFind.setIsActive(true);
        dataToBeFind.setDeletedAt(null);

        this.repository.update(dataToBeFind);
    }

    @Override
    public void delete(CardParam param) {
        var dataToBeFind = this.repository.get(param).getData();

        dataToBeFind.setIsActive(false);
        dataToBeFind.setDeletedAt(LocalDateTime.now());

        this.repository.update(dataToBeFind);
    }
}
