package com.example.hello.entity;

import lombok.Data;

@Data
public class UserInfo {
    
    private String userId;
    private String nickname;
    private String realName;
    private String avatar;
    private String gender;
    private String birthdate;
    private String phone;
    private Integer points;
    private Double balance;
    private String memberLevel;
    private Integer addressCount;
    private Integer favoriteCount;
    private Integer couponCount;
    private Integer cardCount;
}