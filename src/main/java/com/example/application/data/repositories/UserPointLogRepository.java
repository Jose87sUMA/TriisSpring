package com.example.application.data.repositories;

import com.example.application.data.entities.PostsPointLog;
import com.example.application.data.entities.UserPointLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("userPointLogRepository")
public interface UserPointLogRepository extends CrudRepository<UserPointLog, BigInteger>  {

    List<UserPointLog> findAllByBenfUserIdOrderByPointsDesc (BigInteger benfUserId);

}
