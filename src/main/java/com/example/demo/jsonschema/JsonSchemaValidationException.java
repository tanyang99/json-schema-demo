package com.example.demo.jsonschema;

/**
 * 自定义异常类，用于在 JSON Schema 验证过程中出现错误时抛出特定的异常信息。
 * 继承自 RuntimeException，属于非受检异常，使用时无需在方法签名中显式声明抛出。
 */
public class JsonSchemaValidationException extends RuntimeException {

    /**
     * 无参构造函数。
     * 调用父类 RuntimeException 的无参构造函数，创建一个没有特定错误消息的异常实例。
     */
    public JsonSchemaValidationException() {
        super();
    }

    /**
     * 带有错误消息的构造函数。
     * 接收一个字符串参数作为错误消息，调用父类 RuntimeException 的相应构造函数，
     * 方便在抛出异常时提供详细的错误信息。
     *
     * @param message 异常的详细错误消息
     */
    public JsonSchemaValidationException(String message) {
        super(message, null, false, false);
    }

    /**
     * 带有错误消息和原始异常的构造函数。
     * 接收一个字符串参数作为错误消息，以及一个 Throwable 类型的参数作为原始异常，
     * 用于在捕获异常并重新抛出时保留原始异常信息。
     *
     * @param message 异常的详细错误消息
     * @param cause   引发此异常的原始异常
     */
    public JsonSchemaValidationException(String message, Throwable cause) {
        super(message, cause, false, false);
    }

    /**
     * 带有原始异常的构造函数。
     * 接收一个 Throwable 类型的参数作为原始异常，调用父类 RuntimeException 的相应构造函数，
     * 用于将其他异常包装成 JsonSchemaException。
     *
     * @param cause 引发此异常的原始异常
     */
    public JsonSchemaValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * 受保护的构造函数，提供了更详细的控制选项。
     * 包括是否启用抑制和可序列化栈跟踪，通常在特定场景下使用。
     *
     * @param message            异常的详细错误消息
     * @param cause              引发此异常的原始异常
     * @param enableSuppression  是否启用异常抑制功能
     * @param writableStackTrace 栈跟踪信息是否可写
     */
    protected JsonSchemaValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}