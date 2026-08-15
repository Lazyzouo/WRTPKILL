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
            Map<String, Object> userConfig) {
        return mergeMissingDefaults(officialDefaults, userConfig, true);
    }

    private static Map<String, Object> mergeMissingDefaults(
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
                result.put(key, mergeMissingDefaults(
                        asStringMap(defaultMap), asStringMap(userMap), false));
            } else {
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
