package tn.zeros.smg.controllers.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CurrentDTO {
    String id;
    String name;
    String email;
    String role;
    String avatar;
    String status;
}
