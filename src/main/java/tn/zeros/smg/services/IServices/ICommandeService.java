package tn.zeros.smg.services.IServices;

import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.enums.CommandeStatus;

import java.util.List;

public interface ICommandeService {
    Commande getCommandeById(Long commandeId);
    List<Commande> getUserCommandes(Long userId);
    Commande updateCommandeStatus(Long commandeId, CommandeStatus status);
    Long countCommandes();
}
