package com.leorizick.recipeapp.repositories.recipe;

import com.leorizick.recipeapp.entities.recipe.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Modifying
    @Query("delete from Ingredient i where i.recipe.id = :recipeId")
    void deleteByRecipeId(Long recipeId);
}
