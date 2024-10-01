package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.controllers.DTO.DetailsVenteDTO;
import tn.zeros.smg.controllers.DTO.VenteResponseDTO;
import tn.zeros.smg.entities.PiedVte;
import tn.zeros.smg.entities.Vente;
import tn.zeros.smg.services.IServices.IVenteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vente")
public class VenteController {
    private final IVenteService venteService;

    @GetMapping("/getAll")
    public List<VenteResponseDTO> getVentes() {
        List<PiedVte> piedVtes = venteService.retrieveAllVentes();
        if (piedVtes != null) {
            return piedVtes.stream().map(piedVte -> {
                return VenteResponseDTO.builder()
                        .id(piedVte.getNbon())
                        .commandeDate(piedVte.getDatvte())
                        .status(piedVte.getStatus())
                        .total(piedVte.getTotttc())
                        .build();
            }).toList();
        }
        return null;
    }

    @GetMapping("/getAllCurrent")
    public List<PiedVte> getFacturesCurrent() {
        ////////////retrieving current code/////////////////////////////////
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentCode = authentication.getName();
        ////////////////////////////////////////////////////////////////////
        
        return venteService.retrieveVteByClient(currentCode);
    }

    @GetMapping("/getDetails/{id}")
    public ResponseEntity<DetailsVenteDTO> getDetailsFacture(@PathVariable Long id) {
        PiedVte piedVte = venteService.retrievePiedVteById(id);
        List<Vente> lignes = venteService.retrieveAllLignesByPiedVte(piedVte.getNbon(), piedVte.getCodecl());
        DetailsVenteDTO detailsVenteDTO = new DetailsVenteDTO(piedVte, lignes);
        return ResponseEntity.ok(detailsVenteDTO);
    }
}
