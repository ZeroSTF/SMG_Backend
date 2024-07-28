package tn.zeros.smg.services.IServices;

import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.PanierArticle;

import java.util.List;

public interface IPanierService {
    PanierArticle addArticleToPanier(Long panierId, Long articleId, int quantity);
    void removeArticleFromPanier(Long panierId, Long articleId);
    List<PanierArticle> getPanierContents(Long panierId);
    Commande checkout(Long panierId);
    PanierArticle updateQuantity(Long panierId, Long articleId, int quantity);
}
