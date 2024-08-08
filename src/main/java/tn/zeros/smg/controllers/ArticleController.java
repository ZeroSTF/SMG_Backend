package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.controllers.DTO.ArticleDTO;
import tn.zeros.smg.entities.Article;
import tn.zeros.smg.services.IServices.IArticleService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Slf4j
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

    @GetMapping("/advanced-search")
    public ResponseEntity<?> advancedSearchArticles(
            @RequestParam(value = "designation", required = false) String designation,
            @RequestParam(value = "frn", required = false) String frn) {
        List<Article> articles = articleService.advancedSearchArticles(designation, frn);
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/equivalents")
    public ResponseEntity<?> equivalents(
            @RequestParam(value = "articleId", required = true) Long articleId) {
        List<Article> equivalentArticles = articleService.getEquivalentArticles(articleId);
        return ResponseEntity.ok().body(equivalentArticles);
    }

    /*@GetMapping("/getLogo/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws IOException {
        String filePath = UPLOAD_DIR + fileName +".png";
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }
        byte[] imageData = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }*/
}
