import com.fasterxml.jackson.databind.ObjectMapper;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class RecipeWorker {
    private final ObjectMapper mapper = new ObjectMapper();

    @RabbitListener(queues = "recipes.queue")
    public void handleMessage(Object payload) {
        try {
            Map<String,Object> m = mapper.convertValue(payload, Map.class);
            String recipeId = (String) m.get("recipeId");
            String action = (String) m.get("action");
            System.out.println("Worker got message for recipe: " + recipeId + " action=" + action);
            File tmpDir = new File("./uploads/api/tmp/" + recipeId);
            if (!tmpDir.exists()) {
                System.out.println("No tmp dir for recipe " + recipeId);
                return;
            }
            File outDir = new File("./uploads/worker/recipes/" + recipeId);
            outDir.mkdirs();
            for (File f : tmpDir.listFiles()) {
                File out = new File(outDir, "resized_" + f.getName());
                try {
                    Thumbnails.of(f).size(1024, 768).toFile(out);
                    System.out.println("Resized " + f.getName());
                    // TODO: update DB image rows to processed=true (could use JPA here)
                } catch (IOException ex) {
                    System.err.println("Failed to resize " + f.getName() + ": " + ex.getMessage());
                }
            }
            // Optionally: update recipe.status -> PUBLISHED (call API or DB).
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}