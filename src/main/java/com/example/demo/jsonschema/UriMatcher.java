package com.example.demo.jsonschema;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用于匹配 URI 和 URI 模板，并提取路径变量的类
 */
@Slf4j
public class UriMatcher {
    /**
     * 用于匹配路径中是否有变量名称
     */
    private static final Pattern HAS_PATH_VARIABLE_PATTERN = Pattern.compile("\\{[a-zA-Z0-9_]+}");
    /**
     * 用于匹配路径变量的正则表达式模式，路径变量格式为 {变量名}
     */
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("^\\{[a-zA-Z0-9_]+\\}$");
    /**
     * 用于匹配正常 URI 路径段的正则表达式模式
     */
    private static final Pattern NORMAL_SEGMENT_PATTERN = Pattern.compile("^[a-zA-Z0-9-._~!$&'()*+,;=:@%]*$");

    /**
     * 用于匹配有效的 URI 路径
     */
    private static final Pattern URI_PATH_PATTERN = Pattern.compile("^(/[a-zA-Z0-9-._~!$&'()*+,;=:@%{}]*)+$");


    /**
     * 检查字符串是否为空或仅包含空白字符
     *
     * @param str 待检查的字符串
     * @return 如果为空或仅包含空白字符返回 true，否则返回 false
     */
    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 验证 URI 模板的合法性，支持包含路径变量和固定路径的 URI 模板
     *
     * @param uriTemplate 待验证的 URI 模板，例如 "/api/user/{userId}/order/{orderId}"
     * @return 如果 URI 模板合法返回 true，否则返回 false
     */
    public static boolean isValidUriTemplate(String uriTemplate) {
        if (isNullOrEmpty(uriTemplate)) {
            return false;
        }
        String[] segments = uriTemplate.split("/+");
        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }
            if (isPathVariable(segment)) {
                String variableName = segment.substring(1, segment.length() - 1);
                if (!isValidVariableName(variableName)) {
                    return false;
                }
            } else {
                if (!NORMAL_SEGMENT_PATTERN.matcher(segment).matches()) {
                    return false;
                }
            }
        }
        return isUriStructureValid(uriTemplate);
    }

    /**
     * 判断给定的路径段是否为路径变量
     *
     * @param segment 待判断的路径段
     * @return 如果是路径变量返回 true，否则返回 false
     */
    protected static boolean isPathVariable(String segment) {
        if (isNullOrEmpty(segment)) {
            return false;
        }
        return PATH_VARIABLE_PATTERN.matcher(segment).matches();
    }

    /**
     * 验证路径变量名的合法性
     *
     * @param variableName 待验证的路径变量名
     * @return 如果路径变量名合法返回 true，否则返回 false
     */
    protected static boolean isValidVariableName(String variableName) {
        if (isNullOrEmpty(variableName)) {
            return false;
        }
        return variableName.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * 验证整个 URI 模板的结构是否合法
     *
     * @param uriTemplate 待验证的 URI 模板
     * @return 如果 URI 模板结构合法返回 true，否则返回 false
     */
    protected static boolean isUriStructureValid(String uriTemplate) {
        if (isNullOrEmpty(uriTemplate)) {
            return false;
        }
        String sanitizedUri = uriTemplate.replaceAll("\\{[a-zA-Z0-9_]+\\}", "var");
        return isUri(sanitizedUri);
    }

    /**
     * 验证 URI
     *
     * @param uri 待验证的 URI
     * @return 如果 URI 合法返回 true，否则返回 false
     */
    public static boolean isUri(String uri) {
        if (isNullOrEmpty(uri)) {
            return false;
        }
        return URI_PATH_PATTERN.matcher(uri).matches();
    }

    /**
     * 判断 URI 模板中是否包含路径变量
     *
     * @param uriTemplate 待检查的 URI 模板
     * @return 如果包含路径变量返回 true，否则返回 false
     */
    public static boolean hasPathVariable(String uriTemplate) {
        if (isNullOrEmpty(uriTemplate)) {
            return false;
        }
        return HAS_PATH_VARIABLE_PATTERN.matcher(uriTemplate).find();
    }

    /**
     * 检查 URI 是否与 URI 模板匹配，并提取路径变量
     *
     * @param uri         实际的 URI
     * @param uriTemplate URI 模板
     * @return 包含匹配信息和路径变量的对象
     */
    public static UriMatchResult matchUriToTemplate(String uri, String uriTemplate) {
        UriMatchResult result = new UriMatchResult();
        result.setUri(uri);
        result.setUriTemplate(uriTemplate);

        if (!isUri(uri)) {
            result.setMatch(false);
            return result;
        }

        if (!isValidUriTemplate(uriTemplate)) {
            result.setMatch(false);
            return result;
        }

        String[] uriSegments = uri.split("/+");
        String[] templateSegments = uriTemplate.split("/+");

        if (uriSegments.length != templateSegments.length) {
            result.setMatch(false);
            return result;
        }

        Map<String, Object> pathVariables = new HashMap<>();
        for (int i = 0; i < uriSegments.length; i++) {
            String uriSegment = uriSegments[i];
            String templateSegment = templateSegments[i];

            if (isPathVariable(templateSegment)) {
                String variableName = templateSegment.substring(1, templateSegment.length() - 1);
                pathVariables.put(variableName, uriSegment);
            } else if (!uriSegment.equals(templateSegment)) {
                result.setMatch(false);
                return result;
            }
        }

        result.setMatch(true);
        result.setPathVariableMap(pathVariables);
        return result;
    }

    /**
     * 用于存储 URI 匹配结果的类。该类封装了 URI 匹配操作的相关信息，
     * 包括原始 URI、URI 模板、匹配结果以及从 URI 中提取的路径变量。
     */
    @Data
    @ToString
    public static class UriMatchResult {
        /**
         * 实际参与匹配的原始 URI 字符串。它代表了一个具体的资源访问路径，
         * 例如 "/api/user/{userId}/order/{orderId}"，在进行 URI 匹配时以此作为依据之一。
         */
        private String uri;

        /**
         * 用于匹配的 URI 模板字符串。URI 模板可以包含路径变量，
         * 例如 "/api/user/{userId}/order/{orderId}"，其中 "{userId}" 就是一个路径变量，
         * 该模板定义了一类 URI 的通用格式，用于与实际的 URI 进行匹配。
         */
        private String uriTemplate;

        /**
         * 表示 URI 是否与 URI 模板匹配的布尔值。
         * 如果为 true，则说明实际的 URI 与 URI 模板成功匹配；
         * 如果为 false，则表示匹配失败。
         */
        private boolean isMatch;

        /**
         * 存储从 URI 中提取的路径变量及其对应值的映射。
         * 键为 URI 模板中定义的路径变量名，值为实际 URI 中对应位置的具体值。
         * 例如，当 URI 为 "/api/users/123/order/No12345789"，URI 模板为 "/api/user/{userId}/order/{orderId}" 时，
         * 该映射中会包含键值对 "userId" -> "123", "orderId" -> "No12345789"。
         */
        private Map<String, Object> pathVariableMap;
    }
}