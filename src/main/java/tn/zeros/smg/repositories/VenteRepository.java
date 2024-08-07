package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.Vente;

import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {
    List<Vente> findByNbon(String nbon);
    List<Vente> findByNbonInAndCodecl(List<String> nbon, String codecl);
}
