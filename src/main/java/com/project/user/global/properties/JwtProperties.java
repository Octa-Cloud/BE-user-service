package com.project.user.global.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@ConfigurationProperties("jwt-properties")
public class JwtProperties {

    private final String key;
    private final Long accessTokenExpirationPeriodDay;
    private final Long refreshTokenExpirationPeriodDay;
    private final String accessTokenSubject;
    private final String refreshTokenSubject;
    private final String tokenHeader;
    private final String bearer;
    private final String id;

}
