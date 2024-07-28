package tn.zeros.smg.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Panier implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @JsonManagedReference
    User user;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<PanierArticle> panierArticles = new HashSet<>();

    public void addPanierArticle(PanierArticle panierArticle) {
        panierArticles.add(panierArticle);
        panierArticle.setPanier(this);
    }

    public void removePanierArticle(PanierArticle panierArticle) {
        panierArticles.remove(panierArticle);
        panierArticle.setPanier(null);
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            user.setPanier(this);
        }
    }
}
