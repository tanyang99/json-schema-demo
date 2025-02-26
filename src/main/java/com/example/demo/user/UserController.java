package com.example.demo.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/")
public class UserController {

    /**
     * 查询用户记录
     *
     * @param pageNum    页码，默认值为1
     * @param pageSize   每页记录数，默认值为10
     * @param status 外呼状态，可选参数
     * @param userIds    用户ID数组，可选参数
     * @return 响应结果
     */
    @GetMapping("/users")
    public ResponseEntity<String> findUsers(
            @RequestParam(value = "pageNum") Long pageNum,
            @RequestParam(value = "pageSize") Long pageSize,
            @RequestParam(value = "status", required = false) UserStatus status,
            @RequestParam(value = "userIds", required = false) Long[] userIds) {
        try {
            log.info("查询用户记录，页码: {}, 每页记录数: {}, 状态: {}, 用户ID数组: {}",
                    pageNum, pageSize, status, Arrays.toString(userIds));
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("查询用户记录时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询用户记录时发生错误");
        }
    }

    /**
     * 获取用户的订单信息
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param status  订单状态，可选参数
     * @return 响应结果
     */
    @GetMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<String> getUserOrder(
            @PathVariable("userId") String userId,
            @PathVariable("orderId") String orderId,
            @RequestParam(value = "status", required = false) UserStatus status) {
        try {
            // 这里可以添加实际的业务逻辑
            log.info("获取用户 {} 的订单 {} 信息，订单状态: {}", userId, orderId, status);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("获取用户订单信息时发生错误，用户ID: {}, 订单ID: {}", userId, orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取用户订单信息时发生错误");
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @param status 用户状态，可选参数
     * @return 响应结果
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<String> getUser(
            @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "status", required = false) UserStatus status) {
        try {
            // 这里可以添加实际的业务逻辑
            log.info("获取用户 {} 信息，用户状态: {}", userId, status);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("获取用户信息时发生错误，用户ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取用户信息时发生错误");
        }
    }


}