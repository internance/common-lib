package com.internance.common.utils;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static UUID generate() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    public static String generateString() {
        return generate().toString();
    }
}
