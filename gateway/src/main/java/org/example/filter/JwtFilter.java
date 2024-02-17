package org.example.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.HttpHeaderConstant;
import org.example.jwt.JwtUtil;
import org.example.response.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class JwtFilter implements GlobalFilter, Ordered {

    @Autowired
    private ObjectMapper mapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private List<String> nonSecuredEndpoints = List.of(

    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var req = exchange
                .getRequest();

        var res = exchange
                .getResponse();

        Predicate<ServerHttpRequest> isNonSecuredEndpoint = (r) -> {
            var path = r.getURI().getPath();
            return nonSecuredEndpoints
                    .stream()
                    .anyMatch((s) -> s.contains(path));
        };

        if(isNonSecuredEndpoint.test(req)){
            return chain
                    .filter(exchange)
                    .then(Mono.fromRunnable(logResponse(exchange)));
        } else {
            try{
                var headers = req.getHeaders()
                        .get(HttpHeaderConstant.AUTHORIZATION);
                if(!headers.isEmpty()){
                    var header = headers
                            .get(0);
                    if(StringUtils.isBlank(header) || !header.startsWith("Bearer")){
                        var httpResponse = HttpResponse
                                .sendErrorResponse(
                                        "header 'Authorization' value is invalid",
                                        false
                                );
                        return res
                                .writeWith(getDataBuffer(res, httpResponse));
                    } else {
                        var token = header.substring(7);
                        var data = JwtUtil.getTokenData(this.jwtSecret, token);
                        exchange = exchange
                                .mutate()
                                .request(
                                        req
                                                .mutate()
                                                .header(HttpHeaderConstant.USER_ID, data.getId() + "")
                                                .build()
                                )
                                .build();
                        return chain
                                .filter(exchange)
                                .then(Mono.fromRunnable(logResponse(exchange)));
                    }
                } else {
                    var httpResponse = HttpResponse
                            .sendErrorResponse(
                                    "header 'Authorization' is required!",
                                    false
                            );
                    return res
                            .writeWith(getDataBuffer(res, httpResponse));
                }
            } catch(Exception ex){
                var httpResponse = HttpResponse
                        .sendErrorResponse(
                                String.format("exception occurred with message: %s", ex.getMessage()),
                                false
                        );
                return res
                        .writeWith(getDataBuffer(res, httpResponse));
            }
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @SneakyThrows
    private Mono<DataBuffer> getDataBuffer(ServerHttpResponse response, Object httpResponse){
        return Mono.just(
                response.bufferFactory()
                        .wrap(
                                this.mapper
                                        .writeValueAsBytes(httpResponse)
                        )
        );
    }

    private Runnable logResponse(ServerWebExchange exchange){
        return () -> {
            var postResponse = exchange.getResponse();
            var directedRoute = exchange.<URI>getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            log.info("get response: status-code-> {} from URI -> {}", postResponse.getStatusCode(), directedRoute);
        };
    }
}
