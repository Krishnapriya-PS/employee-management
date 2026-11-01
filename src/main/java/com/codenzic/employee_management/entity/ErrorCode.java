package com.codenzic.employee_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "error_codes")
@Getter @Setter @NoArgsConstructor
public class ErrorCode {

    @Id
    @Column(name = "code")
    private Integer code;

    @Column(name = "user_message", nullable = false, length = 255)
    private String userMessage;

    @Column(name = "system_message", nullable = false, length = 255)
    private String systemMessage;

    @Column(name = "http_status", nullable = false)
    private Integer httpStatus;

    public ErrorCode(Integer code, String userMessage, String systemMessage, Integer httpStatus) {
        this.code = code;
        this.userMessage = userMessage;
        this.systemMessage = systemMessage;
        this.httpStatus = httpStatus;
    }
}
