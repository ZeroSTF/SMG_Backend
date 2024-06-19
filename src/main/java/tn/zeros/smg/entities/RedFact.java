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
public class RedFact implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String nfact;
    String datfact;
    Long codecl;
    String nbon;
    int srtret;
    String typvte;
}
