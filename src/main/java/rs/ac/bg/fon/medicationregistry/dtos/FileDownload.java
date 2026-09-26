package rs.ac.bg.fon.medicationregistry.dtos;

import org.springframework.core.io.Resource;

public record FileDownload(String originalFileName, String fileType, Resource resource) {
}
