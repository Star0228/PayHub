package com.payhub.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreditCardApplication {
    private Long id;
    private Long accountId;
    private Long reviewerId;
    private String status;  // PENDING, APPROVED, REJECTED
    private LocalDateTime applicationDate;
    private LocalDateTime reviewDate;
} 