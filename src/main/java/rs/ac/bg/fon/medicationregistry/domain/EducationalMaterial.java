package rs.ac.bg.fon.medicationregistry.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "educational_material")
public class EducationalMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "educational_material_medication",
            joinColumns = @JoinColumn(name = "educational_material_id"),
            inverseJoinColumns = @JoinColumn(name = "medication_id")
    )
    private Set<Medication> medications = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
    @JoinTable(
            name = "educational_material_stored_file",
            joinColumns = @JoinColumn(name = "educational_material_id"),
            inverseJoinColumns = @JoinColumn(name = "stored_file_id", unique = true)
    )
    private List<StoredFile> storedFiles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "admin_id",  nullable = false)
    private Admin admin;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
