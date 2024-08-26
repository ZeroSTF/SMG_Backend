package tn.zeros.smg.services.IServices;

import tn.zeros.smg.entities.PiedFact;
import tn.zeros.smg.entities.RedFact;

import java.util.List;

public interface IFactureService {
    PiedFact retrieveFacture(String nFact);
    List<PiedFact> retrieveAllFactures();
    List<PiedFact> retrieveFactByClient(String codeCl);
    List<RedFact> retrieveAllRedFactByPiedFact(String nFact);
}
