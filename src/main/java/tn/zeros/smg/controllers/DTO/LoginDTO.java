package tn.zeros.smg.controllers.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginDTO {
    private String email;
    private String password;
}