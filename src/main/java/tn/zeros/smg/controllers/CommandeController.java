package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.entities.enums.CommandeStatus;
import tn.zeros.smg.services.IServices.IArticleService;
import tn.zeros.smg.services.IServices.ICommandeService;
import tn.zeros.smg.services.IServices.IPanierService;
import tn.zeros.smg.services.IServices.IUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commande")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CommandeController {
    private final ICommandeService commandeService;
    private final IUserService userService;

    @GetMapping("/{commandeId}")
    public ResponseEntity<Commande> getCommande(@PathVariable Long commandeId) {
        Commande commande = commandeService.getCommandeById(commandeId);
        return ResponseEntity.ok(commande);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Commande>> getUserCommandes(@PathVariable Long userId) {
        List<Commande> commandes = commandeService.getUserCommandes(userId);
        return ResponseEntity.ok(commandes);
    }

    @PutMapping("/{commandeId}/status")
    public ResponseEntity<Commande> updateCommandeStatus(
            @PathVariable Long commandeId,
            @RequestParam CommandeStatus status) {
        Commande updatedCommande = commandeService.updateCommandeStatus(commandeId, status);
        return ResponseEntity.ok(updatedCommande);
    }

    @GetMapping("/getAllCurrent")
    public ResponseEntity<List<Commande>> getAllCurrentCommandes() {
        User currentUser= userService.getCurrentUser();
        List<Commande> commandes = commandeService.getUserCommandes(currentUser.getId());
        return ResponseEntity.ok(commandes);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countCommandes() {
        Long count = commandeService.countCommandes();
        return ResponseEntity.ok(count);
    }
}
