package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.zeros.smg.entities.PiedVte;

import java.util.List;

public interface PiedVteRepository extends JpaRepository<PiedVte, Long> {

    @Query("SELECT p FROM PiedVte p WHERE p.codecl = :codeClt ORDER BY STR_TO_DATE(p.datvte, '%d/%m/%Y') DESC")
    List<PiedVte> findByCodeclOrderByDatvte(@Param("codeClt") String codeClt);

    PiedVte findByCodeclAndNbon(String codeClt, String nBon);
}
