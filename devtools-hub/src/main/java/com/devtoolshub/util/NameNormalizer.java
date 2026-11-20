package com.devtoolshub.util;

public final class NameNormalizer {

    private NameNormalizer() {
    }

    public static String normalize(String name, int maxLength) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Название не может быть пустым"
            );
        }

        String normalizedName = name.trim();

        if (normalizedName.length() > maxLength) {
            throw new IllegalArgumentException(
                    "Название не должно превышать "
                            + maxLength + " символов"
            );
        }

        return normalizedName;
    }
}