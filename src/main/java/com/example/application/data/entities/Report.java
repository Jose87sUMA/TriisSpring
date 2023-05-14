package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigInteger;

@Entity
@Table(name = "REPORT", schema = "UBD3336", catalog = "")
@DynamicUpdate
public class Report {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "REPORT_ID")
    private BigInteger reportId;

    @ManyToOne(fetch = FetchType.EAGER)
    private User reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REPORTED_POST")
    private Post reportedPost;

    @Basic
    @Column(name = "REPORT_MESSAGE")
    private String reportMessage;

    public Report(User authenticatedUser, Post post, String reason) {
        reporter = authenticatedUser;
        reportedPost = post;
        reportMessage = reason;
    }

    public Report() {
    }

    public void setreportId(BigInteger id) {
        this.reportId = id;
    }

    public BigInteger getId() {
        return reportId;
    }
    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public Post getReported_post() {
        return reportedPost;
    }

    public void setReportedPost(Post reportedPost) {
        this.reportedPost = reportedPost;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }
}
