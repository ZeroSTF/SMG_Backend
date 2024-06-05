package tn.zeros.smg.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.zeros.smg.entities.Article;
import tn.zeros.smg.repositories.ArticleRepository;
import tn.zeros.smg.services.IServices.IArticleService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ArticleService implements IArticleService {
    private final ArticleRepository articleRepository;

    @Override
    public List<Article> retrieveAllArticles() {
        return articleRepository.findAll();
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

}
