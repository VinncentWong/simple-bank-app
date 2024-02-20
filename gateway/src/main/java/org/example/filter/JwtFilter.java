package org.example.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.example.constant.HttpHeaderConstant;
import org.example.jwt.JwtUtil;
import org.example.response.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
@Slf4j
@Order(1)
public class JwtFilter implements GlobalFilter {

    @Autowired
    private ObjectMapper mapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Map<String, List<HttpMethod>> nonSecuredEndpoints = Map.ofEntries(
            Map.entry("/user", List.of(HttpMethod.GET, HttpMethod.POST)),
            Map.entry("/user/login", List.of(HttpMethod.POST)),
            Map.entry("/user/*", List.of(HttpMethod.GET))
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var req = exchange
                .getRequest();

        var res = exchange
                .getResponse();

        res.getHeaders()
                .put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));

        Predicate<ServerHttpRequest> isNonSecuredEndpoint = (r) -> {
            var path = r.getURI().getPath();
            var method = r.getMethod();
            log.info("req path: {}", path);
            return nonSecuredEndpoints
                    .entrySet()
                    .stream()
                    .anyMatch((entry) -> {
                        var key = entry.getKey();
                        var value = entry.getValue();

                        var pathContainer = PathContainer.parsePath(path);
                        var pathPattern = PathPatternParser
                                .defaultInstance
                                .parse(key);

                        return pathPattern
                                .matches(pathContainer)
                                &&
                                value.stream()
                                        .anyMatch((m) -> m.equals(method));
                    });
        };

        if(isNonSecuredEndpoint.test(req)){
            return chain
                    .filter(exchange)
                    .then(Mono.fromRunnable(logResponse(exchange)));
        } else {
            try{
                var headers = req.getHeaders()
                        .get(HttpHeaderConstant.AUTHORIZATION);
                if(headers != null){
                    var header = headers
                            .get(0);
                    if(StringUtils.isBlank(header) || !header.startsWith("Bearer")){
                        var httpResponse = HttpResponse
                                .sendErrorResponse(
                                        "header 'Authorization' value is invalid",
                                        false
                                );
                        return res
                                .writeWith(getDataBuffer(res, httpResponse))
                                .then(Mono.fromRunnable(logResponse(exchange)));
                    } else {
                        var token = header.substring(7);
                        var data = JwtUtil.getTokenData(this.jwtSecret, token);
                        log.info("extracted jwt token: {}", data);
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
                            .writeWith(getDataBuffer(res, httpResponse))
                            .then(Mono.fromRunnable(logResponse(exchange)));
                }
            } catch(Exception ex){
                var httpResponse = HttpResponse
                        .sendErrorResponse(
                                String.format("exception occurred with message: %s", ex.getMessage()),
                                false
                        );
                return res
                        .writeWith(getDataBuffer(res, httpResponse))
                        .then(Mono.fromRunnable(logResponse(exchange)));
            }
        }
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
