package recipe_api.controller;

import recipe_api.model.Image;
import recipe_api.repo.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recipes/{id}/images")
public class ImageController {
    private final ImageRepository imageRepo;

    @Value("${app.file.storage-path}")
    private String storagePath;

    public ImageController(ImageRepository imageRepo) {
        this.imageRepo = imageRepo;
    }

    @PostMapping
    public ResponseEntity<?> upload(@PathVariable("id") UUID recipeId,
                                    @RequestParam("files") List<MultipartFile> files,
                                    Authentication auth) throws IOException {
        if (auth == null) return ResponseEntity.status(401).build();
        Path tmp = Paths.get(storagePath, "tmp", recipeId.toString());
        Files.createDirectories(tmp);
        List<Image> saved = new ArrayList<>();
        for (MultipartFile f : files) {
            String fname = System.currentTimeMillis() + "_" + f.getOriginalFilename();
            Path target = tmp.resolve(fname);
            f.transferTo(target);
            Image img = new Image();
            img.setRecipeId(recipeId);
            img.setPath(target.toString());
            img.setProcessed(false);
            imageRepo.save(img);
            saved.add(img);
        }
        return ResponseEntity.status(201).body(saved);
    }
}