package rs.ac.bg.fon.medicationregistry.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.medicationregistry.domain.StoredFile;
import rs.ac.bg.fon.medicationregistry.exceptions.FileStorageException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${storage.location}") String storageLocation) {
        this.root = Paths.get(storageLocation).toAbsolutePath().normalize();
        try{
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create directory: " + root);
        }
    }

    public StoredFile storeFile(MultipartFile file) {
        if(file.isEmpty()) {
            throw new FileStorageException("Cannot store empty file");
        }

        UUID id = UUID.randomUUID();
        String extension = getExtension(file.getOriginalFilename());
        String storedName = id + extension;
        Path target = root.resolve(storedName);

        try(InputStream in = file.getInputStream()){
            Files.copy(in, target);
        } catch (IOException e) {
            throw new FileStorageException("Cannot store file", e);
        }

        StoredFile storedFile = new StoredFile();
        storedFile.setId(id);
        storedFile.setOriginalFileName(file.getOriginalFilename());
        storedFile.setFileSize(file.getSize());
        storedFile.setFileType(file.getContentType());
        storedFile.setFilePath(storedName);
        return storedFile;
    }

    private String getExtension(String fileName) {
        if(fileName == null){
            return "";
        }
        int dot =  fileName.lastIndexOf('.');
        return dot == -1 ? "" : fileName.substring(dot);
    }

    public Resource loadFile(StoredFile storedFile) {
        Path target = root.resolve(storedFile.getFilePath());

        try {
            Resource resource = new UrlResource(target.toUri());
            if(!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File not found: " + storedFile.getOriginalFileName());
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new FileStorageException("Malformed URL", e);
        }
    }

    public void deleteFile(StoredFile storedFile) {
        try {
            Files.deleteIfExists(root.resolve(storedFile.getFilePath()));
        } catch (IOException e) {
            log.warn("Cannot delete file {}", storedFile.getFilePath(), e);
        }
    }

}
