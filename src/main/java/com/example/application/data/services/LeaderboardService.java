package com.example.application.data.services;

import com.example.application.data.entities.Post;
import com.example.application.data.repositories.PostsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class LeaderboardService {
    public enum LeaderboardType {TODAY, THIS_WEEK,THIS_MONTH, THIS_YEAR, ALL_TIME}
    public enum SortType {SORT_BY_POST}

    public static final int ELEMENTS = 5;

    private final PostsRepository postRep;
    private final LeaderboardType leaderboardType;
    private final BigInteger userId;


    public LeaderboardService(PostsRepository postRep, LeaderboardType leaderboardType, BigInteger userId) {
        this.postRep = postRep;
        this.leaderboardType = leaderboardType;
        this.userId = userId;

    }





}
