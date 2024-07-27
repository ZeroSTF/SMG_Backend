package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.controllers.DTO.ArticleDTO;
import tn.zeros.smg.entities.Article;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.services.IServices.IArticleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ArticleController {
    private final IArticleService articleService;

    @GetMapping("/getAll")
    public List<ArticleDTO> getArticles() {
        return articleService.retrieveAllArticles();
    }

    @GetMapping("/get/{article-id}")
    public Article retrieveArticle(@PathVariable("article-id") Long articleId) {
        return articleService.retrieveArticle(articleId);
    }

    @PostMapping("/add")
    public Article addArticle(@RequestBody Article c) {
        return articleService.addArticle(c);
    }

    @DeleteMapping("/delete/{article-id}")
    public void removeArticle(@PathVariable("article-id") Long articleId) {
        articleService.removeArticle(articleId);
    }

    @PutMapping("/update")
    public Article modifyArticle(@RequestBody Article c) {
        return articleService.modifyArticle(c);
    }

    @GetMapping("/search")
    public ResponseEntity<?> chercherArticle(@RequestParam(value = "query", required = false) String query) {
        List<Article> articles = articleService.chercherArticle(query);
        return ResponseEntity.ok().body(articles);
    }
}
