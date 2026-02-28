package com.example.hello.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名只能包含字母、数字和下划线，长度3-20位")
    private String username;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String phone;
    
    private String fullName;
    
    @Pattern(regexp = "^(USER|ADMIN)$", message = "角色只能是USER或ADMIN")
    private String role;
    
    @Pattern(regexp = "^(男|女|其他)?$", message = "性别只能是男、女或其他")
    private String gender;
    
    private String birthdate;
    
    @Min(value = 0, message = "积分不能为负数")
    @Max(value = 999999, message = "积分不能超过999999")
    private Integer points;
    
    @Min(value = 0, message = "余额不能为负数")
    @Max(value = 999999, message = "余额不能超过999999")
    private Double balance;
    
    @Pattern(regexp = "^(普通会员|银牌会员|金牌会员|钻石会员)$", message = "会员等级只能是普通会员、银牌会员、金牌会员或钻石会员")
    private String memberLevel;
    
    private String avatar;
    
    private Boolean enabled;
}
