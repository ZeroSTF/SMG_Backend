package tn.zeros.smg.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.zeros.smg.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCode(String code);
    List<User> findAll(Sort sort);
    List<User> findByNomContainingIgnoreCaseOrderByNom(String nom);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.id = 1")
    List<User> findAdminUsers();

    @Query("SELECT u.SOLDE FROM User u")
    List<String> findSoldes();
}
