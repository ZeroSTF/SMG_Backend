package tn.zeros.smg.controllers.DTO;

import lombok.*;
import tn.zeros.smg.entities.PiedVte;
import tn.zeros.smg.entities.Vente;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DetailsVenteDTO {
    PiedVte piedVte;
    List<Vente> lignes;
}
