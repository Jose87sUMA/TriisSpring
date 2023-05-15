package com.example.application.data.repositories;

import com.example.application.data.entities.Recommendation;
import com.example.application.data.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository("recommendationRepository")
public interface RecommendationRepository extends CrudRepository<Recommendation, BigInteger> {



}
