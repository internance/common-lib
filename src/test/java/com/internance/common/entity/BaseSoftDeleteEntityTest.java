package com.internance.common.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseSoftDeleteEntityTest {

    /** Minimal concrete entity to exercise the abstract base. */
    static class Sample extends BaseSoftDeleteEntity {
    }

    @Test
    void isActiveByDefault() {
        Sample sample = new Sample();

        assertThat(sample.isDeleted()).isFalse();
        assertThat(sample.getDeletedAt()).isNull();
    }

    @Test
    void softDeleteStampsDeletedAt() {
        Sample sample = new Sample();

        sample.softDelete();

        assertThat(sample.isDeleted()).isTrue();
        assertThat(sample.getDeletedAt()).isNotNull();
    }

    @Test
    void softDeleteIsIdempotentAndPreservesOriginalTimestamp() {
        Sample sample = new Sample();

        sample.softDelete();
        var firstDeletedAt = sample.getDeletedAt();
        sample.softDelete();

        assertThat(sample.getDeletedAt()).isEqualTo(firstDeletedAt);
    }

    @Test
    void restoreClearsDeletedAt() {
        Sample sample = new Sample();
        sample.softDelete();

        sample.restore();

        assertThat(sample.isDeleted()).isFalse();
        assertThat(sample.getDeletedAt()).isNull();
    }
}
