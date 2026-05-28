package com.internance.common.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {

    @JsonCreator
    public PageResponse(
        @JsonProperty("content") List<T> content,
        @JsonProperty("page") int page,
        @JsonProperty("size") int size,
        @JsonProperty("totalElements") long totalElements,
        @JsonProperty("totalPages") int totalPages,
        @JsonProperty("hasNext") boolean hasNext,
        @JsonProperty("hasPrevious") boolean hasPrevious
    ) {
        this.content = content == null ? List.of() : List.copyOf(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }

    public static <S, T> PageResponse<T> of(Page<S> page, Function<? super S, ? extends T> mapper) {
        return of(page.map(mapper));
    }
}
