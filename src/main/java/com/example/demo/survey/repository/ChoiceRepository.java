package com.example.demo.survey.repository;

import com.example.demo.survey.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
