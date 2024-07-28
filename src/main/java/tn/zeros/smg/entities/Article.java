package tn.zeros.smg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    String PUDEV;
    String PAHT;
    int MARGE;
    String PVHT;
    int STOCK;
    String QTECMD; //empty
    int STKEQV;
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
    int STKMAGP;

    @Override
    public String toString(){
        return designation;
    }

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<PanierArticle> panierArticles = new HashSet<>();
}
