package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.constant.ContextConstant;
import org.example.constant.HttpHeaderConstant;
import org.example.entity.User;
import org.example.entity.UserParam;
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
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private IService service;

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> save(
            HttpServletRequest req,
            @RequestBody User user
    ){
        var initialTime = LocalDateTime.now();
        var res = this.service.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        HttpResponse.sendSuccessResponse(
                                Context
                                        .of(ContextConstant.TIME_START, initialTime)
                                        .put(ContextConstant.REQUEST_PATH, req.getRequestURI()),
                                HttpStatus.CREATED,
                                "successfully create user",
                                res.getData(),
                                null,
                                null
                        )
                );
    }

    @PostMapping(
            value = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> login(
            HttpServletRequest req,
            @RequestBody User user
    ){
        var initialTime = LocalDateTime.now();
        var res = this.service.login(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        HttpResponse.sendSuccessResponse(
                                Context
                                        .of(ContextConstant.TIME_START, initialTime)
                                        .put(ContextConstant.REQUEST_PATH, req.getRequestURI()),
                                HttpStatus.OK,
                                "user authenticated",
                                res.getData(),
                                null,
                                res.getMetadata()
                        )
                );
    }

    @GetMapping(
            value = "/{accountNumber}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> get(
            HttpServletRequest req,
            @PathVariable(value = "accountNumber") String accountNumber
    ){
        var initialTime = LocalDateTime.now();
        var res = this.service.get(
                UserParam
                        .builder()
                        .accountNumber(accountNumber)
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
                                "successfully get user",
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
            @RequestParam(value = "accountNumber", required = false) String accountNumber,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Long offset
    ){
        var initialTime = LocalDateTime.now();
        var res = this.service.getList(
                UserParam
                        .builder()
                        .accountNumber(accountNumber)
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
                                "successfully get user",
                                res.getData(),
                                res.getPg(),
                                null
                        )
                );
    }

    @PutMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> update(
            HttpServletRequest req,
            @RequestParam(value = "accountNumber", required = false) String accountNumber,
            @RequestBody User user
    ){
        var initialTime = LocalDateTime.now();
        this.service.update(
                UserParam
                        .builder()
                        .accountNumber(accountNumber)
                        .build(),
                user
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

    @DeleteMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> delete(
            HttpServletRequest req
    ){
        var initialTime = LocalDateTime.now();
        var accountNumber = req.getHeader(HttpHeaderConstant.USER_ID);
        this.service.delete(
                UserParam
                        .builder()
                        .accountNumber(accountNumber)
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
                                "successfully delete user",
                                null,
                                null,
                                null
                        )
                );
    }

    @PatchMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<HttpResponse> activate(
            HttpServletRequest req
    ){
        var initialTime = LocalDateTime.now();
        var accountNumber = req.getHeader(HttpHeaderConstant.USER_ID);
        this.service.activate(
                UserParam
                        .builder()
                        .accountNumber(accountNumber)
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
                                "successfully activate user",
                                null,
                                null,
                                null
                        )
                );
    }
}
