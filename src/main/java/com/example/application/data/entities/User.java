package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

@Entity
@Table(name = "USERS", schema = "UBD3336", catalog = "")
@DynamicUpdate
@Transactional
public class User implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "USER_ID")
    private BigInteger userId;
    @Basic
    @Column(name = "USERNAME")
    private String username;
    @Basic
    @Column(name = "PASSWORD")
    private String password;
    @Basic
    @Column(name = "EMAIL")
    private String email;
    @Basic
    @Column(name = "TYPE_1_POINTS")
    private BigInteger type1Points;
    @Basic
    @Column(name = "TYPE_2_POINTS")
    private BigInteger type2Points;
    @Basic
    @Column(name = "VERIFIED")
    private String verified;
    @Basic
    @Column(name = "PROFILE_PICTURE")
    private byte[] profilePicture;
    @Basic
    @Column(name = "TREE_ID")
    private BigInteger treeId;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @OneToMany(mappedBy = "reporter")
    private List<Report> reports;


    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigInteger getType1Points() {
        return type1Points;
    }

    public void setType1Points(BigInteger type1Points) {
        this.type1Points = type1Points;
    }

    public BigInteger getType2Points() {
        return type2Points;
    }

    public void setType2Points(BigInteger type2Points) {
        this.type2Points = type2Points;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public BigInteger getTreeId() {
        return treeId;
    }

    public void setTreeId(BigInteger treeId) {
        this.treeId = treeId;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

   /* public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }*/

    public User(){}

    public User(String newUsername, String newPassword, String newEmail){

        username = newUsername;
        password = newPassword;
        email = newEmail;
        type1Points = BigInteger.valueOf(500);
        type2Points = BigInteger.valueOf(100);
        verified = "N";
        profilePicture = null;
        //tree = new Tree();

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
