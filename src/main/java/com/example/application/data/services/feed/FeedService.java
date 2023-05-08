package com.example.application.data.services.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.repositories.PostsRepository;
import com.sun.jna.platform.win32.OaIdl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.*;

@Service
public abstract class FeedService {
    public static final int ELEMENTS = 5;
    public enum FeedType {DISCOVERY, FOLLOWING}
    public enum SortType {RECENT, POPULAR}
    SortType current;
    int index;
    PostsRepository postRep;
    HashMap<SortType, Sort> sorts = new HashMap<>();
    Pageable page;

    public FeedService(PostsRepository postRep) {
        this.postRep = postRep;
        initializeFeed();
        current = SortType.RECENT;
    }

    public void initializeFeed(){
        index = 0;
        sorts.put(SortType.RECENT, Sort.unsorted());
        sorts.put(SortType.POPULAR, Sort.by("likes").descending());
    }

    public void setSort(SortType st){
        reset();
        current = st;
    }
    public void reset(){
        index = 0;
    }

    public abstract List<Post> findNextNPosts();

    void nextPage(){
        index++;
    }
}
