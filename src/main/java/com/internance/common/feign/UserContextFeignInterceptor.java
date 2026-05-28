package com.internance.common.feign;

import com.internance.common.context.UserContextHolder;
import com.internance.common.filter.UserContextFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserContextFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        UserContextHolder.getUserId()
            .ifPresent(id -> template.header(UserContextFilter.USER_ID_HEADER, id.toString()));
    }
}
