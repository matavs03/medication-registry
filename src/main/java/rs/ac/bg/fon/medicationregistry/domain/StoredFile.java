package rs.ac.bg.fon.medicationregistry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "stored_file")
public class StoredFile {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String originalFileName;

    private String fileType;

    @Column(nullable = false)
    private String filePath;

    private long fileSize;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadedAt;


}
