package org.project.exchange.model.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import org.project.exchange.config.TokenProvider;
import org.project.exchange.model.auth.service.EmailService;
import org.project.exchange.model.auth.service.PermissionService;
import org.project.exchange.model.user.Dto.ResetNameResponse;
import org.project.exchange.model.user.Dto.SignInRequest;
import org.project.exchange.model.user.Dto.SignInResponse;
import org.project.exchange.model.user.Dto.SignUpRequest;
import org.project.exchange.model.user.Dto.SignUpResponse;
import org.project.exchange.model.user.Dto.UpdateUserInfoRequest;
import org.project.exchange.model.user.Dto.UserInfoResponse;
import org.project.exchange.model.user.KakaoUser;
import org.project.exchange.model.user.RefreshToken;
import org.project.exchange.model.user.User;
import org.project.exchange.model.user.repository.RefreshTokenRepository;
import org.project.exchange.model.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j; 

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final PermissionService permissionService; // 약관 동의 관리
    private final EmailService emailService; // 이메일 인증 관리
    private final KakaoService kakaoService; // 카카오 로그인 관리
    private final Random random = new Random();

    // 📌 비밀번호 패턴 (영문, 숫자, 특수문자 포함, 8~16자)
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,16}$";
    
    @Transactional
    public void sendOtpToEmail(String email) {
        emailService.setEmail(email);
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest request, String otp, List<Boolean> agreedTerms) {
        String normalizedEmail = request.getUserEmail().trim().toLowerCase(Locale.getDefault());

        // 이메일 중복 체크
        if (userRepository.existsByUserEmail(normalizedEmail)) {
            return SignUpResponse.builder()
                    .msg("이메일이 이미 존재합니다.")
                    .build();
        }

        // 이메일 인증 코드 확인
        if (!emailService.checkAuthNumber(normalizedEmail, otp)) {
            return SignUpResponse.builder()
                    .msg("이메일 인증 코드가 유효하지 않습니다.")
                    .build();
        }


        // 비밀번호 형식 확인
        if (!isValidPassword(request.getUserPassword())) {
            return SignUpResponse.builder()
                    .msg("비밀번호 형식이 올바르지 않습니다. 비밀번호는 8자 이상 16자 이하, 문자, 숫자, 특수문자를 포함해야 합니다.")
                    .build();
        }

        // 사용자 생성
        User user = User.builder()
                .userName(request.getUserName())
                .userDateOfBirth(request.getUserDateOfBirth())
                .userGender(request.isUserGender())
                .userEmail(normalizedEmail)
                .userPassword(passwordEncoder.encode(request.getUserPassword()))
                .userCreatedAt(new Date(System.currentTimeMillis()))
                .userUpdatedAt(new Date(System.currentTimeMillis()))
                .build();

        userRepository.save(user);

        // 필수 약관 동의 확인
        if (!permissionService.hasAgreedToRequiredTerms(user)) {
            return SignUpResponse.builder()
                    .msg("필수 약관에 동의해야 회원가입이 가능합니다.")
                    .build();
        }
        // 약관 동의 저장
        permissionService.saveAgreedTerms(user, agreedTerms);

        return SignUpResponse.builder()
                .msg("회원가입 성공")
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userGender(user.isUserGender())
                .userDateOfBirth(user.getUserDateOfBirth().toString())
                .build();
    }

    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        if (request == null || request.getUserEmail() == null || request.getUserPassword() == null) {
            return SignInResponse.builder()
                    .msg("이메일 또는 비밀번호가 제공되지 않았습니다.")
                    .build();
        }

        String normalizedEmail = request.getUserEmail().trim().toLowerCase(Locale.getDefault());
        User user = userRepository.findByUserEmail(normalizedEmail);

        if (user == null) {
            return SignInResponse.builder()
                    .msg("이메일이 존재하지 않습니다.")
                    .build();
        }

        if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())) {
            return SignInResponse.builder()
                    .msg("비밀번호가 일치하지 않습니다.")
                    .build();
        }


        String accessToken = tokenProvider.createToken(user);
        String refreshToken = tokenProvider.createRefreshToken();

        refreshTokenRepository.save(
                refreshTokenRepository.findById(user.getUserId())
                        .map(existingToken -> existingToken.toBuilder().refreshToken(refreshToken).build())
                        .orElseGet(() -> RefreshToken.builder()
                                .tokenId(user.getUserId())
                                .refreshToken(refreshToken)
                                .User(user)
                                .build()));

        return SignInResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .msg("로그인 성공")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public String signOut(String token) throws JsonProcessingException {
        String username = tokenProvider.validateTokenAndGetSubject(token).toLowerCase(Locale.getDefault());

        Optional<User> userOptional = userRepository.findByUserEmailOptional(username);
        if (userOptional.isEmpty() || refreshTokenRepository.findById(userOptional.get().getUserId()).isEmpty()) {
            return "로그아웃 실패";
        }

        try {
            refreshTokenRepository.deleteById(userOptional.get().getUserId());
        } catch (Exception e) {
            return "로그아웃 실패";
        }

        return "로그아웃 성공";
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,16}$";
        return Pattern.matches(passwordPattern, password);
    }

    // 카카오 로그인
    @Transactional
    public SignInResponse kakaoSignIn(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("카카오 액세스 토큰이 유효하지 않습니다.");
        }

        log.info("Received Kakao access token: {}", accessToken);

        KakaoUser kakaoUser = kakaoService.saveOrUpdateKakaoUser(accessToken);
        if (kakaoUser == null) {
            throw new RuntimeException("카카오 사용자 정보가 없습니다.");
        }

        User user = kakaoUser.getUser();
        if (user == null) {
            throw new RuntimeException("해당 카카오 사용자에 대한 유저 정보가 없습니다.");
        }

        log.info("User associated with Kakao user: {}", user);

        String jwtAccessToken = tokenProvider.createToken(user);
        String jwtRefreshToken = tokenProvider.createRefreshToken();

        refreshTokenRepository.save(
                new RefreshToken(user.getUserId(), user, jwtRefreshToken));

        return SignInResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .msg("카카오 로그인 성공")
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    // 아이디(이메일)찾기 - 이름, 생년월일로
    @Transactional
    public String findId(String userName, LocalDate userDateOfBirth) {
        User user = userRepository.findByUserNameAndUserDateOfBirth(userName, userDateOfBirth);
        if (user == null) {
            return "일치하는 사용자 정보가 없습니다.";
        }
        return user.getUserEmail();
    }

    // 비밀번호 찾기 (임시 비밀번호 발급)
    @Transactional
    public String findPassword(String userEmail, String userName) {
        User user = userRepository.findByUserEmail(userEmail);
        if (user == null || !user.getUserName().equals(userName)) {
            return "일치하는 사용자 정보가 없습니다.";
        }

        // 임시 비밀번호 생성
        String tempPassword = generateValidRandomPassword();
        user = user.toBuilder()
                .userPassword(passwordEncoder.encode(tempPassword))
                .userUpdatedAt(new Date(System.currentTimeMillis()))
                .build();
        userRepository.save(user);

        // 임시 비밀번호 이메일 전송
        emailService.sendTemporaryPassword(userEmail, tempPassword);

        return "임시 비밀번호가 이메일로 전송되었습니다.";
    }

    //  비밀번호 재설정
    @Transactional
    public String resetPassword(String userEmail, String newPassword, String confirmPassword) {
        User user = userRepository.findByUserEmail(userEmail);

        if (user == null) {
            return "일치하는 사용자 정보가 없습니다.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.";
        }

        if (!isValidPassword(newPassword)) {
            return "비밀번호는 8~16자이며, 영문, 숫자, 특수문자를 포함해야 합니다.";
        }

        user = user.toBuilder()
                .userPassword(passwordEncoder.encode(newPassword))
                .userUpdatedAt(new Date(System.currentTimeMillis()))
                .build();
        userRepository.save(user);

        return "비밀번호가 성공적으로 변경되었습니다.";
    }

    //  랜덤 비밀번호 생성 (비밀번호 규칙 적용)
    private String generateValidRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password;
        do {
            password = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                password.append(characters.charAt(random.nextInt(characters.length())));
            }
        } while (!isValidPassword(password.toString())); // 규칙 만족할 때까지 반복
        return password.toString();
    }

    // 이름 재설정
    @Transactional
    public ResetNameResponse resetName(String userEmail, String newName) {
        User user = userRepository.findByUserEmail(userEmail);
        if (user == null) {
            return ResetNameResponse.builder()
                    .msg("일치하는 사용자 정보가 없습니다.")
                    .build();
        }

        user = user.toBuilder()
                .userName(newName)
                .userUpdatedAt(new Date(System.currentTimeMillis()))
                .build();
        userRepository.save(user);

        return ResetNameResponse.builder()
                .userEmail(userEmail)
                .msg("이름이 성공적으로 변경되었습니다.")
                .userName(newName)
                .build();
    }

    // 사용자 정보 조회
    public UserInfoResponse getUserInfo(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail);
        if (user == null) {
            return null;
        }

        // `Date` → `String` 변환
        String formattedDate = user.getUserDateOfBirth().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userDateOfBirth(formattedDate) // yyyy-MM-dd로 변환 후 전달
                .build();
    }

    @Transactional
    public String updateUserInfo(UpdateUserInfoRequest request) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserEmail(request.getUserEmail()));

        if (optionalUser.isEmpty()) {
            return "사용자를 찾을 수 없습니다.";
        }

        User user = optionalUser.get();
        boolean updated = false;

        // 이름 변경
        if (request.getUserName() != null && !request.getUserName().isEmpty()) {
            user = user.toBuilder().userName(request.getUserName()).build();
            updated = true;
        }

        // 생년월일 변경 (String → Date 변환)
        if (request.getUserDateOfBirth() != null && !request.getUserDateOfBirth().isEmpty()) {
            LocalDate birthDate = LocalDate.parse(request.getUserDateOfBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            user = user.toBuilder().userDateOfBirth(Date.valueOf(birthDate)).build();
            updated = true;
        }

        // 비밀번호 변경 (선택 사항, 검증 추가)
        if (request.getUserPassword() != null && !request.getUserPassword().isEmpty()) {
            if (!isValidPassword(request.getUserPassword())) {
                return "비밀번호는 8~16자이며, 영문, 숫자, 특수문자를 포함해야 합니다.";
            }
            user = user.toBuilder().userPassword(passwordEncoder.encode(request.getUserPassword())).build();
            updated = true;
        }

        if (updated) {
            userRepository.save(user);
            return "회원정보가 성공적으로 변경되었습니다.";
        } else {
            return "변경할 내용이 없습니다.";
        }
    }
}