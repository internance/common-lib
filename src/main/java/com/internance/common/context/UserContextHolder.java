package com.internance.common.context;

import java.util.Optional;
import java.util.UUID;

public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    public static Optional<UserContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static Optional<UUID> getUserId() {
        return get().map(UserContext::userId);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
