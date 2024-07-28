package tn.zeros.smg.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
public class PanierArticle implements Serializable {
    @EmbeddedId
    PanierArticleKey id;

    @ManyToOne
    @MapsId("panierId")
    @JoinColumn(name = "panier_id")
    @JsonBackReference
    Panier panier;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    Article article;

    int quantity;

}
