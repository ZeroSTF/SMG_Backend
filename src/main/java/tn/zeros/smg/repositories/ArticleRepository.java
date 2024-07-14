package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.Article;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findByReferenceAndFrn(String reference, String frn);
    }
