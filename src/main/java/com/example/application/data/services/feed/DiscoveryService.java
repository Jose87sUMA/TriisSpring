package com.example.application.data.services.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.repositories.PostsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscoveryService extends FeedService{
    public DiscoveryService(PostsRepository postRep) {
        super(postRep);
    }

    @Override
    public List<Post> findNextNPosts() {
        List<Post> posts = postRep.findAll(PageRequest.of(index, ELEMENTS, sorts.get(current)));
        nextPage();
        return posts;
    }
}
