package me.nolanjames.financemanagerapi.token;

import java.util.Optional;

public interface TokenService {
    Optional<Token> findByToken(String token);

    void saveToken(Token token);
}
