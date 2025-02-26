package com.example.demo.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToUserStatusConverter implements Converter<String, UserStatus> {
    @Override
    public UserStatus convert(String source) {
        return UserStatus.fromValue(source);
    }
}