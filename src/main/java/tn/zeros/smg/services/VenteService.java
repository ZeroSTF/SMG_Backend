package tn.zeros.smg.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.zeros.smg.entities.Article;
import tn.zeros.smg.entities.PiedVte;
import tn.zeros.smg.entities.RedFact;
import tn.zeros.smg.entities.Vente;
import tn.zeros.smg.repositories.ArticleRepository;
import tn.zeros.smg.repositories.PiedVteRepository;
import tn.zeros.smg.repositories.RedFactRepository;
import tn.zeros.smg.repositories.VenteRepository;
import tn.zeros.smg.services.IServices.IFactureService;
import tn.zeros.smg.services.IServices.IVenteService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class VenteService implements IVenteService {
    private final PiedVteRepository piedVteRepository;
    private final VenteRepository venteRepository;
    private final RedFactRepository redFactRepository;
    private final IFactureService factureService;
    private final ArticleRepository articleRepository;

    @Override
    public PiedVte retrievePiedVteById(Long id) {
        return piedVteRepository.findById(id).orElse(null);
    }

    @Override
    public List<PiedVte> retrieveAllVentes() {
        return piedVteRepository.findAll();
    }

    @Override
    public List<PiedVte> retrieveVteByClient(String codeCl) {
        return piedVteRepository.findByCodeclOrderByDatvte(codeCl);
    }

    @Override
    public PiedVte retrieveVteByClientAndNbon(String codeCl, String nbon) {
        return piedVteRepository.findByCodeclAndNbon(codeCl, nbon);
    }

    @Override
    public List<Vente> retrieveAllVentesByPiedVte(String nbon) {
        return venteRepository.findByNbon(nbon);
    }

    @Override
    public List<Vente> retrieveAllLignesByPiedFact(String nFact, String codeCl) {
        List<RedFact> redFactList = factureService.retrieveAllRedFactByPiedFact(nFact);

        // Collect all nbon values to fetch ventes in a single query
        List<String> nbonList = redFactList.stream().map(RedFact::getNbon).collect(Collectors.toList());

        // Fetch all ventes in one query
        List<Vente> allVentes = venteRepository.findByNbonInAndCodecl(nbonList, codeCl);

        // Collect all article references to fetch articles in a single query
        List<String> references = allVentes.stream().map(Vente::getReference).collect(Collectors.toList());
        List<String> frns = allVentes.stream().map(Vente::getFab).collect(Collectors.toList());

        // Fetch all articles in one query
        List<Article> articles = articleRepository.findByReferenceInAndFrnIn(references, frns);

        // Create a map for quick lookup
        Map<String, Article> articleMap = articles.stream()
                .collect(Collectors.toMap(a -> a.getReference() + a.getFrn(), a -> a));

        // Set instance for each vente
        allVentes.forEach(vente -> {
            Article article = articleMap.get(vente.getReference() + vente.getFab());
            if (article != null) {
                vente.setInstance(article.getDesignation());
            }
        });
        return allVentes;
    }

    @Override
    public List<Vente> retrieveAllLignesByPiedVte(String nbon, String codeCl) {
        List<Vente> allVentes = venteRepository.findByNbonAndCodecl(nbon, codeCl);

        // Collect all article references to fetch articles in a single query
        List<String> references = allVentes.stream().map(Vente::getReference).collect(Collectors.toList());
        List<String> frns = allVentes.stream().map(Vente::getFab).collect(Collectors.toList());

        // Fetch all articles in one query
        List<Article> articles = articleRepository.findByReferenceInAndFrnIn(references, frns);

        // Create a map for quick lookup
        Map<String, Article> articleMap = articles.stream()
                .collect(Collectors.toMap(a -> a.getReference() + a.getFrn(), a -> a));

        // Set instance for each vente
        allVentes.forEach(vente -> {
            Article article = articleMap.get(vente.getReference() + vente.getFab());
            if (article != null) {
                vente.setInstance(article.getDesignation());
            }
        });
        return allVentes;
    }

    @Override
    public void inputPiedVteStatus() {
        List<PiedVte> piedVteList = piedVteRepository.findAll();
        piedVteList.forEach(piedVte -> {
            String nBon= piedVte.getNbon();
            String codeCl = piedVte.getCodecl();
            if (redFactRepository.existsByCodeclAndNbon(codeCl, nBon)) {
                piedVte.setStatus("Facturée");
            } else {
                piedVte.setStatus("En Cours");
            }
            piedVteRepository.save(piedVte);
        });
    }
}
