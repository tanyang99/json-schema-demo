package com.example.demo.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Getter
@Data
public class JsonSchemaValidationService {

    @Autowired
    private JsonSchemaConfig jsonSchemaConfig;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 对给定的请求方法、URI和参数进行JSON Schema验证
     *
     * @param method 请求方法，如GET、POST等
     * @param uri    请求的URI
     * @param params 请求的参数，以键值对的形式存储
     */
    public void validate(String method, String uri, Map<String, Object> params) {
        if (!shouldValidate(method, uri, params)) {
            return;
        }

        uri = adjustUri(uri, params);
        JsonSchema jsonSchema = getSchema(uri);

        if (jsonSchema != null) {
            performValidation(uri, params, jsonSchema);
        }
    }

    /**
     * 判断是否应该进行验证
     *
     * @param method 请求方法
     * @param uri    请求的URI
     * @param params 请求的参数
     * @return 如果应该验证返回true，否则返回false
     */
    private boolean shouldValidate(String method, String uri, Map<String, Object> params) {
        return isValidationEnabled() && isMethodAllowed(method) && isValidUri(uri) && hasParams(params) && !isUriExcluded(uri);
    }

    /**
     * 检查JSON Schema验证功能是否启用
     *
     * @return 如果启用则返回true，否则返回false
     */
    private boolean isValidationEnabled() {
        return this.getJsonSchemaConfig().isEnabled();
    }

    /**
     * 检查请求方法是否在允许的方法列表中
     *
     * @param method 请求方法
     * @return 如果在允许的方法列表中则返回true，否则返回false
     */
    private boolean isMethodAllowed(String method) {
        return this.getJsonSchemaConfig().getIncludeMethods().contains(method);
    }

    /**
     * 检查URI是否有效
     *
     * @param uri 请求的URI
     * @return 如果URI有效则返回true，否则返回false
     */
    private boolean isValidUri(String uri) {
        return UriMatcher.isUri(uri);
    }

    /**
     * 检查参数是否为空
     *
     * @param params 请求的参数
     * @return 如果参数为空则返回true，否则返回false
     */
    private boolean hasParams(Map<String, Object> params) {
        return params != null;
    }

    /**
     * 检查URI是否在排除列表中
     *
     * @param uri 请求的URI
     * @return 如果URI在排除列表中则返回true，否则返回false
     */
    private boolean isUriExcluded(String uri) {
        return this.getJsonSchemaConfig().getExcludeUris().contains(uri);
    }

    /**
     * 调整URI，处理其中的路径变量，并将路径变量添加到参数中
     *
     * @param uri    请求的URI
     * @param params 请求的参数
     * @return 调整后的URI
     */
    private String adjustUri(String uri, Map<String, Object> params) {
        Set<String> pathVariableUriSet = this.getJsonSchemaConfig().getPathVariableUriSet();
        UriMatcher.UriMatchResult matchResult = findMatchingUri(uri, pathVariableUriSet);

        if (matchResult != null && matchResult.isMatch()) {
            uri = matchResult.getUriTemplate();
            addPathVariables(params, matchResult.getPathVariableMap());
        }
        return uri;
    }

    /**
     * 查找与给定URI匹配的URI模板
     *
     * @param uri          请求的URI
     * @param uriTemplates 包含URI模板的集合
     * @return 匹配结果，如果没有匹配则返回null
     */
    private UriMatcher.UriMatchResult findMatchingUri(String uri, Set<String> uriTemplates) {
        for (String uriTemplate : uriTemplates) {
            UriMatcher.UriMatchResult result = UriMatcher.matchUriToTemplate(uri, uriTemplate);
            if (result.isMatch()) {
                return result;
            }
        }
        return null;
    }

    /**
     * 将路径变量添加到参数中
     *
     * @param params          请求的参数
     * @param pathVariableMap 路径变量映射
     */
    private void addPathVariables(Map<String, Object> params, Map<String, Object> pathVariableMap) {
        params.putAll(pathVariableMap);
    }

    /**
     * 获取与给定URI对应的JSON Schema
     *
     * @param uri 请求的URI
     * @return 对应的JSON Schema，如果不存在则返回null
     */
    private JsonSchema getSchema(String uri) {
        JsonSchema jsonSchema = this.getJsonSchemaConfig().getSchemaMap().get(uri);
        if (jsonSchema == null) {
            log.warn("No JSON schema found for URI: {}", uri);
        }
        return jsonSchema;
    }

    /**
     * 执行JSON Schema验证
     *
     * @param uri        请求的URI
     * @param params     请求的参数
     * @param jsonSchema 对应的JSON Schema
     */
    private void performValidation(String uri, Map<String, Object> params, JsonSchema jsonSchema) {
        if (!hasParams(params)) {
            return;
        }
        JsonNode jsonNode = this.getObjectMapper().valueToTree(params);
        Set<ValidationMessage> validationMessages = jsonSchema.validate(jsonNode);

        if (!validationMessages.isEmpty()) {
            handleValidationErrors(uri, validationMessages, jsonSchema);
        }
    }

    /**
     * 处理验证错误
     *
     * @param uri                请求的URI
     * @param validationMessages 验证消息集合
     * @param jsonSchema         JSON Schema对象
     */
    private void handleValidationErrors(String uri, Set<ValidationMessage> validationMessages, JsonSchema jsonSchema) {
        List<String> customErrorMessages = buildCustomErrorMessages(validationMessages, jsonSchema);
        String errors = String.join("; ", customErrorMessages);
        log.info("URI '{}' parameter validation failed: {}", uri, errors);
        throw new JsonSchemaValidationException(errors);
    }

    /**
     * 构建自定义的错误消息列表
     *
     * @param messages   验证消息集合
     * @param jsonSchema JSON Schema对象
     * @return 自定义的错误消息列表
     */
    private List<String> buildCustomErrorMessages(Set<ValidationMessage> messages, JsonSchema jsonSchema) {
        List<String> errorMessages = new ArrayList<>();
        for (ValidationMessage message : messages) {
            errorMessages.add(getCustomErrorMessage(message, jsonSchema));
        }
        return errorMessages;
    }

    /**
     * 从验证消息中提取自定义的错误消息
     *
     * @param message    验证消息
     * @param jsonSchema JSON Schema对象
     * @return 自定义错误消息或原始验证消息
     */
    private String getCustomErrorMessage(ValidationMessage message, JsonSchema jsonSchema) {
        try {
            // 获取包含自定义错误信息的节点
            JsonNode errorMessagesNode = jsonSchema.getSchemaNode().at("/errorMessages");
            if (errorMessagesNode.isMissingNode()) {
                return message.getMessage();
            }

            // 获取节点名称
            String nodeName = getNodeName(message);
            JsonNode customMessageNode = errorMessagesNode.get(nodeName);

            // 检查节点是否存在且为文本类型，并且文本内容不为空
            if (customMessageNode != null && customMessageNode.isTextual()) {
                String customMessage = customMessageNode.asText().trim();
                if (!customMessage.isEmpty()) {
                    return String.format("参数 '%s' 验证失败 ： '%s' ", nodeName, customMessage);
                }
            }
        } catch (Exception e) {
            // 记录处理验证消息时的异常信息
            log.warn("Error processing validation message", e);
        }
        // 若未找到有效自定义消息，返回原始验证消息
        return message.getMessage();
    }

    /**
     * 根据验证消息获取节点名称
     *
     * @param message 验证消息
     * @return 节点名称
     */
    private String getNodeName(ValidationMessage message) {
        return message.getMessageKey().equals("required") ? message.getProperty() : message.getInstanceLocation().getName(0);
    }
}