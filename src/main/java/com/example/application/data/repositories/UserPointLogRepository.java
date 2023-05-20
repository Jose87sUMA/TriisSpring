package com.example.application.data.repositories;

import com.example.application.data.entities.PostsPointLog;
import com.example.application.data.entities.UserPointLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("userPointLogRepository")
public interface UserPointLogRepository extends CrudRepository<UserPointLog, BigInteger>  {

    List<UserPointLog> findAllByBenfUserIdOrderByPointsDesc (BigInteger benfUserId);
    @Query(value = "select * from USER_POINT_LOGS WHERE LOG_ID IN (SELECT MAX(LOG_ID) FROM USER_POINT_LOGS)", nativeQuery = true)
    UserPointLog findLastInsertedLog();

}
