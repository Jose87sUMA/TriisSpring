package com.example.application.services;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.PostsRepository;
import com.example.application.data.repositories.UsersRepository;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Service that manages the leaderboards.
 */
@Service
public class LeaderboardService{
    private final UsersRepository userRep;
    private final PostsRepository postRep;

    public enum LeaderboardType {TODAY, THIS_WEEK,THIS_MONTH, THIS_YEAR, ALL_TIME, USERS}

    /**
     * @param postRep
     * @param userRep
     */
    public LeaderboardService(PostsRepository postRep, UsersRepository userRep) {
        this.userRep = userRep;
        this.postRep = postRep;
    }

    /**
     * @return 10 original pointed posts with the highest number of points posted today
     * @author Ksenia Myakisheva
     */
    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedToday(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now()));
    }
    /**
     * @return 10 original pointed posts with the highest number of points posted this week
     * @author Ksenia Myakisheva
     */
    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisWeek(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now().minusWeeks(1)));
    }
    /**
     * @return 10 original pointed posts with the highest number of points posted this month
     * @author Ksenia Myakisheva
     */
    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisMonth(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now().minusMonths(1)));
    }
    /**
     * @return 10 original pointed posts with the highest number of points posted this year
     * @author Ksenia Myakisheva
     */
    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisYear(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now().minusYears(1)));
    }

    /**
     * @return 10 original pointed posts with the highest number of points
     * @author Ksenia Myakisheva
     */
    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDesc(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullOrderByPointsDesc();
    }

    /**
     * @return 10 users with the highest number of points
     * @author Ksenia Myakisheva
     */
    public List<User>  findUsersHighestType1Points(){
        return userRep.findTenOrderByType1PointsDesc();
    }
}
