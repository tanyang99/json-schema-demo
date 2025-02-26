package com.example.demo.jsonschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * JsonSchemaConfig 类是一个 Spring Boot 配置类，用于加载和验证 JSON Schema 配置。
 * 它从配置文件中读取相关配置信息，对 HTTP 方法、URI 和 JSON Schema 进行验证，并将验证通过的模式存储起来。
 * <p>
 * 示例配置如下（通常在 application.properties 或 application.yml 中配置）：
 * <p>
 * application.yml 示例：
 * <pre>
 * <code>
 * json-schema:
 *   enabled: true
 *   exclude-uris:
 *     - /exclude/uri1
 *     - /exclude/uri2
 *   include-methods:
 *     - GET
 *     - POST
 *   schemas:
 *     - uri: /api/user
 *       schema: |
 *         {
 *           "type": "object",
 *           "properties": {
 *             "name": {
 *               "type": "string"
 *             },
 *             "age": {
 *               "type": "number"
 *             }
 *           }
 *         }
 *       description: "用户信息的 JSON Schema 配置"
 *       enabled: true
 * <pre>
 * <code>
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "json-schema")
public class JsonSchemaConfig {
    /**
     * 表示是否启用 JSON Schema 验证的标志，默认为 false。
     */
    private boolean enabled = false;
    /**
     * 排除的 URI 列表，在验证过程中，这些 URI 将被跳过。
     */
    private List<String> excludeUris = new ArrayList<>();
    /**
     * 包含的 HTTP 方法列表，只有这些方法会参与验证。
     */
    private List<String> includeMethods = new ArrayList<>();
    /**
     * 模式配置列表，每个配置包含 URI、JSON Schema、描述和启用标志。
     */
    private List<SchemaConfig> schemas = new ArrayList<>();
    /**
     * 存储验证通过的 URI 和对应的 JsonSchema 对象的映射。
     */
    private Map<String, JsonSchema> schemaMap = new HashMap<>();
    /**
     * 存储有 PathVariable 变量的 URI 的集合。
     */
    private Set<String> pathVariableUriSet = new HashSet<>();

    /**
     * 初始化方法，在 Bean 初始化完成后调用。
     * 如果启用了验证，则调用 validateMethods 和 validateSchemas 方法进行初始化验证；
     * 否则，记录日志表示验证未启用。
     */
    @PostConstruct
    public void init() {
        if (!enabled) {
            log.info("JSON Schema validation is not enabled.");
            return;
        }
        validateMethods();
        validateSchemas();
    }

    /**
     * 验证 includeMethods 列表中的 HTTP 方法是否合法。
     * 如果列表为空，则记录日志并直接返回；
     * 否则，遍历列表，尝试将每个方法转换为 HttpMethod 枚举类型，
     * 如果转换失败，则记录错误日志并抛出 IllegalArgumentException 异常。
     */
    private void validateMethods() {
        if (includeMethods == null || includeMethods.isEmpty()) {
            log.info("No HTTP methods are configured for validation.");
            return;
        }

        for (String method : includeMethods) {
            try {
                HttpMethod.valueOf(method.toUpperCase());
            } catch (IllegalArgumentException e) {
                String errorMessage = String.format("Invalid HTTP method '%s'. Please check your configuration.", method);
                log.error(errorMessage, e);
                throw new IllegalArgumentException(errorMessage, e);
            }
        }
    }

    /**
     * 获取验证通过的 URI 和对应的 JsonSchema 对象的映射。
     *
     * @return 包含验证通过的 URI 和 JsonSchema 对象的映射。
     */
    public Map<String, JsonSchema> getSchemaMap() {
        return schemaMap;
    }

