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
    Long id;
    String nbon;
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
    String imp;
    String valide;
    String time;
    String datvalid;
    String expedie;
    String cr;
    String datregl;
    String depot;
    String suite;
    String mag;
    String ctrl;
    String trsp;
    String nrecu;
    String datrecu;
    int nbcolie;
    String datecr;
    String prepare;
    int nblig;
    String qteg;
    String hcmd;
    String hprep;
    String hfinrep;
    String hvalid;
    String priorite;
    String datprep;
    String veriefiepar;
}
