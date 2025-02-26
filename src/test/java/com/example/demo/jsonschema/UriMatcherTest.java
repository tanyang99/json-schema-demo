package com.example.demo.jsonschema;

import com.example.demo.jsonschema.UriMatcher;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class UriMatcherTest {

    // 测试 isValidUriTemplate 方法
    @Test
    void testIsValidUriTemplate() {
        // 有效 URI 模板
        String validTemplate = "/api/user/{userId}/order/{orderId}";
        assertTrue(UriMatcher.isValidUriTemplate(validTemplate));

        // 无效 URI 模板 - 空字符串
        String emptyTemplate = "";
        assertFalse(UriMatcher.isValidUriTemplate(emptyTemplate));

        // 无效 URI 模板 - 包含非法字符
        String invalidTemplate = "/api/user/{userId}/order/{orderId!}";
        assertFalse(UriMatcher.isValidUriTemplate(invalidTemplate));
    }

    // 测试 isPathVariable 方法
    @Test
    void testIsPathVariable() {
        // 是路径变量
        String pathVariable = "{userId}";
        assertTrue(UriMatcher.isPathVariable(pathVariable));

        // 不是路径变量
        String nonPathVariable = "userId";
        assertFalse(UriMatcher.isPathVariable(nonPathVariable));
    }

    // 测试 isValidVariableName 方法
    @Test
    void testIsValidVariableName() {
        // 有效变量名
        String validName = "userId";
        assertTrue(UriMatcher.isValidVariableName(validName));

        // 无效变量名 - 包含非法字符
        String invalidName = "user-id";
        assertFalse(UriMatcher.isValidVariableName(invalidName));
    }

    // 测试 isUriStructureValid 方法
    @Test
    void testIsUriStructureValidTrue() {
        // 有效 URI 结构
        String validUri = "/api/user/var/order/var";
        assertTrue(UriMatcher.isUriStructureValid("/api/user/{userId}/order/{orderId}"));

    }


    @Test
    void testIsUriStructureValidFalse() {

        // 无效 URI 结构
        String invalidUri = "invalid//uri";
        assertFalse(UriMatcher.isUriStructureValid(invalidUri));
    }

    // 测试 isUri 方法
    @Test
    void testIsUri() {
        // 有效 URI
        String validUri = "/api/user/123";
        assertTrue(UriMatcher.isUri(validUri));

        // 无效 URI
        String invalidUri = "invalid uri";
        assertFalse(UriMatcher.isUri(invalidUri));
    }

    // 测试 hasPathVariable 方法
    @Test
    void testHasPathVariable() {
        // 包含路径变量
        String uriWithPathVariable = "/api/user/{userId}/order/{orderId}";
        assertTrue(UriMatcher.hasPathVariable(uriWithPathVariable));

        // 不包含路径变量
        String uriWithoutPathVariable = "/api/user/123/order/456";
        assertFalse(UriMatcher.hasPathVariable(uriWithoutPathVariable));
    }

    // 测试 matchUriToTemplate 方法
    @Test
    void testMatchUriToTemplate() {
        // 匹配成功
        String uri = "/api/user/123/order/456";
        String uriTemplate = "/api/user/{userId}/order/{orderId}";
        UriMatcher.UriMatchResult result = UriMatcher.matchUriToTemplate(uri, uriTemplate);
        assertTrue(result.isMatch());
        Map<String, String> expectedPathVariables = new HashMap<>();
        expectedPathVariables.put("userId", "123");
        expectedPathVariables.put("orderId", "456");
        assertEquals(expectedPathVariables, result.getPathVariableMap());

        // 匹配失败 - 路径段数量不一致
        String uriMismatchSegments = "/api/user/123";
        result = UriMatcher.matchUriToTemplate(uriMismatchSegments, uriTemplate);
        assertFalse(result.isMatch());

        // 匹配失败 - 非路径变量段不匹配
        String uriMismatchNonVariable = "/api/admin/123/order/456";
        result = UriMatcher.matchUriToTemplate(uriMismatchNonVariable, uriTemplate);
        assertFalse(result.isMatch());
    }


    @Test
    public void testHasPathVariable_containsPathVariable() {
        // 测试 URI 模板包含路径变量的情况
        String uriTemplateWithVariable = "/api/user/{userId}/order";
        boolean result = UriMatcher.hasPathVariable(uriTemplateWithVariable);
        assertTrue(result, "当 URI 模板包含路径变量时，应返回 true");
    }

    @Test
    public void testHasPathVariable_noPathVariable() {
        // 测试 URI 模板不包含路径变量的情况
        String uriTemplateWithoutVariable = "/api/user/123/order";
        boolean result = UriMatcher.hasPathVariable(uriTemplateWithoutVariable);
        assertFalse(result, "当 URI 模板不包含路径变量时，应返回 false");
    }

    @Test
    public void testHasPathVariable_emptyTemplate() {
        // 测试 URI 模板为空字符串的情况
        String emptyTemplate = "";
        boolean result = UriMatcher.hasPathVariable(emptyTemplate);
        assertFalse(result, "当 URI 模板为空字符串时，应返回 false");
    }

    @Test
    public void testHasPathVariable_nullTemplate() {
        // 测试 URI 模板为 null 的情况
        String nullTemplate = null;
        // 这里根据 hasPathVariable 方法逻辑，虽然未对 null 做特殊处理，但正则匹配 null 不会匹配到路径变量，结果是 false
        boolean result = UriMatcher.hasPathVariable(nullTemplate);
        assertFalse(result, "当 URI 模板为 null 时，应返回 false");
    }
}