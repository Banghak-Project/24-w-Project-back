package org.project.exchange.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.project.exchange.global.api.ApiResponse;
import org.project.exchange.model.user.Dto.KakaoLoginRequest;
import org.project.exchange.model.user.Dto.SignInRequest;
import org.project.exchange.model.user.Dto.SignInResponse;
import org.project.exchange.model.user.Dto.SignUpRequest;
import org.project.exchange.model.user.Dto.SignUpResponse;
import org.project.exchange.model.user.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j; // 📌 log 사용을 위한 Lombok 어노테이션

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signUp(
            @Validated @RequestBody SignUpRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.createFail(bindingResult));
        }
        //회원가입 진행
        SignUpResponse userResponse = userService.signUp(request, request.getOtp(), request.getAgreedTerms());

        // ✅ JSON 직렬화 확인을 위한 로그 추가
        ApiResponse<SignUpResponse> response = ApiResponse.createSuccessWithMessage(userResponse, "회원가입 성공");
        try {
            String jsonResponse = new ObjectMapper().writeValueAsString(response);
            System.out.println("✅ 직렬화된 API 응답: " + jsonResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if ("회원가입 성공".equals(userResponse.getMsg())) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(ApiResponse.createError(userResponse.getMsg()));
    }


        // 로그인
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<?>> signIn(
            @Validated @RequestBody SignInRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body((ApiResponse<SignInResponse>) ApiResponse.createFail(bindingResult));
        }

        SignInResponse response = userService.signIn(request);
        if ("로그인 성공".equals(response.getMsg())) {
            return ResponseEntity.ok(ApiResponse.createSuccessWithMessage(response, "로그인 성공"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createError(response.getMsg()));
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<?>> signOut(@RequestBody Map<String, String> request)
            throws JsonProcessingException {
        String token = request.get("token");
        String response = userService.signOut(token);
        if ("로그아웃 성공".equals(response)) {
            return ResponseEntity.ok(ApiResponse.createSuccessWithMessage(response, "로그아웃 성공"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createError(response));
    }

    @PostMapping("/kakao/signin")
    public ResponseEntity<ApiResponse<?>> kakaoSignIn(@RequestBody KakaoLoginRequest request) {
        log.info("🔍 Raw Request Body: " + request);
        log.info("🔍 Kakao SignIn endpoint hit with token: " + request.getAccessToken());

        if (request.getAccessToken() == null || request.getAccessToken().isEmpty()) {
            log.error("❌ 카카오 로그인 실패: 토큰이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError("카카오 액세스 토큰이 없습니다."));
        }

        SignInResponse response = userService.kakaoSignIn(request.getAccessToken());

        if ("카카오 로그인 성공".equals(response.getMsg())) {
            return ResponseEntity.ok(ApiResponse.createSuccessWithMessage(response, "카카오 로그인 성공"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError(response.getMsg()));
    }

    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<?>> findId(
            @RequestParam String userName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate userDateOfBirth) {

        String userEmail = userService.findId(userName, userDateOfBirth);
        if (userEmail != null) {
            return ResponseEntity.ok(ApiResponse.createSuccessWithMessage(userEmail, "아이디 찾기 성공"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createError("아이디 찾기 실패"));
    }


}