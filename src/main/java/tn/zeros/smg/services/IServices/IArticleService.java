package tn.zeros.smg.services.IServices;

import tn.zeros.smg.entities.Article;

import java.util.List;

public interface IArticleService {
    //CRUD
    List<Article> retrieveAllArticles();
    Article retrieveArticle(Long id);
    Article addArticle(Article c);
    void removeArticle(Long id);
    Article modifyArticle(Article Article);
}
