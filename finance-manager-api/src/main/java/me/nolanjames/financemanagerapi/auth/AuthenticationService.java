package me.nolanjames.financemanagerapi.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final int ACTIVATION_CODE_LENGTH = 6;
    private static final int ACTIVATION_CODE_EXPIRE_MINUTES = 15;
    private static final String ACTIVATION_CODE_CHARS = "123456789";
    private static final String ACTIVATION_EMAIL_SUBJECT = "Account Activation";

    @Value("${application.mail.frontend.activation-url")
    private String activationUrl;
}
