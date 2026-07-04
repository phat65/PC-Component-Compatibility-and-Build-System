package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.model.product.Image;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.product.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageService {
    public static final int MAX_IMAGES_PER_PRODUCT = 8;
    public static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

    private static final Path UPLOAD_PATH = Paths.get("uploads", "images");
    private static final String IMAGE_URL_PREFIX = "/image/";
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    private final ImageRepository imageRepository;

    public ProductImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Transactional
    public List<Image> storeProductImages(Product product, List<MultipartFile> files) throws IOException {
        List<Image> images = new ArrayList<>();
        List<MultipartFile> validFiles = getUploadableFiles(files);
        validateNewProductImages(validFiles);

        for (MultipartFile file : validFiles) {
            try {
                Image image = new Image();
                image.setImageUrl(storeImageFile(file));
                image.setProduct(product);
                images.add(image);
            } catch (IOException | RuntimeException ex) {
                deleteStoredImageFiles(images);
                throw ex;
            }
        }

        return imageRepository.saveAll(images);
    }

    public void validateNewProductImages(List<MultipartFile> files) {
        validateProductImageUpload(files, 0);
    }

    public void validateProductImageUpload(List<MultipartFile> files, int existingImageCount) {
        List<MultipartFile> uploadableFiles = getUploadableFiles(files);
        int totalImageCount = existingImageCount + uploadableFiles.size();
        if (totalImageCount > MAX_IMAGES_PER_PRODUCT) {
            throw new IllegalArgumentException("A product can have at most " + MAX_IMAGES_PER_PRODUCT + " images.");
        }

        for (MultipartFile file : uploadableFiles) {
            validateImageFile(file);
        }
    }

    private String storeImageFile(MultipartFile file) throws IOException {
        validateImageFile(file);
        Files.createDirectories(UPLOAD_PATH);

        String extension = getFileExtension(file.getOriginalFilename());
        String newFilename = UUID.randomUUID() + "." + extension;
        Path filePath = UPLOAD_PATH.resolve(newFilename).normalize();

        if (!filePath.startsWith(UPLOAD_PATH)) {
            throw new IOException("Invalid image path");
        }

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return IMAGE_URL_PREFIX + newFilename;
    }

    @Transactional
    public boolean deleteProductImageById(Integer imageId) {
        if (imageId == null) {
            return false;
        }

        return imageRepository.findById(imageId)
                .map(image -> {
                    deleteImageFile(image);
                    imageRepository.delete(image);
                    return true;
                })
                .orElse(false);
    }

    public void deleteStoredImageFiles(Collection<Image> images) {
        if (images == null) {
            return;
        }
        images.forEach(this::deleteImageFile);
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("Each image must be 5MB or smaller");
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Only JPG and PNG images are allowed");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only JPG and PNG images are allowed");
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Image filename is required");
        }

        String fileName = Paths.get(originalFilename).getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("Image file extension is required");
        }
        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private List<MultipartFile> getUploadableFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();
    }

    private void deleteImageFile(Image image) {
        if (image == null || image.getImageUrl() == null || image.getImageUrl().isBlank()) {
            return;
        }

        if (!image.getImageUrl().startsWith(IMAGE_URL_PREFIX)) {
            return;
        }

        String filename = image.getImageUrl().substring(IMAGE_URL_PREFIX.length());
        Path filePath = UPLOAD_PATH.resolve(filename).normalize();
        if (!filePath.startsWith(UPLOAD_PATH)) {
            return;
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // Deleting database state should not be blocked by a missing or locked file.
        }
    }
}
