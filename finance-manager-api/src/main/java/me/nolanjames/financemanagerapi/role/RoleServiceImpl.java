package me.nolanjames.financemanagerapi.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getUserRole(String userRole) {
        // todo - change exception
        return roleRepository.findByName(userRole)
                .orElseThrow(() -> new IllegalStateException("Role was not initialised"));
    }


}
