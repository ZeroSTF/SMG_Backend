package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.controllers.DTO.DetailsFactureDTO;
import tn.zeros.smg.entities.PiedFact;
import tn.zeros.smg.entities.RedFact;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.entities.Vente;
import tn.zeros.smg.services.IServices.IFactureService;
import tn.zeros.smg.services.IServices.IUserService;
import tn.zeros.smg.services.IServices.IVenteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/facture")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class FactureController {
    private final IFactureService factureService;
    private final IUserService userService;
    private final IVenteService venteService;

    @GetMapping("/getAll")
    public List<PiedFact> getFactures() {
        return factureService.retrieveAllFactures();
    }

    @GetMapping("/getAllCurrent")
    public List<PiedFact> getFacturesCurrent() {
        ////////////retrieving current code/////////////////////////////////
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentCode = authentication.getName();
        ////////////////////////////////////////////////////////////////////
        return factureService.retrieveFactByClient(currentCode);
    }

    @GetMapping("/getDetails/{nFact}")
    public ResponseEntity<DetailsFactureDTO> getDetailsFacture(@PathVariable String nFact) {
        PiedFact piedFact = factureService.retrieveFacture(nFact);
        List<Vente> lignes = venteService.retrieveAllLignesByPiedFact(nFact, piedFact.getCodecl());
        DetailsFactureDTO detailsFactureDTO = new DetailsFactureDTO(piedFact, lignes);
        return ResponseEntity.ok(detailsFactureDTO);
    }
}
