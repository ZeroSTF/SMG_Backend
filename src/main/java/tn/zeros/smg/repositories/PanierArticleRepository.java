package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.Article;
import tn.zeros.smg.entities.Panier;
import tn.zeros.smg.entities.PanierArticle;
import tn.zeros.smg.entities.PanierArticleKey;

import java.util.List;

public interface PanierArticleRepository extends JpaRepository<PanierArticle, PanierArticleKey> {
    List<PanierArticle> findByPanier(Panier panier);
    List<PanierArticle> findByArticle(Article article);
    List<PanierArticle> findAllByPanierId(Long panierId);
}
