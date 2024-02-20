package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.constant.ContextConstant;
import org.example.constant.HttpHeaderConstant;
import org.example.entity.Card;
import org.example.entity.CardParam;
import org.example.response.HttpResponse;
import org.example.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.util.context.Context;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/card")
public class CardController {

    @Autowired
    private IService service;

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> save(
            HttpServletRequest req,
            @RequestBody Card card
    ){
        var initialTime = LocalDateTime.now();
        var accountNumber = req.getHeader(HttpHeaderConstant.USER_ID);
        var res = this.service.save(accountNumber, card);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        HttpResponse.sendSuccessResponse(
                                Context
                                        .of(ContextConstant.TIME_START, initialTime)
                                        .put(ContextConstant.REQUEST_PATH, req.getRequestURI()),
                                HttpStatus.CREATED,
                                "successfully create card",
                                res.getData(),
                                null,
                                null
                        )
                );
    }

    @GetMapping(
            value = "/id",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> save(
            HttpServletRequest req,
            @PathVariable Long id
    ){
        var initialTime = LocalDateTime.now();
        var res = this.service.get(
                CardParam
                        .builder()
                        .id(id)
                        .build()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        HttpResponse.sendSuccessResponse(
                                Context
                                        .of(ContextConstant.TIME_START, initialTime)
                                        .put(ContextConstant.REQUEST_PATH, req.getRequestURI()),
                                HttpStatus.OK,
                                "successfully get card",
                                res.getData(),
                                null,
                                null
                        )
                );
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> getList(
            HttpServletRequest req,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "fkUserId", required = false) Long fkUserId,
            @RequestParam(value = "fkUserIds", required = false) List<Long> fkUserIds,
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
            @RequestParam(value = "cardNumbers", required = false) List<String> cardNumbers,
            @RequestParam(value = "cardType", required = false) String cardType,
            @RequestParam(value = "cardTypes", required = false) List<String> cardTypes,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Long offset
    ){
        var initialTime = LocalDateTime.now();
        var res = this.service.getList(
                CardParam
                        .builder()
                        .id(id)
                        .ids(ids)
                        .isActive(isActive)
                        .cardNumber(cardNumber)
                        .cardNumbers(cardNumbers)
                        .cardType(cardType)
                        .cardTypes(cardTypes)
                        .fkUserId(fkUserId)
                        .fkUserIds(fkUserIds)
                        .pgParam(HttpResponse.PaginationParam
                                .builder()
                                .limit(limit)
                                .offset(offset)
                                .build())
                        .build()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        HttpResponse.sendSuccessResponse(
                                Context
                                        .of(ContextConstant.TIME_START, initialTime)
                                        .put(ContextConstant.REQUEST_PATH, req.getRequestURI()),
                                HttpStatus.OK,
                                "successfully get card",
                                res.getData(),
                                res.getPg(),
                                null
                        )
                );
    }

    @PutMapping(
            value = "/id",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> update(
            HttpServletRequest req,
            @PathVariable("id") Long id,
            @RequestBody Card card
    ){
        var initialTime = LocalDateTime.now();
        this.service.update(
                CardParam
                        .builder()
                        .id(id)
                        .build(),
                card
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        HttpResponse.sendSuccessResponse(
                                Context
                                        .of(ContextConstant.TIME_START, initialTime)
                                        .put(ContextConstant.REQUEST_PATH, req.getRequestURI()),
                                HttpStatus.OK,
                                "successfully update user",
                                null,
                                null,
                                null
                        )
                );
    }
}
