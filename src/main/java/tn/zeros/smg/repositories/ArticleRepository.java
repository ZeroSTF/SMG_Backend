package tn.zeros.smg.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.zeros.smg.controllers.DTO.ArticleDTO;
import tn.zeros.smg.entities.Article;
import tn.zeros.smg.entities.User;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findByReferenceAndFrn(String reference, String frn);
    List<Article> findByDesignationContainingIgnoreCaseOrderByDesignation(String designation);
    List<Article> findAll(Sort sort);
    @Query("SELECT a.id as id, a.designation as designation, a.frn as frn, a.PVHT as PVHT, a.STOCK as STOCK FROM Article a WHERE a.STOCK > 0")
    List<ArticleDTO> findAllProjectedBy(Sort sort);
    }
