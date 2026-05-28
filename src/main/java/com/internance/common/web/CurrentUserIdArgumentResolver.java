package com.internance.common.web;

import com.internance.common.context.UserContextHolder;
import com.internance.common.exception.BusinessException;
import com.internance.common.exception.GlobalErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(CurrentUserId.class)) {
            return false;
        }
        if (!UUID.class.equals(parameter.getParameterType())) {
            throw new IllegalStateException(
                "@CurrentUserId must be applied to a UUID parameter, but was: "
                    + parameter.getParameterType().getName());
        }
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        CurrentUserId annotation = parameter.getParameterAnnotation(CurrentUserId.class);
        UUID userId = UserContextHolder.getUserId().orElse(null);
        if (userId == null && annotation != null && annotation.required()) {
            throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
        }
        return userId;
    }
}
