package com.leorizick.recipeapp.services.domain.service.mapping.recipe;

import com.leorizick.recipeapp.dto.account.AccountSummaryResponse;
import com.leorizick.recipeapp.dto.recipe.CommentSummaryResponse;
import com.leorizick.recipeapp.dto.recipe.IngredientSummaryResponse;
import com.leorizick.recipeapp.dto.recipe.RecipeCrudResponse;
import com.leorizick.recipeapp.dto.recipe.RecipeStepSummaryResponse;
import com.leorizick.recipeapp.entities.recipe.Comment;
import com.leorizick.recipeapp.entities.recipe.Recipe;
import com.leorizick.recipeapp.repositories.like.RecipeLikeRepository;
import com.leorizick.recipeapp.services.api.service.file.FileApiService;
import com.leorizick.recipeapp.services.domain.service.config.auth.AuthenticationContext;
import com.leorizick.recipeapp.services.domain.service.file.FileManagement;
import com.leorizick.recipeapp.services.domain.service.rating.RatingManagement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class RecipeResponseMapper {
    private final ModelMapper modelMapper;
    private final RecipeLikeRepository recipeLikeRepository;
    private final AuthenticationContext authenticationContext;
    private final RatingManagement ratingManagement;
    private final FileApiService fileApiService;

    @PostConstruct
    public void configure() {
        createFromRecipe();
    }

    private void createFromRecipe() {
        modelMapper.createTypeMap(Recipe.class, RecipeCrudResponse.class)
                .setConverter(mappingContext -> {
                    var src = mappingContext.getSource();
                    var author = modelMapper.map(src.getAuthor(), AccountSummaryResponse.class);

                    var comments = src.getComment()
                            .stream().sorted(Comparator.comparingLong(Comment::getId)
                                    .reversed()).limit(3)
                            .map(comment -> modelMapper.map(comment, CommentSummaryResponse.class))
                            .collect(Collectors.toList());

                    var steps = src.getStep()
                            .stream().map(step -> new RecipeStepSummaryResponse(step.getId(), step.getStep()))
                            .collect(Collectors.toList());

                    var ingredients = src.getIngredients().stream()
                            .map(ing -> new IngredientSummaryResponse(ing.getId(), ing.getIngredient()))
                            .collect(Collectors.toList());

                    var liker = authenticationContext.getAccountId();

                    var ratesCount = ratingManagement.getRatingCount(src.getId());
                    var userRating = ratingManagement.getUserRating(src.getId());

                    var rating = ratingManagement.getRatingTotal(src.getId());

                    var cover = fileApiService.searchRecipeCoverImgByRecipeId(src.getId());
                    var listImages = fileApiService.searchAllNotCoverRecipeImages(src.getId());

                    return RecipeCrudResponse.builder()
                            .id(src.getId())
                            .author(author)
                            .name(src.getName())
                            .description(src.getDescription())
                            .enabled(src.isEnabled())
                            .step(steps)
                            .ingredients(ingredients)
                            .comment(comments)
                            .category(src.getCategory().getName())
                            .categoryId(src.getCategory().getId())
                            .isLiked(recipeLikeRepository.isLiked(src.getId(), liker))
                            .likesCount(recipeLikeRepository.likeCount(src.getId()))
                            .ratesCount(ratesCount)
                            .rating(rating)
                            .userRating(userRating)
                            .cover(cover)
                            .recipeImages(listImages)
                            .build();
                });
    }
}
