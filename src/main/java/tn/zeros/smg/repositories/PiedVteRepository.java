package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.PiedVte;

import java.util.List;

public interface PiedVteRepository extends JpaRepository<PiedVte, Long> {
    List<PiedVte> findByCodecl(String codeClt);
}
