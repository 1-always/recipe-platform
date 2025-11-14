package recipe_api.controller;

import recipe_api.model.User;
import recipe_api.repo.UserRepository;
import recipe_api.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtTokenProvider jwtProvider;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserRepository userRepo, JwtTokenProvider jwtProvider, BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String handle = body.getOrDefault("handle", null);
        String role = body.getOrDefault("role", "USER");
        if (email == null || password == null) return ResponseEntity.badRequest().body("email & password required");
        if (userRepo.findByEmail(email).isPresent()) return ResponseEntity.status(409).body("email already exists");
        User u = new User();
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setHandle(handle);
        u.setRole(role);
        userRepo.save(u);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Optional<User> ou = userRepo.findByEmail(email);
        if (ou.isEmpty()) return ResponseEntity.status(401).body("invalid");
        User u = ou.get();
        if (!encoder.matches(password, u.getPassword())) return ResponseEntity.status(401).body("invalid");
        String token = jwtProvider.createToken(u.getId(), u.getRole());
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}