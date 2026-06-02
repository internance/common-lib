package com.internance.common.entity;

import com.internance.common.context.UserContextHolder;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for entities that should be soft-deleted rather than physically
 * removed. Extends {@link BaseEntity}, adding a nullable {@code deleted_at}
 * column: a {@code null} value means the row is active, while a timestamp marks
 * when it was deleted.
 *
 * <p><strong>The {@code @SQLDelete} and {@code @SQLRestriction} annotations must
 * be declared on each concrete entity, not here.</strong> Hibernate does not
 * inherit them from a {@code @MappedSuperclass}, and {@code @SQLDelete} needs the
 * entity's own table name. Declare them on the subclass like so:
 *
 * <pre>{@code
 * @Entity
 * @Table(name = "product")
 * @SQLDelete(sql = "UPDATE product SET deleted_at = now() WHERE id = ?")
 * @SQLRestriction("deleted_at is null")
 * public class Product extends BaseSoftDeleteEntity {
 *     // ...
 * }
 * }</pre>
 *
 * <p>With those in place, {@code repository.delete(entity)} issues the UPDATE
 * above and all queries transparently exclude soft-deleted rows. Alternatively,
 * call {@link #softDelete()} and persist the entity to flag it explicitly.
 *
 * <p>Besides {@code deleted_at}, a {@code deleted_by} column records the
 * {@link UUID} of the user who deleted the row, taken from
 * {@link UserContextHolder} at the time {@link #softDelete()} runs. Note that the
 * {@code @SQLDelete} path issues a static SQL UPDATE that bypasses the entity, so
 * it does <em>not</em> populate {@code deleted_by} (nor the {@code @LastModifiedBy}
 * audit column). Use the {@link #softDelete()}-then-save path when you need the
 * deleter recorded.
 */
@Getter
@MappedSuperclass
public abstract class BaseSoftDeleteEntity extends BaseEntity implements SoftDeletable {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Override
    public boolean isDeleted() {
        return deletedAt != null;
    }

    @Override
    public void softDelete() {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
            deletedBy = UserContextHolder.getUserId().orElse(null);
        }
    }

    @Override
    public void restore() {
        deletedAt = null;
        deletedBy = null;
    }
}
