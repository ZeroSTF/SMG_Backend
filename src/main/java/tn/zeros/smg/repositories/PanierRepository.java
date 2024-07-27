package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.Panier;
import tn.zeros.smg.entities.User;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Panier findByUser(User user);
}
