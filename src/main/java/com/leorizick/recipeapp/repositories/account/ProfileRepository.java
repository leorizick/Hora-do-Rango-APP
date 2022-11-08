package com.leorizick.recipeapp.repositories.account;

import com.leorizick.recipeapp.entities.account.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByName(String name);
}
