package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.zeros.smg.entities.Confirmation;
import tn.zeros.smg.entities.User;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    Confirmation findByToken(String token);
    Confirmation findConfirmationByUser(User user);
}
