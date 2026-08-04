package com.lazyz.wrtpkill;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ConfigTreeMerger {
    private static final Set<String> USER_OWNED_ROOT_SECTIONS = Set.of("worlds");

    private ConfigTreeMerger() {
    }

    static Map<String, Object> merge(
            Map<String, Object> officialDefaults,
            Map<String, Object> userConfig,
            Map<String, Object> previousOfficialDefaults) {
        if (previousOfficialDefaults == null) {
            return bootstrapMerge(officialDefaults, userConfig, true);
        }
        return mergeWithBaseline(officialDefaults, userConfig, previousOfficialDefaults);
    }

    private static Map<String, Object> bootstrapMerge(
            Map<String, Object> officialDefaults,
            Map<String, Object> userConfig,
            boolean root) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : orderedKeys(officialDefaults, userConfig)) {
            boolean hasUserValue = userConfig.containsKey(key);
            boolean hasDefaultValue = officialDefaults.containsKey(key);
            if (root && USER_OWNED_ROOT_SECTIONS.contains(key)) {
                if (hasUserValue) result.put(key, deepCopy(userConfig.get(key)));
                continue;
            }
            if (!hasUserValue) {
                if (hasDefaultValue) result.put(key, deepCopy(officialDefaults.get(key)));
                continue;
            }

            Object userValue = userConfig.get(key);
            Object defaultValue = officialDefaults.get(key);
            if (hasDefaultValue && userValue instanceof Map<?, ?> userMap
                    && defaultValue instanceof Map<?, ?> defaultMap) {
                result.put(key, bootstrapMerge(
                        asStringMap(defaultMap), asStringMap(userMap), false));
            } else {
                result.put(key, deepCopy(userValue));
            }
        }
        return result;
    }

    private static Map<String, Object> mergeWithBaseline(
            Map<String, Object> officialDefaults,
            Map<String, Object> userConfig,
            Map<String, Object> previousOfficialDefaults) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : orderedKeys(officialDefaults, userConfig, previousOfficialDefaults)) {
            boolean hasUserValue = userConfig.containsKey(key);
            boolean hasNewDefault = officialDefaults.containsKey(key);
            boolean hadOldDefault = previousOfficialDefaults.containsKey(key);

            if (!hasUserValue) {
                if (!hadOldDefault && hasNewDefault) {
                    result.put(key, deepCopy(officialDefaults.get(key)));
                }
                continue;
            }

            Object userValue = userConfig.get(key);
            Object newDefault = officialDefaults.get(key);
            Object oldDefault = previousOfficialDefaults.get(key);
            if (hasNewDefault && userValue instanceof Map<?, ?> userMap
                    && newDefault instanceof Map<?, ?> newDefaultMap) {
                Map<String, Object> oldDefaultMap = oldDefault instanceof Map<?, ?> map
                        ? asStringMap(map)
                        : Map.of();
                result.put(key, mergeWithBaseline(
                        asStringMap(newDefaultMap), asStringMap(userMap), oldDefaultMap));
            } else {
                // Existing user values always win, including values equal to old defaults.
                result.put(key, deepCopy(userValue));
            }
        }
        return result;
    }

    @SafeVarargs
    private static Set<String> orderedKeys(Map<String, Object>... maps) {
        Set<String> keys = new LinkedHashSet<>();
        for (Map<String, Object> map : maps) keys.addAll(map.keySet());
        return keys;
    }

    private static Map<String, Object> asStringMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            result.put(String.valueOf(entry.getKey()), deepCopy(entry.getValue()));
        }
        return result;
    }

    static Object deepCopy(Object value) {
        if (value instanceof Map<?, ?> map) return asStringMap(map);
        if (value instanceof List<?> list) {
            List<Object> result = new ArrayList<>(list.size());
            for (Object element : list) result.add(deepCopy(element));
            return result;
        }
        return value;
    }
}
