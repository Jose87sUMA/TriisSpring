package com.example.application.data.services;

import com.example.application.data.entities.Post;
import com.example.application.data.repositories.PostsRepository;
import com.sun.jna.platform.win32.OaIdl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.*;

/**
 * Service to handle Feed requests. It works by sending data page by page.
 */
public class FeedService {
    public enum FeedType {DISCOVERY, FOLLOWING, PROFILE}
    public enum SortType {RECENT, POPULAR}

    public static final int ELEMENTS = 10;

    private final PostsRepository postRep;
    private final FeedType feedType;
    private final BigInteger userId;

    SortType currentSort;

    int index;

    final Map<SortType, Sort> sorts;

    /**
     * Constructs the service. And initializes a page.
     * @param postRep
     * @param feedType
     * @param userId
     */
    public FeedService(PostsRepository postRep, FeedType feedType, BigInteger userId) {
        this.postRep = postRep;
        this.feedType = feedType;
        this.userId = userId;

        sorts = Map.of(SortType.RECENT, Sort.by("post_date").descending(),
                SortType.POPULAR, Sort.by("points").descending());

        index = 0;
        currentSort = SortType.RECENT;

        //sorts.put(SortType.RECENT, Sort.unsorted());
        //sorts.put(SortType.POPULAR, Sort.by("likes").descending());
    }

    /**
     * Set the sort parameter to st.
     * @param st Sorting mode.
     */
    public void setSort(SortType st){
        reset();
        currentSort = st;
    }

    /**
     * Resets the page.
     */
    public void reset(){
        index = 0;
    }

    /**
     * Find the next N posts. N is set by the ELEMENTS variable of the class.
     * @return Returns a list of N posts found.
     */
    public List<Post> findNextNPosts(){
        List<Post> posts = null;
        Pageable page = PageRequest.of(index, ELEMENTS, sorts.get(currentSort));
        switch (feedType){
            case FOLLOWING -> posts = postRep.findAllByUsersFollowedByUserId(page, userId);
            case DISCOVERY -> posts = postRep.findAll(page);
            case PROFILE -> posts = postRep.findAllByUserId(page, userId);
        }
        nextPage();
        return posts;
    }

    /**
     * Go to the next page.
     */
    void nextPage(){
        index++;
    }
}
