package me.nolanjames.financemanagerapi.role;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import me.nolanjames.financemanagerapi.shared.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role extends BaseEntity {
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType name;
}
