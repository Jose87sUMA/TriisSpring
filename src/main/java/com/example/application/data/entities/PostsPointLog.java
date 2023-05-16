package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;

@Entity
@Table(name = "PostsPointLogs", schema = "UBD3336", catalog = "")
@DynamicUpdate
public class PostsPointLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "LOG_ID")
    private BigInteger logId;

    @Basic
    @Column(name = "USER_ID")
    private BigInteger userId;

    @Basic
    @Column(name = "POST_ID")
    private BigInteger postId;

    @Basic
    @Column(name = "POINTS")
    private BigInteger points;

    @Basic
    @Column(name = "LOG_DATE")
    private Date logDate;

    @Basic
    @Column(name = "DIRECT")
    private boolean direct;

    @Basic
    @Column(name = "PREVIOUS_HASH")
    private String previousHash;

    @Basic
    @Column(name = "CURRENT_HASH")
    private String currentHash;

    public PostsPointLog(){}

    public PostsPointLog(Post post, User user, int points, boolean direct, String previousHash){

        this.logId = null;
        this.userId = user.getUserId();
        this.postId = post.getPostId();
        this.points = BigInteger.valueOf(points);
        this.logDate = Date.from(Instant.now());
        this.direct = direct;
        this.previousHash = previousHash;

    }


    public BigInteger getLogId() {
        return logId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public BigInteger getPostId() {
        return postId;
    }

    public BigInteger getPoints() {
        return points;
    }

    public void setPoints(BigInteger points) {
        this.points = points;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }

}
