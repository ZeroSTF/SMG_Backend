package tn.zeros.smg.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PiedVte implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String nbon;
    String typvte;
    Long codecl;
    String datvte;
    int srtret;
    float brutht;
    float totrem;
    float btva0;
    float btva6;
    float mtva6;
    float btva12;
    float mtva12;
    float btva18;
    float mtva18;
    float tottva;
    int timbre;
    float totttc;
    float majoration;
    String nomclt;
    String adrclt;
    String tvaclt;
    String patclt;
    float netht;
    float mtfodec;
    String livrea;
    float RS;
}
