package rs.ac.bg.fon.medicationregistry.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "letter")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "letter_medication",
            joinColumns = @JoinColumn(name = "letter_id"),
            inverseJoinColumns = @JoinColumn(name = "medication_id")
    )
    private Set<Medication> medications = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "stored_file_id", nullable = false, unique = true)
    private StoredFile storedFile;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "admin_id",  nullable = false)
    private Admin admin;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
