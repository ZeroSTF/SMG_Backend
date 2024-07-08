package tn.zeros.smg.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Confirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdAt;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name ="user_id")
    private User user;

    public Confirmation(User user){
        this.user=user;
        this.createdAt=LocalDateTime.now();
        this.token= UUID.randomUUID().toString();
    }
}
