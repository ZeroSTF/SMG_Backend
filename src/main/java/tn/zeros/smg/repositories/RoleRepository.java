package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.zeros.smg.entities.Role;
import tn.zeros.smg.entities.enums.TypeRole;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByAuthority(String authority);
    Optional<Role> findByType(TypeRole type);
}
