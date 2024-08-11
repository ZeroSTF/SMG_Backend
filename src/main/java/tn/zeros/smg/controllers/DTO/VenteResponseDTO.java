package tn.zeros.smg.controllers.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VenteResponseDTO {
    String id;
    String commandeDate;
    String total;
    String status;
}
