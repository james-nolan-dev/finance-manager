package me.nolanjames.financemanagerapi.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import me.nolanjames.financemanagerapi.shared.ValidationMessages;

public record RegistrationRequest(
        @NotEmpty(message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
        @NotBlank(message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
        String username,
        @Email(message = ValidationMessages.EMAIL_FORMAT)
        @NotEmpty(message = ValidationMessages.EMAIL_MUST_NOT_BE_BLANK)
        @NotBlank(message = ValidationMessages.EMAIL_MUST_NOT_BE_BLANK)
        String email,
        @NotEmpty(message = ValidationMessages.PASSWORD_MUST_NOT_BE_BLANK)
        @NotBlank(message = ValidationMessages.PASSWORD_MUST_NOT_BE_BLANK)
        @Size(min = 8, message = ValidationMessages.PASSWORD_MIN_CHARS)
        String password
) {
}
