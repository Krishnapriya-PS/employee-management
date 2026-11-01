package com.codenzic.employee_management.service;

import com.codenzic.employee_management.entity.ErrorCode;
import com.codenzic.employee_management.repository.ErrorCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ErrorCodeService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ErrorCodeService.class);
    private static final int FALLBACK_CODE = 5000;

    private final ErrorCodeRepository repository;
    private final Properties codeProperties;
    private final Map<Integer, ErrorCode> cache = new ConcurrentHashMap<>();

    public ErrorCodeService(ErrorCodeRepository repository) throws IOException {
        this.repository = repository;
        this.codeProperties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("errorcode.properties"));
    }

    @Override
    public void run(ApplicationArguments args) {
        refreshCache();
    }

    /** key (from ErrorCodeConstants) -> numeric code (from errorcode.properties) */
    public int codeFor(String key) {
        String value = codeProperties.getProperty(key);
        if (value == null) {
            log.warn("Error key '{}' not found in errorcode.properties", key);
            return FALLBACK_CODE;
        }
        return Integer.parseInt(value.trim());
    }

    /** key -> table row (cached) */
    public ErrorCode get(String key) {
        int code = codeFor(key);
        ErrorCode entity = cache.get(code);
        if (entity != null) {
            return entity;
        }
        log.warn("Error code {} not found in error_codes table, using fallback", code);
        return new ErrorCode(code, "Something went wrong. Please try again later.",
                "Unknown error code", 500);
    }

    public void refreshCache() {
        cache.clear();
        repository.findAll().forEach(e -> cache.put(e.getCode(), e));
        log.info("Loaded {} error codes", cache.size());
    }
}
