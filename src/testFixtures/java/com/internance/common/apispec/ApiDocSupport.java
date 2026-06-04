package com.internance.common.apispec;

import com.internance.common.filter.UserContextFilter;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

/**
 * Shared Spring REST Docs / restdocs-api-spec (epages) helpers for documenting the
 * cross-cutting request headers every Internance service exposes, plus the common
 * request/response preprocessors.
 *
 * <p>Published as the {@code common-lib} test-fixtures artifact. A consuming
 * service uses it from its own controller tests:
 * <pre>{@code
 * testImplementation(testFixtures("com.github.internance:common-lib:<tag>"))
 * }</pre>
 *
 * <p>JWT bearer auth is auto-detected by restdocs-api-spec from the
 * {@code Authorization: Bearer ...} header, so it surfaces in the generated spec
 * once a test sends that header; {@link #bearerAuthHeader()} documents it
 * consistently. The {@code X-User-Id} header is a plain documented request header
 * ({@link #userIdHeader()}), reusing {@link UserContextFilter#USER_ID_HEADER} so
 * the name never drifts from the runtime filter.
 */
public final class ApiDocSupport {

    /** The HTTP {@code Authorization} header carrying the JWT bearer token. */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** The {@code X-User-Id} header bound by {@link UserContextFilter}. */
    public static final String USER_ID_HEADER = UserContextFilter.USER_ID_HEADER;

    private ApiDocSupport() {
    }

    /**
     * @return a descriptor for the {@code X-User-Id} header (authenticated user id,
     *         a UUID v7, propagated by the gateway).
     */
    public static HeaderDescriptor userIdHeader() {
        return headerWithName(USER_ID_HEADER)
                .description("Authenticated user id (UUID v7), propagated by the gateway.");
    }

    /**
     * @return a descriptor for the {@code Authorization} header carrying a JWT
     *         bearer token; restdocs-api-spec promotes this to a bearer security
     *         scheme in the generated OpenAPI spec.
     */
    public static HeaderDescriptor bearerAuthHeader() {
        return headerWithName(AUTHORIZATION_HEADER)
                .description("JWT bearer token in the form `Bearer <token>`.");
    }

    /** @return the standard request preprocessor (pretty-printed bodies). */
    public static OperationRequestPreprocessor requestPreprocessor() {
        return preprocessRequest(prettyPrint());
    }

    /** @return the standard response preprocessor (pretty-printed bodies). */
    public static OperationResponsePreprocessor responsePreprocessor() {
        return preprocessResponse(prettyPrint());
    }
}
