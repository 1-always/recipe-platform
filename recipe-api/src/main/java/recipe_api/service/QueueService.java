package recipe_api.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class QueueService {
    private final AmqpTemplate amqp;

    public QueueService(AmqpTemplate amqp) { this.amqp = amqp; }

    public void sendRecipeMessage(UUID recipeId, String action) {
        var payload = Map.of("recipeId", recipeId.toString(), "action", action);
        amqp.convertAndSend("recipes.exchange", "recipes.key", payload);
    }
}
