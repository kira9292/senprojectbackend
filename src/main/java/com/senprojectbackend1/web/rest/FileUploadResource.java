package com.senprojectbackend1.web.rest;

import java.nio.file.Path;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class FileUploadResource {

    private static final Logger log = LoggerFactory.getLogger(FileUploadResource.class);
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart filePart) {
        String filename = UUID.randomUUID() + "-" + filePart.filename();

        Path path = Path.of(UPLOAD_DIR, filename);

        return filePart
            .transferTo(path)
            .thenReturn(ResponseEntity.ok("Fichier uploadé avec succès: " + filename))
            .onErrorResume(e -> {
                log.error("Erreur lors de l'upload", e);
                return Mono.just(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'upload: " + e.getMessage())
                );
            });
    }
}
