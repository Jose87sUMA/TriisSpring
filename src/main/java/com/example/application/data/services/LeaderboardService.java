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

    SortType currentSort;

    int index;

    final Map<SortType, Sort> sorts = Map.of(SortType.SORT_BY_POST, Sort.by("points").descending());

    public LeaderboardService(PostsRepository postRep, LeaderboardType leaderboardType, BigInteger userId) {
        this.postRep = postRep;
        this.leaderboardType = leaderboardType;
        this.userId = userId;
        initializeLeaderboard();
    }

    public void initializeLeaderboard(){
        index = 0;
    }

    public void setSort(SortType st){
        reset();
        currentSort = st;
    }
    public void reset(){
        index = 0;
    }

    public List<Post> findNextNPosts(){
        List<Post> posts = null;
        Pageable page = PageRequest.of(index, ELEMENTS, sorts.get(currentSort));
        switch (leaderboardType){
            case TODAY -> posts = postRep.findAllByUsersFollowedByUserId(page, userId);
            case THIS_WEEK -> posts = postRep.findAll(page);
            default -> posts = postRep.findAll(page);
        }
        nextPage();
        return posts;
    }

    void nextPage(){
        index++;
    }
}
