package com.example.moyeothon.Controller;

import com.example.moyeothon.DTO.JWTDTO;
import com.example.moyeothon.DTO.OAuth2CodeDTO;
import com.example.moyeothon.DTO.UserDTO;
import com.example.moyeothon.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @Operation(summary = "회원 가입")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    // 로그인
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<JWTDTO> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.login(userDTO.getUid(), userDTO.getPassword()));
    }

    // uid로 해당 유저 조회
    @Operation(summary = "uid로 해당 유저 조회")
    @GetMapping("/{uid}")
    public ResponseEntity<UserDTO> getUserByUid(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserByUid(uid, userDetails));
    }

    // 회원 정보 수정
    @Operation(summary = "회원 정보 수정")
    @PutMapping("/{uid}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String uid, @RequestBody UserDTO userDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.updateUser(uid, userDTO, userDetails));
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/{uid}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.deleteUser(uid, userDetails));
    }

    // 닉네임 수정, 소셜 로그인 사용자라면 초기 닉네임 설정
    @Operation(summary = "bucketList 닉네임 수정, 소셜 로그인 사용자라면 초기 닉네임 설정")
    @PutMapping("/nickname/{uid}")
    public ResponseEntity<UserDTO> updateNickname(@PathVariable String uid, @RequestBody String nickname, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.updateNickname(uid, nickname, userDetails));
    }

    // 카카오 로그인 성공 시 호출되는 엔드포인트 (GET)
    @Operation(summary = "카카오 로그인 성공 시 호출되는 엔드포인트 (GET)")
    @GetMapping("/oauth2/code/kakao")
    public ResponseEntity<JWTDTO> kakaoCallback(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok(userService.loginWithOAuth2(code, state));
    }

    // 구글 로그인 성공 시 호출되는 엔드포인트 (GET)
    @Operation(summary = "구글 로그인 성공 시 호출되는 엔드포인트 (GET)")
    @GetMapping("/oauth2/code/google")
    public ResponseEntity<JWTDTO> googleCallback(@RequestParam String code) {
        return ResponseEntity.ok(userService.loginWithGoogleOAuth2(code));
    }
}
