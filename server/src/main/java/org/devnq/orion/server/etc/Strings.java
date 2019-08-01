package org.devnq.orion.server.etc;

import static java.util.Objects.isNull;

public final class Strings {

    private Strings() {}

    public static String nvl(final String value, final String alternateValue) {
        return isNull(value) ? alternateValue : value;
    }
}
