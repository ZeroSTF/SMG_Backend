package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.User;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    @Query("SELECT c FROM Commande c WHERE c.user = :user ORDER BY c.commandeDate DESC")
    List<Commande> findByUser(User user);

    Long countAllBy();

    Long countAllByUser(User user);

}
