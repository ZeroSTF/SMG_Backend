package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.RedFact;

import java.util.List;

public interface RedFactRepository extends JpaRepository<RedFact, Long> {
    List<RedFact> findByNfact(String nFact);
    boolean existsByCodeclAndNbon(String codeCl, String nbon);
}
