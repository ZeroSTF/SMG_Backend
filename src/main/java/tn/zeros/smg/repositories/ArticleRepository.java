package tn.zeros.smg.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.zeros.smg.controllers.DTO.ArticleDTO;
import tn.zeros.smg.entities.Article;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findByReferenceAndFrn(String reference, String frn);
    List<Article> findByReferenceInAndFrnIn(List<String> references, List<String> frns);
    List<Article> findByReferenceStartingWithIgnoreCase(String reference);
    List<Article> findAll(Sort sort);

    @Query("SELECT a.id as id, a.designation as designation, a.frn as frn, a.PVHT as PVHT, a.STOCK as STOCK FROM Article a WHERE a.STOCK > 0")
    List<ArticleDTO> findAllProjectedBy(Sort sort);

    // Advanced search by designation and frn and full frn name
    @Query(value = "SELECT DISTINCT a.* FROM article a " +
            "LEFT JOIN fournisseur f ON a.frn = f.abbreviation " +
            "WHERE (:designation IS NULL OR " +
            "      (SELECT COUNT(*) FROM " +
            "          (SELECT DISTINCT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(:designation, ' ', n), ' ', -1)) AS word " +
            "           FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) numbers " +
            "           WHERE n <= 1 + LENGTH(:designation) - LENGTH(REPLACE(:designation, ' ', '')) " +
            "             AND TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(:designation, ' ', n), ' ', -1)) != '') words " +
            "       WHERE LOWER(a.designation) LIKE CONCAT('%', LOWER(word), '%')) = " +
            "      (SELECT COUNT(*) FROM " +
            "          (SELECT DISTINCT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(:designation, ' ', n), ' ', -1)) AS word " +
            "           FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) numbers " +
            "           WHERE n <= 1 + LENGTH(:designation) - LENGTH(REPLACE(:designation, ' ', '')) " +
            "             AND TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(:designation, ' ', n), ' ', -1)) != '') words)) " +
            "AND (:frn IS NULL OR LOWER(a.frn) LIKE LOWER(CONCAT('%', :frn, '%')) OR LOWER(f.full_name) LIKE LOWER(CONCAT('%', :frn, '%'))) " +
            "ORDER BY a.STOCK DESC", nativeQuery = true)
    List<Article> findByDesignationAndFrn(@Param("designation") String designation, @Param("frn") String frn);

    List<Article> findByDesignationIgnoreCaseContainingOrderBySTOCKDesc(String designation);


}
