package org.devnq.orion.server.json;

import org.json.JSONObject;

public final class Util {

    private Util() {}

    public static JSONObject shallowCopy(final JSONObject obj) {
        return new JSONObject(obj, JSONObject.getNames(obj));
    }

    public static JSONObject dissoc(final JSONObject o, final String key) {
        final JSONObject clone = shallowCopy(o);
        clone.remove(key);
        return clone;
    }

    public static JSONObject assoc(final JSONObject o, final String key, final Object val) {
        final JSONObject clone = shallowCopy(o);
        clone.put(key, val);
        return clone;
    }
}
