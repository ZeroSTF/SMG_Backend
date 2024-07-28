package tn.zeros.smg.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.zeros.smg.entities.enums.UStatus;

import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String code;
    String nom;
    String adresse;
    String secteur;
    int exonoration;
    int bloque;
    String idfiscal;
    String codetva;
    String dateeffet;
    String regcommerce;
    String responsable;
    String tel1;
    String tel2;
    String fax;
    String email;
    String magasinier1;
    String telmag1;
    String magasinier2;
    String telmag2;
    String magasinier3;
    String telmag3;
    String modereg;
    int nbjour;
    String banque;
    String rib;
    int crdplaf;
    String encour;
    int txrem;
    int TXMAJ;
    String banque2;
    String rib2;
    String banque3;
    String rib3;
    String RSUPP;
    String REGIME;
    int IMPRMS;
    String CR;
    String SOLDE;
    String ENC;
    String DATLIM;
    //Added stuff
    String password;
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> role;
    @Enumerated(EnumType.STRING)
    private UStatus status;
    String photomat;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    Panier panier;

    // UserDetails methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(code, user.getCode());
    }
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role;
    }
    @Override
    public String getUsername() {
        return this.code;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() { return true; }

    public User(String email, String password, String nom, String adresse, String codetva, String tel1, String tel2, String fax, String idfiscal) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.adresse = adresse;
        this.codetva = codetva;
        this.tel1 = tel1;
        this.tel2 = tel2;
        this.fax = fax;
        this.idfiscal = idfiscal;
        this.SOLDE="00";
        // Set default values for other fields
        this.role = new HashSet<>(); // Initialize empty set for roles
    }
}
