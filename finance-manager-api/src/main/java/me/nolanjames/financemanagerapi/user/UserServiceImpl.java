package me.nolanjames.financemanagerapi.user;

import lombok.RequiredArgsConstructor;
import me.nolanjames.financemanagerapi.shared.BaseService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
