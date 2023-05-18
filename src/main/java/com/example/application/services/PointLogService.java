package com.example.application.data.services;

import com.example.application.data.entities.PostsPointLog;
import com.example.application.data.entities.UserPointLog;
import com.example.application.data.repositories.PostPointLogRepository;
import com.example.application.data.repositories.UserPointLogRepository;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PointLogService {
    private final PostPointLogRepository postPointLogRep;
    private final UserPointLogRepository userPointLogRep;

    public PointLogService(PostPointLogRepository postPointLogRep, UserPointLogRepository userPointLogRep) {
        this.postPointLogRep = postPointLogRep;
        this.userPointLogRep = userPointLogRep;
    }

    PostsPointLog findLastInsertedPostLog(){
        return postPointLogRep.findLastInsertedLog();
    }

    UserPointLog findLastInsertedUserLog(){
        return userPointLogRep.findLastInsertedLog();
    }

    public void calculatePointLogHash(PostsPointLog postLog){
        String dataToHash = postLog.getPreviousHash() + postLog.getUserId() + postLog.getPostId() + postLog.getPoints() + postLog.getLogDate().toString() + postLog.isDirect();
        postLog.setCurrentHash(calculateEntryHash(dataToHash));
        postPointLogRep.save(postLog);
    }

    public void calculatePointLogHash(UserPointLog userLog){
        String dataToHash = userLog.getPreviousHash() + userLog.getBenfUserId() + userLog.getPayerUserId() + userLog.getPoints() + userLog.getLogDate().toString() + userLog.isDirect();
        userLog.setCurrentHash(calculateEntryHash(dataToHash));
        userPointLogRep.save(userLog);
    }

    /**
     * Calculates the hash for an entry given the String to hash
     *
     * @return
     */
    private String calculateEntryHash(String dataToHash) {
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException |
                 UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

}
