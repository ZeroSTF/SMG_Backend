package tn.zeros.smg.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.zeros.smg.entities.PiedFact;
import tn.zeros.smg.entities.RedFact;
import tn.zeros.smg.repositories.PiedFactRepository;
import tn.zeros.smg.repositories.RedFactRepository;
import tn.zeros.smg.services.IServices.IFactureService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class FactureService implements IFactureService {
    private final PiedFactRepository piedFactRepository;
    private final RedFactRepository redFactRepository;

    @Override
    public PiedFact retrieveFacture(String nFact) {
        return piedFactRepository.findBynFact(nFact);
    }

    @Override
    public List<PiedFact> retrieveAllFactures() {
        return piedFactRepository.findAll();
    }

    @Override
    public List<PiedFact> retrieveFactByClient(String codeCl) {
        return piedFactRepository.findByCodecl(codeCl);
    }

    @Override
    public List<RedFact> retrieveAllRedFactByPiedFact(String nFact) {
        return redFactRepository.findByNfact(nFact);
    }
}
