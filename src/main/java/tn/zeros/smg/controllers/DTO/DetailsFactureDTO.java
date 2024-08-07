package tn.zeros.smg.controllers.DTO;

import lombok.*;
import tn.zeros.smg.entities.PiedFact;
import tn.zeros.smg.entities.Vente;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DetailsFactureDTO {
    PiedFact piedFact;
    List<Vente> lignes;
}
