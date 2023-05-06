package com.example.demo.survey.repository.choice;

import com.example.demo.survey.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChoiceRepository extends JpaRepository<Choice, Long>, ChoiceRepositoryCustom {
    Optional<Choice> findByTitle(String checkAnswer);
}
