package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "PostsPointLogs", schema = "UBD3336", catalog = "")
@DynamicUpdate
@IdClass(PostPointLogCompositePK.class)
public class PostsPointLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "USER_ID")
    private BigInteger userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "POST_ID")
    private BigInteger postId;

    @EmbeddedId
    private PostPointLogCompositePK pointLogid;

    @Basic
    @Column(name = "POINTS")
    private BigInteger points;

    @Basic
    @Column(name = "LOG_DATE")
    private Date logDate;

    @Basic
    @Column(name = "DIRECT")
    private boolean direct;

    public PostsPointLog(){}

    public PostsPointLog(Post post, User user, int points, boolean direct){

        this.pointLogid = new PostPointLogCompositePK(post.getPostId(), user.getUserId());
        this.points = BigInteger.valueOf(points);
        this.logDate = Date.from(Instant.now());
        this.direct = direct;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getPostId() {
        return postId;
    }

    public void setPostId(BigInteger postId) {
        this.postId = postId;
    }
    public void setId(PostPointLogCompositePK pointLogid) {
        this.pointLogid = pointLogid;
    }

    public PostPointLogCompositePK getId() {
        return pointLogid;
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
}
