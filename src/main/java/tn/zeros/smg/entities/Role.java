package tn.zeros.smg.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import tn.zeros.smg.entities.enums.TypeRole;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String authority;
    @Enumerated(EnumType.STRING)
    private TypeRole type;
    public Role(String authority){
        this.authority = authority;
    }
    @Override
    public String getAuthority() {
        return this.authority;
    }
}
