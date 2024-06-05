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
public class Article implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String frn;
    String reference;
    String designation;
    int fodec;
    int tva;
    String ngp;
    String bloque;
    int qmin;
    int qmax;
    String groupe;
    String voiture;
    String casier;
    String desigbliv;
    String ecofiltre;
    String QEMB;
    String DDATENT;
    int DQTEENT;
    int REPLICAT;
    float PUDEV;
    float PAHT;
    int MARGE;
    float PVHT;
    int STOCK;
    String QTECMD; //empty
    int STKEVQV;
    int ARRIVAGE; //all 0
    int STKMAG;
    String VALIDE;
    String VTE;
    int RESTE;
    int STKMAGA;
    int STKPROV;
    String DATPROV;
    int CUMENT;
    String CBARRE;
    int QCART;
}
