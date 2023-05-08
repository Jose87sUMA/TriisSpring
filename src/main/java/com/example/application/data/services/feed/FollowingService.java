package com.example.application.data.services.feed;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.PostsRepository;
import com.vaadin.flow.component.page.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class FollowingService extends FeedService{
    private BigInteger userId;
    public FollowingService(PostsRepository postRep) {
        super(postRep);
    }

    public void setUserId(BigInteger id) {
        this.userId = id;
    }

    @Override
    public List<Post> findNextNPosts() {
        List<Post> posts = postRep.findAllByUsersFollowedByUserId(PageRequest.of(index, ELEMENTS, sorts.get(current)), userId);
        nextPage();
        return posts;
    }
}
