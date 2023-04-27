package com.example.application.data.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class FollowCompositePK implements Serializable {

    private BigInteger userIdFollower;
    private BigInteger userIdFollowing;

    public FollowCompositePK(BigInteger userIdFollower, BigInteger userIdFollowing) {
        this.userIdFollower = userIdFollower;
        this.userIdFollowing = userIdFollowing;
    }

    private FollowCompositePK() {
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
