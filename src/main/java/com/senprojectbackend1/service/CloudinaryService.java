package com.senprojectbackend1.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service
public class CloudinaryService {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Mono<String> uploadImage(MultipartFile file) {
        return Mono.fromCallable(() -> {
            try {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                return (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                LOG.error("Erreur lors de l'upload vers Cloudinary", e);
                throw new RuntimeException("Failed to upload image to Cloudinary", e);
            }
        });
    }

    /**
     * Upload une image encodée en base64 (avec ou sans préfixe data:) sur Cloudinary.
     * @param rawData la chaîne base64 (avec ou sans préfixe data:)
     * @param prefix préfixe pour le nom du fichier (ex: "profile", "team", "gallery")
     * @return Mono<String> url de l'image uploadée
     */
    public Mono<String> uploadBase64Image(String rawData, String prefix) {
        try {
            String base64Data;
            String contentType = "image/jpeg";
            if (rawData.startsWith("data:")) {
                int commaIndex = rawData.indexOf(",");
                String metadata = rawData.substring(5, commaIndex);
                contentType = metadata.split(";")[0];
                base64Data = rawData.substring(commaIndex + 1);
            } else {
                base64Data = rawData;
            }
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
            String extension = contentType.split("/")[1];
            String filename = prefix + "_" + System.currentTimeMillis() + "." + extension;
            org.springframework.web.multipart.MultipartFile file = new com.senprojectbackend1.service.util.ByteArrayMultipartFile(
                imageBytes,
                filename,
                contentType
            );
            return uploadImage(file);
        } catch (Exception e) {
            LOG.error("Erreur lors du décodage ou de l'upload de l'image base64", e);
            return Mono.error(e);
        }
    }
}
