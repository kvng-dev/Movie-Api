package com.movieflix.movie_api.auth.controller;

import com.movieflix.movie_api.auth.entity.ForgotPassword;
import com.movieflix.movie_api.auth.entity.User;
import com.movieflix.movie_api.auth.repository.ForgotPasswordRepository;
import com.movieflix.movie_api.auth.repository.UserRepository;
import com.movieflix.movie_api.auth.utils.ChangePassword;
import com.movieflix.movie_api.dto.MailBody;
import com.movieflix.movie_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/verify/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        int otp = otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .subject("Reset Password")
                .text("This is the OTP to reset your password: "+ otp)
                .build();

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .user(user)
                .expirationTime(new Date(System.currentTimeMillis() + 600_000))
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Email sent successfully");

    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable String email, @PathVariable Integer otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: "+ email ));

        if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(forgotPassword.getFpid());
            return ResponseEntity.badRequest().body("OTP has expired");
        }

            return ResponseEntity.ok("OTP verified successfully");

    }

    @PostMapping("/change-password/{email}")
    public ResponseEntity<String> changePassword(@PathVariable String email,
                                                 @RequestBody ChangePassword changePassword) {
        if (!Objects.equals(changePassword.password(), changePassword.confirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        String encoded = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encoded);

        return ResponseEntity.ok("Password changed successfully");
    }



    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt( 100_000, 999_999);
    }
}
