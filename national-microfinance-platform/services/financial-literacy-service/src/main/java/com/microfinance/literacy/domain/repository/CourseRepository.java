package com.microfinance.literacy.domain.repository;

import com.microfinance.literacy.domain.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByActiveTrue();
    List<Course> findByCategoryAndActiveTrue(String category);
    List<Course> findByLevelAndActiveTrue(String level);
    List<Course> findByLanguageAndActiveTrue(String language);
}
