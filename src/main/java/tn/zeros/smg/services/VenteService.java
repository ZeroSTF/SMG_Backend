package tn.zeros.smg.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.zeros.smg.entities.PiedVte;
import tn.zeros.smg.entities.RedFact;
import tn.zeros.smg.entities.Vente;
import tn.zeros.smg.repositories.ArticleRepository;
import tn.zeros.smg.repositories.PiedVteRepository;
import tn.zeros.smg.repositories.VenteRepository;
import tn.zeros.smg.services.IServices.IFactureService;
import tn.zeros.smg.services.IServices.IVenteService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class VenteService implements IVenteService {
    private final PiedVteRepository piedVteRepository;
    private final VenteRepository venteRepository;
    private final IFactureService factureService;
    private final ArticleRepository articleRepository;

    @Override
    public List<PiedVte> retrieveAllVentes() {
        return piedVteRepository.findAll();
    }

    @Override
    public List<PiedVte> retrieveVteByClient(String codeCl) {
        return piedVteRepository.findByCodecl(codeCl);
    }

    @Override
    public List<Vente> retrieveAllVentesByPiedVte(String nbon) {
        return venteRepository.findByNbon(nbon);
    }

    @Override
    public List<Vente> retrieveAllLignesByPiedFact(String nFact, String codeCl) {
        List<RedFact> redFactList = factureService.retrieveAllRedFactByPiedFact(nFact);
        List <Vente> returnList = new java.util.ArrayList<>(List.of());
        for (RedFact redFact : redFactList) {
            List<Vente> venteList = venteRepository.findByNbonAndCodecl(redFact.getNbon(), codeCl);
            log.info("venteList: " + venteList);
            if (!venteList.isEmpty()) {
                for (Vente vente : venteList) {
                    vente.setInstance(articleRepository.findByReferenceAndFrn(vente.getReference(), vente.getFab()).getDesignation()); //todo fix
                }
                returnList.addAll(venteList);
            }
        }
        return returnList;
    }
}
