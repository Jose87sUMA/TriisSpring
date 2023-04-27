package com.example.application.data.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class LikesCompositePK implements Serializable {

    private BigInteger userId;
    private BigInteger postId;

    public LikesCompositePK(BigInteger userId, BigInteger postId) {
        this.userId = userId;
        this.postId = postId;
    }

    private LikesCompositePK() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikesCompositePK that = (LikesCompositePK) o;
        return Objects.equals(userId, that.userId) && Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }


}
