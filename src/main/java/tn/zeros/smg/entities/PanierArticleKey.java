package tn.zeros.smg.entities;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PanierArticleKey implements Serializable {
    Long panierId;
    Long articleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PanierArticleKey)) return false;
        PanierArticleKey that = (PanierArticleKey) o;
        return panierId.equals(that.panierId) && articleId.equals(that.articleId);
    }

    @Override
    public int hashCode() {
        return panierId.hashCode() + articleId.hashCode();
    }
}
