package com.example.application.data.entities;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigInteger;

@Embeddable
public class PostPointLogCompositePK implements Serializable {

    @Column(name = "POST_ID", insertable=false, updatable=false)
    private BigInteger postId;

    @Column(name = "USER_ID", insertable=false, updatable=false)
    private BigInteger userId;

    public PostPointLogCompositePK(){}

    public PostPointLogCompositePK(BigInteger postId, BigInteger userId){

        this.postId = postId;
        this.userId = userId;

    }
    public BigInteger getPostId() {
        return postId;
    }

    public void setPostId(BigInteger postId) {
        this.postId = postId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostPointLogCompositePK that = (PostPointLogCompositePK) o;

        if (!postId.equals(that.postId)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        int result = postId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
