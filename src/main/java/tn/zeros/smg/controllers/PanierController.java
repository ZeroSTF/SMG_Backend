package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.PanierArticle;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.services.IServices.IPanierService;
import tn.zeros.smg.services.IServices.IUserService;
import tn.zeros.smg.services.PanierService;

import java.util.List;

@RestController
@RequestMapping("/panier")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Slf4j
@RequiredArgsConstructor
public class PanierController {
    private final IPanierService panierService;
    private final IUserService userService;

/*    @PostMapping("/{panierId}/articles")
    public ResponseEntity<PanierArticle> addArticleToPanier(
            @PathVariable Long panierId,
            @RequestParam Long articleId,
            @RequestParam int quantity) {
        PanierArticle panierArticle = panierService.addArticleToPanier(panierId, articleId, quantity);
        return ResponseEntity.ok(panierArticle);
    }*/

    @PostMapping("/add-to-cart")
    public ResponseEntity<PanierArticle> addArticleToPanier(
            @RequestParam Long articleId,
            @RequestParam int quantity) {
        // Get the current user's panier ID

        User currentUser;
        ////////////retrieving current user/////////////////////////////////
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentCode = authentication.getName();
        currentUser = userService.loadUserByCode(currentCode);
        ////////////////////////////////////////////////////////////////////
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

    @PostMapping("/{panierId}/checkout")
    public ResponseEntity<Commande> checkout(@PathVariable Long panierId) {
        Commande commande = panierService.checkout(panierId);
        return ResponseEntity.ok(commande);
    }
}
