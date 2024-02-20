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
public class UserParam {

    @ParamColumn(name = "account_number")
    private String accountNumber;

    @ParamColumn(name = "account_number")
    private List<String> accountNumbers;

    private HttpResponse.PaginationParam pgParam;
}
