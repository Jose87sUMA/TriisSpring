package com.example.application.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Embeddable
public class FollowCompositePK implements Serializable {
    @Column(name = "USER_ID_FOLLOWER", insertable = false, updatable = false)
    private BigInteger userIdFollower;
    @Column(name = "USER_ID_FOLLOWING", insertable = false, updatable = false)
    private BigInteger userIdFollowing;

    public FollowCompositePK(BigInteger userIdFollower, BigInteger userIdFollowing) {
        this.userIdFollower = userIdFollower;
        this.userIdFollowing = userIdFollowing;
    }
    public FollowCompositePK() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowCompositePK that = (FollowCompositePK) o;
        return Objects.equals(userIdFollower, that.userIdFollower) && Objects.equals(userIdFollowing, that.userIdFollowing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIdFollower, userIdFollowing);
    }


}
