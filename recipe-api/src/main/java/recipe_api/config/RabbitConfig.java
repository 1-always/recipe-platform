package recipe_api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public DirectExchange recipeExchange() { return new DirectExchange("recipes.exchange"); }
    @Bean
    public Queue recipeQueue() { return new Queue("recipes.queue", true); }
    @Bean
    public Binding binding(Queue recipeQueue, DirectExchange recipeExchange) {
        return BindingBuilder.bind(recipeQueue).to(recipeExchange).with("recipes.key");
    }
}
