package com.example.demo.jsonschema;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Data
@Component
public class JsonSchemaValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private JsonSchemaValidationService jsonSchemaValidationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查请求方法是否为 GET
        if (!isGetRequest(request)) {
            log.warn("不支持的请求方法，仅支持 GET 请求");
            return true;
        }
        // 提取请求参数
        Map<String, Object> params = extractRequestParams(request);
        this.getJsonSchemaValidationService().validate(request.getMethod(), request.getRequestURI(), params);
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里添加请求处理完成但视图渲染之前的逻辑
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 可以在这里添加请求完成后的逻辑，例如日志记录等

    }

    /**
     * 检查请求是否为 GET 请求
     *
     * @param request HttpServletRequest 对象
     * @return 如果是 GET 请求返回 true，否则返回 false
     */
    private boolean isGetRequest(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod());
    }

    /**
     * 提取请求参数
     *
     * @param request HttpServletRequest 对象
     * @return 包含请求参数的 Map
     */
    private Map<String, Object> extractRequestParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.put(entry.getKey(), values[0]);
            }
        }
        return params;
    }
}