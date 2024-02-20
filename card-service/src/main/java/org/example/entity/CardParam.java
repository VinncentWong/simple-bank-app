package org.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.annotation.ParamColumn;
import org.example.response.HttpResponse;

import java.util.List;

@Builder
@Setter
@Getter
@ToString
public class CardParam {

    private Long id;

    @ParamColumn(name = "id")
    private List<Long> ids;

    @ParamColumn(name = "card_number")
    private String cardNumber;

    @ParamColumn(name = "card_number")
    private List<String> cardNumbers;

    @ParamColumn(name = "card_type")
    private String cardType;

    @ParamColumn(name = "card_type")
    private List<String> cardTypes;

    @ParamColumn(name = "fk_user_id")
    private Long fkUserId;

    @ParamColumn(name = "fk_user_id")
    private List<Long> fkUserIds;

    @ParamColumn(name = "is_active")
    private Boolean isActive;

    private HttpResponse.PaginationParam pgParam;
}
