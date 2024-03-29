package com.leorizick.recipeapp.repositories.comment;

import com.leorizick.recipeapp.entities.recipe.Comment;
import com.leorizick.recipeapp.entities.recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.recipe.id = :recipeId and enabled = true")
    Page<Comment> findAllByRecipe(Long recipeId, Pageable pageable);

}
