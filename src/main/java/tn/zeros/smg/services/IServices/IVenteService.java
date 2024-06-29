package tn.zeros.smg.services.IServices;

import tn.zeros.smg.entities.PiedVte;
import tn.zeros.smg.entities.Vente;

import java.util.List;

public interface IVenteService {
    List<PiedVte> retrieveAllVentes();
    List<PiedVte> retrieveVteByClient(String codeCl);
    List<Vente> retrieveAllVentesByPiedVte(String nbon);
    List<Vente> retrieveAllLignesByPiedFact (String nFact, String codeCl);
}
