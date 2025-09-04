package com.project.user.global.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@AllArgsConstructor
@ConfigurationProperties("exclude-whitelist-path-patterns")
public class ExcludeWhitelistPathProperties {
    private final List<AuthPath> paths;

    public List<String> getExcludeAuthPathPatterns() {
        return paths.stream().map(AuthPath::getPathPattern).toList();
    }

    @Getter
    @AllArgsConstructor
    public static class AuthPath {
        private String pathPattern;
        private String method;
    }
}
