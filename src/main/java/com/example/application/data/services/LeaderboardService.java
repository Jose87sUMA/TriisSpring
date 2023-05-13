package com.example.application.data.services;

import com.example.application.data.entities.Post;
import com.example.application.data.repositories.PostsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class LeaderboardService{
    public enum LeaderboardType {TODAY, THIS_WEEK,THIS_MONTH, THIS_YEAR, ALL_TIME, USERS}
}
