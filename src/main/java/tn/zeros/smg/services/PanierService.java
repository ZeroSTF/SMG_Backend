package tn.zeros.smg.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.zeros.smg.entities.*;
import tn.zeros.smg.entities.enums.CommandeStatus;
import tn.zeros.smg.repositories.ArticleRepository;
import tn.zeros.smg.repositories.CommandeRepository;
import tn.zeros.smg.repositories.PanierArticleRepository;
import tn.zeros.smg.repositories.PanierRepository;
import tn.zeros.smg.services.IServices.IPanierService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PanierService implements IPanierService {
    private final PanierRepository panierRepository;
    private final ArticleRepository articleRepository;
    private final PanierArticleRepository panierArticleRepository;
    private final CommandeRepository commandeRepository;

    @Override
    public PanierArticle addArticleToPanier(Long panierId, Long articleId, int quantity) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new EntityNotFoundException("Panier not found"));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        PanierArticle panierArticle = new PanierArticle();
        panierArticle.setId(new PanierArticleKey(panierId, articleId));
        panierArticle.setPanier(panier);
        panierArticle.setArticle(article);
        panierArticle.setQuantity(quantity);

        return panierArticleRepository.save(panierArticle);
    }

    @Override
    public void removeArticleFromPanier(Long panierId, Long articleId) {
        PanierArticleKey key = new PanierArticleKey(panierId, articleId);
        panierArticleRepository.deleteById(key);
    }

    @Override
    public List<PanierArticle> getPanierContents(Long panierId) {
        return panierArticleRepository.findByPanierId(panierId);
    }

    @Override
    @Transactional
    public Commande checkout(Long panierId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new EntityNotFoundException("Panier not found"));

        if (panier.getPanierArticles().isEmpty()) {
            throw new EntityNotFoundException("Cannot checkout an empty panier");
        }

        Commande commande = new Commande();
        commande.setUser(panier.getUser());
        commande.setCommandeDate(LocalDateTime.now());
        commande.setStatus(CommandeStatus.PENDING);

        double total = 0;
        for (PanierArticle panierArticle : panier.getPanierArticles()) {
            Article article = panierArticle.getArticle();
            int quantity = panierArticle.getQuantity();

            // Check stock
            if (article.getSTOCK() < quantity) {
                throw new EntityNotFoundException("Not enough stock for article: " + article.getReference());
            }

            // Update stock
            article.setSTOCK(article.getSTOCK() - quantity);
            articleRepository.save(article);

            // Create order item
            CommandeItem commandeItem = new CommandeItem();
            commandeItem.setCommande(commande);
            commandeItem.setArticle(article);
            commandeItem.setQuantity(quantity);
            commandeItem.setPrice(Double.parseDouble(article.getPAHT())); // Assuming PAHT is the price
            commande.getCommandeItems().add(commandeItem);

            total += quantity * Double.parseDouble(article.getPAHT());
        }

        commande.setTotal(total);
        Commande savedCommande = commandeRepository.save(commande);

        // Clear the panier
        panier.getPanierArticles().clear();
        panierRepository.save(panier);

        return savedCommande;
    }
}
