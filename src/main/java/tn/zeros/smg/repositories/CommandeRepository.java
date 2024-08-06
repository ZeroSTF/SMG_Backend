package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.User;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande,Long> {
    List<Commande> findByUser(User user);
    Long countAllBy();
    Long countAllByUser(User user);

}
