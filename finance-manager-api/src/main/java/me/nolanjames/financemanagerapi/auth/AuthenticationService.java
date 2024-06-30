package me.nolanjames.financemanagerapi.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import me.nolanjames.financemanagerapi.email.EmailService;
import me.nolanjames.financemanagerapi.email.EmailTemplateName;
import me.nolanjames.financemanagerapi.role.RoleService;
import me.nolanjames.financemanagerapi.role.RoleType;
import me.nolanjames.financemanagerapi.security.JwtService;
import me.nolanjames.financemanagerapi.token.Token;
import me.nolanjames.financemanagerapi.token.TokenService;
import me.nolanjames.financemanagerapi.user.User;
import me.nolanjames.financemanagerapi.user.UserRepository;
import me.nolanjames.financemanagerapi.user.UserService;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final int ACTIVATION_CODE_LENGTH = 6;
    private static final int ACTIVATION_CODE_EXPIRE_MINUTES = 15;
    private static final String ACTIVATION_CODE_CHARS = "123456789";
    private static final String ACTIVATION_EMAIL_SUBJECT = "Account Activation";

    private final RoleService roleService;
    private final UserService userService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Value("${application.mail.frontend.activation-url")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleService.getUserRole(RoleType.USER.name());
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userService.saveUser(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateActivationToken(user);

        emailService.sendMail(
                user.getEmail(),
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                ACTIVATION_EMAIL_SUBJECT
        );
    }

    private String generateActivationToken(User user) {
        String generatedToken = generateActivationCode(ACTIVATION_CODE_LENGTH);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(ACTIVATION_CODE_EXPIRE_MINUTES))
                .user(user)
                .build();
        tokenService.saveToken(token);

        return generatedToken;
    }

    private String generateActivationCode(int length) {
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ACTIVATION_CODE_CHARS.length());
            codeBuilder.append(ACTIVATION_CODE_CHARS.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var claims = new HashMap<String, Object>();
        User user = (User) auth.getPrincipal();
        claims.put("fullName", user.getName());
        var jwtToken = jwtService.generateToken(claims, user);

        return new AuthenticationResponse(jwtToken);
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenService.findByToken(token)
                // todo exception to be defined
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (LocalDateTime.now().equals(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired.  A new token has been sent.");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenService.saveToken(savedToken);
    }
}
