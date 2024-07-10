package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findFirstByReference(String reference);
}
