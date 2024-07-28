package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.zeros.smg.controllers.DTO.VenteResponseDTO;
import tn.zeros.smg.entities.PiedFact;
import tn.zeros.smg.entities.PiedVte;
import tn.zeros.smg.services.IServices.IUserService;
import tn.zeros.smg.services.IServices.IVenteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vente")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class VenteController {
    private final IVenteService venteService;
    private final IUserService userService;

    @GetMapping("/getAll")
    public List<VenteResponseDTO> getVentes() {
        List<PiedVte> piedVtes = venteService.retrieveAllVentes();
        if (piedVtes != null) {
            return piedVtes.stream().map(piedVte -> {
                return VenteResponseDTO.builder()
                        .id(piedVte.getId())
                        .date(piedVte.getDatvte())
                        .etat(piedVte.getValide())
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
}
