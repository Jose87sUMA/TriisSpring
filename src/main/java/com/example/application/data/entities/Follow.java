package com.example.application.data.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@jakarta.persistence.Table(name = "FOLLOW", schema = "UBD3336", catalog = "")
@IdClass(FollowCompositePK.class)
public class Follow implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "USER_ID_FOLLOWER")
    private BigInteger userIdFollower;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "USER_ID_FOLLOWING")
    private BigInteger userIdFollowing;

    public FollowCompositePK getId() {
        return new FollowCompositePK(userIdFollower, userIdFollowing);
    }

    public BigInteger getUserIdFollower() {
        return userIdFollower;
    }

    public void setUserIdFollower(BigInteger userIdFollower) {
        this.userIdFollower = userIdFollower;
    }

    public BigInteger getUserIdFollowing() {
        return userIdFollowing;
    }

    public void setUserIdFollowing(BigInteger userIdFollowing) {
        this.userIdFollowing = userIdFollowing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow that = (Follow) o;
        return Objects.equals(userIdFollower, that.userIdFollower) && Objects.equals(userIdFollowing, that.userIdFollowing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIdFollower, userIdFollowing);
    }

}

