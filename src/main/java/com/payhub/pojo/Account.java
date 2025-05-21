package com.payhub.pojo;

import lombok.Data;

@Data
public class Account {
    private Long id;
    private Long accountId;
    private String password;
    private Long cardId;
    private String username;
    private String email;
    private String address;
    private String gender;
    private String occupation;
    private Long phoneNumber;
    private Double annualIncome;
}