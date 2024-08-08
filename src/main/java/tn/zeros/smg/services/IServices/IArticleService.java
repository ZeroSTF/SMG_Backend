package tn.zeros.smg.services.IServices;

import tn.zeros.smg.controllers.DTO.ArticleDTO;
import tn.zeros.smg.entities.Article;

import java.util.List;

public interface IArticleService {
    //CRUD
    List<ArticleDTO> retrieveAllArticles();
    Article retrieveArticle(Long id);
    Article addArticle(Article c);
    void removeArticle(Long id);
    Article modifyArticle(Article Article);
    List<Article> chercherArticle(String reference);
    List<Article> advancedSearchArticles(String designation, String frn);
    List<Article> getEquivalentArticles(Long articleId);
}