    /**
     * 遍历 schemas 列表，对每个模式配置进行验证。
     * 验证内容包括配置是否启用、URI 是否在排除列表中、URI 格式是否有效、URI 是否重复、
     * JSON Schema 格式是否有效以及模式转换是否成功。
     * 如果验证通过，则将 URI 加入 uriTemplateSet 集合，并将 URI 和对应的 JsonSchema 对象存入 validSchemas 映射。
     */
    private void validateSchemas() {
        List<String> errorMessages = new ArrayList<>();

        for (SchemaConfig schemaConfig : schemas) {
            String uri = schemaConfig.getUri();
            String schema = schemaConfig.getSchema();
            boolean enabled = schemaConfig.isEnabled();

            if (!enabled) {
                errorMessages.add(String.format("Skipping validation for URI '%s': Validation is disabled", uri));
                continue;
            }

            if (excludeUris != null && !excludeUris.isEmpty() && excludeUris.contains(uri)) {
                errorMessages.add(String.format("Skipping validation for URI '%s': URI is excluded", uri));
                continue;
            }

            if (!isValidUri(uri)) {
                errorMessages.add(String.format("Skipping validation for URI '%s': Invalid format", uri));
                continue;
            }

            if (schemaMap.containsKey(uri)) {
                errorMessages.add(String.format("Skipping validation for URI '%s': URI is duplicated", uri));
                continue;
            }

            if (!isValidJson(schema)) {
                errorMessages.add(String.format("Skipping validation for URI '%s': Invalid JSON schema", uri));
                continue;
            }

            JsonSchema jsonSchema = getJsonSchema(schema);
            if (jsonSchema == null) {
                errorMessages.add(String.format("Skipping validation for URI '%s': Schema conversion failed", uri));
                continue;
            }

            if (UriMatcher.hasPathVariable(uri)) {
                pathVariableUriSet.add(uri);
            }
            schemaMap.put(uri, jsonSchema);
        }

        // Output all error messages
        if (!errorMessages.isEmpty()) {
            log.error("Validation errors encountered:\n{}", String.join("\n", errorMessages));
        }
    }


    /**
     * 验证 URI 格式是否有效。
     * 调用 UriMatcher 类的 isValidUriTemplate 方法进行验证，
     * 如果验证过程中出现异常，则记录错误日志并返回 false。
     *
     * @param uri 待验证的 URI。
     * @return 如果 URI 格式有效返回 true，否则返回 false。
     */
    private boolean isValidUri(String uri) {
        try {
            return UriMatcher.isValidUriTemplate(uri);
        } catch (Exception e) {
            log.error("Error validating URI '{}': {}", uri, e.getMessage());
            return false;
        }
    }

    /**
     * 验证 JSON 字符串格式是否有效。
     * 使用 ObjectMapper 尝试将 JSON 字符串解析为树结构，
     * 如果解析过程中出现异常，则记录错误日志并返回 false。
     *
     * @param schema 待验证的 JSON 字符串。
     * @return 如果 JSON 格式有效返回 true，否则返回 false。
     */
    private boolean isValidJson(String schema) {
        try {
            new ObjectMapper().readTree(schema);
            return true;
        } catch (Exception e) {
            log.error("Error validating JSON schema: {}", schema, e);
            return false;
        }
    }

    private JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    /**
     * 将 JSON 字符串转换为 JsonSchema 对象。
     * 使用 JsonSchemaFactory 根据 JSON 字符串创建 JsonSchema 对象，
     * 如果创建过程中出现异常，则记录错误日志并返回 null。
     *
     * @param schema 待转换的 JSON 字符串。
     * @return 转换后的 JsonSchema 对象，如果转换失败则返回 null。
     */
    private JsonSchema getJsonSchema(String schema) {
        try {
            return jsonSchemaFactory.getSchema(schema);
        } catch (Exception e) {
            log.error("Failed to convert Schema to JSON Schema: {}", schema, e);
            return null;
        }
    }

    /**
     * SchemaConfig 类用于存储每个 URI 的 JSON Schema 配置信息，
     * 包括 URI、Schema、描述和启用标志。
     */
    @Data
    public static class SchemaConfig {
        /**
         * URI
         */
        private String uri;
        /**
         * JSON Schema 字符串。
         */
        private String schema;
        /**
         * 描述信息。
         */
        private String description;
        /**
         * 表示该配置是否启用的标志，默认为 true。
         */
        private boolean enabled = true;
    }
}


