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
public class Vente implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String reference;
    String typvte;
    Long codecl;
    String datvte;
    String nbon;
    String fab;
    Long nordre;
    int srtret;
    int qte;
    float pvht;
    int remise;
    int tva;
    float pvttc;
    String magasinier;
    String RMQ;
    int STKMAG;
    String instance;
}
