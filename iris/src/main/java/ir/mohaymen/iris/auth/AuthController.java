package ir.mohaymen.iris.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthTokensDto> login(
      @RequestBody UserDto request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<String> refreshToken(@RequestBody AuthTokensDto authTokensDto) {
    return ResponseEntity.ok(authService.refreshToken(authTokensDto));
  }

  @GetMapping("/send-activation-code")
  public void sendActivationCode(@RequestParam String phoneNumber) {
    authService.sendActivationCode(phoneNumber);
  }

}
