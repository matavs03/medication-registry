package rs.ac.bg.fon.medicationregistry.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "medication")
public class Medication {

    @Id
    @Column(nullable = false, length = 20)
    private String id;

    @Column(length = 1000)
    private String inn;

    @Column(length = 1000)
    private String dosageForm;

    @Column(length = 1000)
    private String manufacturer;

    @Column(nullable = false, length = 1000)
    private String name;

    private String atc;

    private String type;

    private String prescriptionMode;

    private LocalDate licenseValidUntil;

    @Column(nullable = false)
    private LocalDateTime firstSeen;

    @Column(nullable = false)
    private LocalDateTime lastSeen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicationStatus status;
}
