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
public class PiedFact implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String nFact;
    String typvte;
    String codecl;
    String datvte;
    int srtret;
    String brutht;
    String totrem;
    String btva0;
    String btva6;
    String mtva6;
    String btva12;
    String mtva12;
    String btva18;
    String mtva18;
    String tottva;
    int timbre;
    String totttc;
    String majoration;
    String nomclt;
    String adrclt;
    String tvaclt;
    String patclt;
    String netht;
    String mtfodec;
    String livrea;
    String RS;
}
