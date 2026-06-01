package com.internance.common.entity;

import java.time.LocalDateTime;

/**
 * Contract for entities that support soft deletion: instead of being physically
 * removed from the database, a row is flagged as deleted by stamping the moment
 * it was deleted.
 *
 * <p>Implemented by {@link BaseSoftDeleteEntity}; expose this interface where
 * generic, entity-agnostic handling of soft-deletable rows is needed.
 */
public interface SoftDeletable {

    /**
     * @return the instant this entity was soft-deleted, or {@code null} if it is active.
     */
    LocalDateTime getDeletedAt();

    /**
     * @return {@code true} if this entity has been soft-deleted.
     */
    boolean isDeleted();

    /**
     * Marks this entity as deleted. Idempotent: the original deletion timestamp
     * is preserved if the entity is already deleted.
     */
    void softDelete();

    /**
     * Clears the deletion flag, restoring this entity to the active state.
     */
    void restore();
}
