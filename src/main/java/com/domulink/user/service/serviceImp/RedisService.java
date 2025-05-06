package com.domulink.user.service.serviceImp;

import com.domulink.dto.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long OTP_EXPIRATION_MINUTES = 5;


    public void storeSignupData(SignUpRequest request, String otp) {
        String email = request.getEmail();
        String signupKey = getSignupKey(email);
        String otpKey = getOtpKey(email);

        redisTemplate.opsForValue().set(signupKey, request);
        redisTemplate.opsForValue().set(otpKey, otp);
        redisTemplate.expire(signupKey, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        redisTemplate.expire(otpKey, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    public SignUpRequest getSignupRequest(String email) {
        return (SignUpRequest) redisTemplate.opsForValue().get(getSignupKey(email));
    }

    public String getResetPasswordOtp(String email) {
        return (String) redisTemplate.opsForValue().get(getResetEmailKey(email));
    }


    public void storeResetPasswordOtp(String email, String otp){
        String emailRestKey =  getResetEmailKey(email);
        redisTemplate.opsForValue().set(emailRestKey, otp);
        redisTemplate.expire(emailRestKey, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }


    public String getOtp(String email) {
        return (String) redisTemplate.opsForValue().get(getOtpKey(email));
    }

    public void deleteSignupData(String email) {
        redisTemplate.delete(getSignupKey(email));
        redisTemplate.delete(getOtpKey(email));
    }

    public void deleteResetPasswordOtp(String email) {
        redisTemplate.delete(getResetEmailKey(email));
    }


    private String getSignupKey(String email) {
        return "signup:" + email;
    }
    private String getResetEmailKey(String email) {
        return "reset-password:" + email;
    }

    private String getOtpKey(String email) {
        return "otp:" + email;
    }
}

