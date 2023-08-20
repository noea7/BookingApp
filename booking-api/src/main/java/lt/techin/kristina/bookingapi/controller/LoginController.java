package lt.techin.kristina.bookingapi.controller;

import lt.techin.kristina.bookingapi.exception.BookingValidationException;
import lt.techin.kristina.bookingapi.model.User;
import lt.techin.kristina.bookingapi.model.dto.UserCredentials;
import lt.techin.kristina.bookingapi.repository.UserRepository;
import lt.techin.kristina.bookingapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody UserCredentials credentials) {
        if (credentials.getUsername() == null || credentials.getUsername().equals("") ||
                credentials.getPassword() == null || credentials.getPassword().equals("")) {
            throw new BookingValidationException("Username and password cannot be null or empty");
        }
        User user = userRepository.findByUsername(credentials.getUsername()).orElse(null);
        if (user != null && passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(credentials.getUsername(), user.getSpecialist().getId());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }
}
