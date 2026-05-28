package com.internance.common.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internance.common.exception.BusinessException;
import com.internance.common.exception.DownstreamErrorCode;
import com.internance.common.exception.GlobalErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ApiResponseFeignErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(ApiResponseFeignErrorDecoder.class);

    private final ObjectMapper objectMapper;
    private final ErrorDecoder fallback = new Default();

    public ApiResponseFeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = resolveStatus(response.status());
        if (!status.is4xxClientError() && !status.is5xxServerError()) {
            return fallback.decode(methodKey, response);
        }

        String body = readBody(response);
        DecodedError decoded = tryDecode(body);
        if (decoded != null) {
            return new BusinessException(new DownstreamErrorCode(decoded.code(), decoded.message(), status));
        }

        String message = body == null || body.isBlank()
            ? status.getReasonPhrase()
            : body;
        return new BusinessException(new DownstreamErrorCode(fallbackCode(status), message, status));
    }

    private DecodedError tryDecode(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode error = root.path("error");
            if (error.isMissingNode() || error.isNull()) {
                return null;
            }
            String code = error.path("code").asText(null);
            String message = error.path("message").asText(null);
            if (code == null && message == null) {
                return null;
            }
            return new DecodedError(
                code != null ? code : GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                message != null ? message : "");
        } catch (IOException e) {
            log.debug("Feign error body is not JSON: {}", e.getMessage());
            return null;
        }
    }

    private HttpStatus resolveStatus(int raw) {
        HttpStatus resolved = HttpStatus.resolve(raw);
        return resolved != null ? resolved : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String fallbackCode(HttpStatus status) {
        return "G" + status.value();
    }

    private String readBody(Response response) {
        if (response.body() == null) {
            return null;
        }
        try (InputStream in = response.body().asInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.debug("Failed to read feign response body: {}", e.getMessage());
            return null;
        }
    }

    private record DecodedError(String code, String message) {
    }
}
