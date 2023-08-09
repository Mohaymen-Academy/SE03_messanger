package ir.mohaymen.iris.profile;

import ir.mohaymen.iris.media.Media;
import ir.mohaymen.iris.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "user_profiles")
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne
    @JoinColumn(name = "mediaId")
    private Media media;

    private Instant setAt;
}
