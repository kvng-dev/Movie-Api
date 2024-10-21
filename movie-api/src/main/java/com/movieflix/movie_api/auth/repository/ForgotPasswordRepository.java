package com.movieflix.movie_api.auth.repository;

import com.movieflix.movie_api.auth.entity.ForgotPassword;
import com.movieflix.movie_api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    @Query("SELECT f FROM ForgotPassword f WHERE f.otp = ?1 AND f.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);
}
