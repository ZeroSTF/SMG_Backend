package tn.zeros.smg.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.zeros.smg.controllers.DTO.ArticleDTO;
import tn.zeros.smg.entities.Article;
import tn.zeros.smg.repositories.ArticleRepository;
import tn.zeros.smg.services.IServices.IArticleService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ArticleService implements IArticleService {
    private final ArticleRepository articleRepository;
/*
    public static final String UPLOAD_DIR = "uploads/fournisseurs/";
*/


    @Override
    public List<ArticleDTO> retrieveAllArticles() {
        return articleRepository.findAllProjectedBy(Sort.by(Sort.Direction.ASC, "designation"));
    }

    @Override
    public Article retrieveArticle(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    @Override
    public Article addArticle(Article c) {
        return articleRepository.save(c);
    }

    @Override
    public void removeArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public Article modifyArticle(Article c) {
        return articleRepository.save(c);
    }

    @Override
    public List<Article> chercherArticle(String reference) {
        if(reference == null || reference.isEmpty()){
            return null;
        }
        List<Article> articles= articleRepository.findByReferenceStartingWithIgnoreCase(reference);
        articles.removeIf(article -> article.getReference().length() < reference.length() || article.getReference().length() > reference.length()+1 || article.getDesignation().isEmpty() || article.getPVHT().isEmpty());
        return articles;
    }

    @Override
    public List<Article> advancedSearchArticles(String designation, String frn) {
        List<Article> articles = articleRepository.findByDesignationAndFrn(designation, frn);
        articles.removeIf(article -> article.getDesignation().isEmpty() || article.getPVHT().isEmpty());
        return articles.stream().limit(3).collect(Collectors.toList());
    }

    @Override
    public List<Article> getEquivalentArticles(Long articleId) {
        Article article = articleRepository.findById(articleId).get();
        List<Article> equivalents = articleRepository.findByDesignationIgnoreCaseContainingOrderBySTOCKDesc(article.getDesignation());
        equivalents.removeIf(equivalent -> equivalent.getId().equals(articleId));
        return equivalents;
    }

}
