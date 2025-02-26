# json-schema-demo 项目说明

## 项目概述
`json-schema-demo` 是一个基于 Spring Boot 的示例项目，主要用于演示如何使用 JSON Schema 进行 API 请求参数的验证。通过该项目，你可以了解到如何配置和使用 JSON Schema 来确保 API 的输入数据符合预期的格式和规则。

## 项目结构
项目的主要结构如下：
- `src/main/java/com/example/demo`：包含主要的 Java 代码。
    - `config`：配置类，如拦截器配置、全局异常处理等。
    - `jsonschema`：JSON Schema 相关的配置和服务类。
    - `user`：用户相关的控制器、转换器等。
    - `DemoApplication.java`：Spring Boot 应用的入口类。
- `src/main/resources`：资源文件目录。
    - `application.yml`：应用的配置文件，包含 JSON Schema 的相关配置。
- `pom.xml`：Maven 项目的配置文件，定义了项目的依赖和构建插件。

## 配置说明
### application.yml
在 `application.yml` 中，主要配置了以下内容：
- `server.port`：应用的端口号，默认为 8080。
- `json-schema`：JSON Schema 的相关配置。
    - `enabled`：是否启用 JSON Schema 验证，默认为 `true`。
    - `exclude-uris`：排除不需要验证的 API 路径。
    - `include-methods`：需要验证的 HTTP 请求方法，如 `GET`。
    - `schemas`：定义了多个 JSON Schema 规则，每个规则包含 `description`、`enabled`、`uri` 和 `schema` 等信息。

### 示例 JSON Schema 配置
以下是一个用于验证 `/api/users/{userId}/orders/{orderId}` GET API 请求参数的 JSON Schema 示例：
```yaml
- description: "查询订单"
  uri: "/api/users/{userId}/orders/{orderId}"
  enabled: true
  schema: >
    {
        "$schema": "http://json-schema.org/draft-07/schema#",
        "title": "查询订单",
        "description": "用于验证 /api/users/{userId}/orders/{orderId} GET API 请求参数的模式",
        "type": "object",
        "properties": {
            "userId": {
                "type": "string",
                "pattern": "^[1-9]\\d*$"
            },
            "orderId": {
                "type": "string",
                "pattern": "^[1-9]\\d*$"
            },
            "status": {
                "type": ["string", "null"],
                "enum": ["YES", "NO", "UNKNOWN", null]
            }
        },
        "required": ["userId", "orderId"],
        "errorMessages": {
            "userId": "userId 必填，且为数字",
            "orderId": "orderId 必填，且为数字",
            "status": "status 值必须为 YES、NO、UNKNOWN其中之一"
        }
    }
```

## 运行项目
1. 确保你已经安装了 Java 8 和 Maven。
2. 克隆项目到本地：
   ```sh
   git clone <项目仓库地址>
   cd json-schema-demo
   ```
3. 使用 Maven 构建和运行项目：
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```
4. 项目启动后，可以通过以下 URL 访问 API：
    - 查询用户记录：`http://localhost:8080/api/users`
    - 获取用户的订单信息：`http://localhost:8080/api/users/1/orders/2`
    - 获取用户信息：`http://localhost:8080/api/users/1`

## 注意事项
- JSON Schema 验证是通过拦截器实现的，确保在配置中正确设置了需要验证的 API 路径和请求方法。
- 当请求参数不符合 JSON Schema 规则时，会返回相应的错误信息。

- 目前仅支持 `GET` 请求。其他 HTTP 请求方法（如 `POST`, `PUT`, `DELETE` 等）暂不支持。
  - **PathVariable（路径变量）**：支持通过路径变量传递参数。例如在 URL 中 `/api/users/{userId}/orders/{orderId}`，`{userId}` 和 `{orderId}` 就是路径变量。
  - **Request Parameter（请求参数）**：支持通过请求参数传递数据，通常在 URL 后面以 `?key=value` 的形式传递，例如 `/api/users?pageNum=1&pageSize=10`。
  - **Body 参数**：不支持通过请求体（如 JSON 或表单数据）传递参数。

请在使用时注意这些限制，以确保请求能够被正确处理。

## 贡献指南
如果你发现了任何问题或者有改进的建议，欢迎提交 Issue 或者 Pull Request。

## 许可证
本项目使用 [MIT 许可证](https://opensource.org/licenses/MIT)。



