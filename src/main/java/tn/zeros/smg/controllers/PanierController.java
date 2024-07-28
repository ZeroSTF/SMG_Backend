package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.PanierArticle;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.services.IServices.IPanierService;
import tn.zeros.smg.services.IServices.IUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/panier")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Slf4j
@RequiredArgsConstructor
public class PanierController {
    private final IPanierService panierService;
    private final IUserService userService;

    @PostMapping("/add-to-cart")
    public ResponseEntity<PanierArticle> addArticleToPanier(
            @RequestParam Long articleId,
            @RequestParam int quantity) {
        // Get the current user's panier ID
        User currentUser;
        currentUser = userService.getCurrentUser();
        Long panierId = currentUser.getPanier().getId();
        PanierArticle panierArticle = panierService.addArticleToPanier(panierId, articleId, quantity);
        return ResponseEntity.ok(panierArticle);
    }

    @DeleteMapping("/{panierId}/articles/{articleId}")
    public ResponseEntity<Void> removeArticleFromPanier(
            @PathVariable Long panierId,
            @PathVariable Long articleId) {
        panierService.removeArticleFromPanier(panierId, articleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{panierId}")
    public ResponseEntity<List<PanierArticle>> getPanierContents(@PathVariable Long panierId) {
        List<PanierArticle> contents = panierService.getPanierContents(panierId);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/current")
    public ResponseEntity<List<PanierArticle>> getCurrentPanier() {
        // Get the current user's panier ID
        User currentUser;
        currentUser = userService.getCurrentUser();
        Long panierId = currentUser.getPanier().getId();
        List<PanierArticle> contents = panierService.getPanierContents(panierId);
        return ResponseEntity.ok(contents);
    }

    @DeleteMapping("/remove/{articleId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long articleId) {
        User currentUser = userService.getCurrentUser();
        Long panierId = currentUser.getPanier().getId();
        panierService.removeArticleFromPanier(panierId, articleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<PanierArticle> updateQuantity(@RequestBody Map<String, Object> payload) {
        Long articleId = Long.parseLong(payload.get("articleId").toString());
        int quantity = (int) payload.get("quantity");
        User currentUser = userService.getCurrentUser();
        Long panierId = currentUser.getPanier().getId();
        PanierArticle updatedArticle = panierService.updateQuantity(panierId, articleId, quantity);
        return ResponseEntity.ok(updatedArticle);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Commande> checkout() {
        User currentUser = userService.getCurrentUser();
        Long panierId = currentUser.getPanier().getId();
        Commande commande = panierService.checkout(panierId);
        return ResponseEntity.ok(commande);
    }
}
