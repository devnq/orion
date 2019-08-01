package org.devnq.orion.server.crypt;

import static at.favre.lib.crypto.bcrypt.BCrypt.Version.VERSION_2B;
import static at.favre.lib.crypto.bcrypt.BCrypt.verifyer;
import static at.favre.lib.crypto.bcrypt.BCrypt.with;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class BCrypt {

    private static final int COST = 6;

    private BCrypt() {}

    public static String hash(final String value) {
        if (isNotBlank(value)) {
            return with(VERSION_2B).hashToString(COST, value.toCharArray());
        } else {
            throw new RuntimeException("Cannot hash an empty or null value");
        }
    }

    public static boolean verify(final String hash, final String value) {
        return verifyer().verify(value.toCharArray(), hash).verified;
    }
}
