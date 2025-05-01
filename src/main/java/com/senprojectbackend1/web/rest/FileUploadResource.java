//package com.senprojectbackend1.web.rest;
//
//import com.cloudinary.Cloudinary;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.io.File;
//import java.util.Map;
//import java.util.UUID;
//import java.util.HashMap;
//
//@RestController
//@RequestMapping("/api")
//public class FileUploadResource {
//
//    private static final Logger log = LoggerFactory.getLogger(FileUploadResource.class);
//    private final Cloudinary cloudinary;
//
//    public FileUploadResource(Cloudinary cloudinary) {
//        this.cloudinary = cloudinary;
//    }
//
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Mono<ResponseEntity<Map<String, String>>> uploadFile(@RequestPart("file") FilePart filePart) {
//        String filename = UUID.randomUUID().toString() + "_" + filePart.filename();
//
//        return Mono.fromCallable(() -> File.createTempFile("temp", filename))
//            .flatMap(tempFile ->
//                filePart.transferTo(tempFile)
//                    .then(Mono.fromCallable(() -> {
//                        try {
//                            Map uploadResult = cloudinary.uploader().upload(tempFile, Map.of());
//                            tempFile.delete();
//                            log.debug("Réussit l'upload vers Cloudinary");
//
//                            // Créer une réponse structurée
//                            Map<String, String> response = new HashMap<>();
//                            // Utiliser secure_url comme URL principale
//                            String secureUrl = (String) uploadResult.get("secure_url");
//                            response.put("url", secureUrl);
//                            response.put("public_id", (String) uploadResult.get("public_id"));
//
//                            return ResponseEntity.ok(response);
//                        } catch (Exception e) {
//                            log.error("Erreur lors de l'upload vers Cloudinary", e);
//                            Map<String, String> errorResponse = new HashMap<>();
//                            errorResponse.put("error", "Erreur lors de l'upload: " + e.getMessage());
//                            return ResponseEntity
//                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                .body(errorResponse);
//                        }
//                    }))
//            );
//    }
//}
