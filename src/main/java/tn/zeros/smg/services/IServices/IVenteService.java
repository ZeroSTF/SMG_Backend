package tn.zeros.smg.services.IServices;

import tn.zeros.smg.entities.PiedFact;
import tn.zeros.smg.entities.PiedVte;
import tn.zeros.smg.entities.Vente;

import java.util.List;

public interface IVenteService {
    PiedVte retrievePiedVteById(Long id);
    List<PiedVte> retrieveAllVentes();
    List<PiedVte> retrieveVteByClient(String codeCl);
    PiedVte retrieveVteByClientAndNbon(String codeCl, String nbon);
    List<Vente> retrieveAllVentesByPiedVte(String nbon);
    List<Vente> retrieveAllLignesByPiedFact (String nFact, String codeCl);
    List<Vente> retrieveAllLignesByPiedVte (String nbon, String codeCl);
    void inputPiedVteStatus();
}
