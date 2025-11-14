package recipe_api.repo;

import recipe_api.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findByRecipeIdAndProcessedFalse(UUID recipeId);
}