package com.example.application.data.services;

import com.example.application.data.entities.Post;
import com.example.application.data.repositories.PostsRepository;
import com.sun.jna.platform.win32.OaIdl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.*;

public class FeedService {
    public enum FeedType {DISCOVERY, FOLLOWING}
    public enum SortType {RECENT, POPULAR}

    public static final int ELEMENTS = 5;

    private final PostsRepository postRep;
    private final FeedType feedType;
    private final BigInteger userId;

    SortType currentSort;

    int index;

    final Map<SortType, Sort> sorts = Map.of(SortType.RECENT, Sort.by("post_date").descending(),
                                             SortType.POPULAR, Sort.by("points").descending());

    public FeedService(PostsRepository postRep, FeedType feedType, BigInteger userId) {
        this.postRep = postRep;
        this.feedType = feedType;
        this.userId = userId;
        initializeFeed();
    }

    public void initializeFeed(){
        index = 0;
        //sorts.put(SortType.RECENT, Sort.unsorted());
        //sorts.put(SortType.POPULAR, Sort.by("likes").descending());
        currentSort = SortType.RECENT;
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
        switch (feedType){
            case FOLLOWING -> posts = postRep.findAllByUsersFollowedByUserId(page, userId);
            case DISCOVERY -> posts = postRep.findAll(page);
        }
        nextPage();
        return posts;
    }

    void nextPage(){
        index++;
    }
}
