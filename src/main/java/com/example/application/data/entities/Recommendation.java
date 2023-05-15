package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name = "RECOMMENDATION", schema = "UBD3336", catalog = "")
@DynamicUpdate
@Transactional
public class Recommendation implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RECOMMENDATION_ID")
    private BigInteger recommendationId;

    @Basic
    @Column(name = "RECOMMENDED_USER_ID")
    private BigInteger recommendedUserId;

    @Basic
    @Column(name = "RECOMMENDATION_USER_ID")
    private BigInteger recommendationUserId;

    @Basic
    @Column(name = "SCORE")
    private BigInteger score;

    public Recommendation(){}

    public Recommendation(BigInteger recommendedUserId, BigInteger recommendationUserId, BigInteger score) {
        this.recommendedUserId = recommendedUserId;
        this.recommendationUserId = recommendationUserId;
        this.score = score;
    }

    public BigInteger getRecommendedUserId() {
        return recommendedUserId;
    }

    public BigInteger getRecommendationUserId() {
        return recommendationUserId;
    }

    public BigInteger getScore() {
        return score;
    }

}
