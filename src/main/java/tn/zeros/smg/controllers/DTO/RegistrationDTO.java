package tn.zeros.smg.controllers.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationDTO {
    String nom;
    String email;
    String password;
    String adresse;
    String codetva;
    String tel1;
    String tel2;
    String fax;
    String idfiscal;
}
