package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.PiedFact;

import java.util.List;

public interface PiedFactRepository extends JpaRepository<PiedFact, Long> {
    PiedFact findBynFact(String nFact);
    List<PiedFact> findByCodecl(String codeClt);
}
