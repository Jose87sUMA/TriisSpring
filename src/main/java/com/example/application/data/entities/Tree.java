package com.example.application.data.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "TREES", schema = "UBD3336", catalog = "")
public class Tree implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "TREE_ID")
    private BigInteger treeId;

    public BigInteger getTreeId() {
        return treeId;
    }

    public void setTreeId(BigInteger treeId) {
        this.treeId = treeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree that = (Tree) o;
        return Objects.equals(treeId, that.treeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treeId);
    }
}
