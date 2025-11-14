package recipe_api.controller;

import recipe_api.model.Recipe;
import recipe_api.model.User;
import recipe_api.repo.RecipeRepository;
import recipe_api.repo.UserRepository;
import recipe_api.service.QueueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    private final RecipeRepository recipeRepo;
    private final UserRepository userRepo;
    private final QueueService queueService;

    public RecipeController(RecipeRepository recipeRepo, UserRepository userRepo, QueueService queueService) {
        this.recipeRepo = recipeRepo;
        this.userRepo = userRepo;
        this.queueService = queueService;
    }

    // Simple paged list (public)
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int page_size,
                                  @RequestParam(required = false) String q) {
        Page<Recipe> p = recipeRepo.findAll(PageRequest.of(page, Math.min(page_size, 100),
                Sort.by(Sort.Direction.DESC, "publishedAt")));
        return ResponseEntity.ok(Map.of("data", p.getContent(), "meta",
                Map.of("page", p.getNumber(), "page_size", p.getSize(), "total_pages", p.getTotalPages(), "total_items", p.getTotalElements())));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        User chef = userRepo.findById(userId).orElseThrow();
        Recipe r = new Recipe();
        r.setChef(chef);
        r.setTitle((String) body.get("title"));
        r.setSummary((String) body.get("summary"));
        r.setIngredients((String) body.get("ingredients"));
        r.setSteps((String) body.get("steps"));
        r.setStatus("DRAFT");
        recipeRepo.save(r);
        queueService.sendRecipeMessage(r.getId(), "CREATE");
        return ResponseEntity.status(201).body(r);
    }
}