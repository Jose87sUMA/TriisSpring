package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "UserPointLogs", schema = "UBD3336", catalog = "")
@DynamicUpdate
public class UserPointLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "LOG_ID")
    private BigInteger logId;

    @Basic
    @Column(name = "BENEFICIARY_USER_ID")
    private BigInteger benfUserId;

    @Basic
    @Column(name = "PAYER_USER_ID")
    private BigInteger payerUserId;

    @Basic
    @Column(name = "POINTS")
    private BigInteger points;

    @Basic
    @Column(name = "LOG_DATE")
    private Date logDate;

    @Basic
    @Column(name = "DIRECT")
    private boolean direct;

    public UserPointLog(){}

    public UserPointLog(User userBen, User userPay, int points, boolean direct){

        this.logId = null;
        this.benfUserId = userBen.getUserId();
        this.payerUserId = userPay.getUserId();
        this.points = BigInteger.valueOf(points);
        this.logDate = Date.from(Instant.now());
        this.direct = direct;

    }

    public BigInteger getLogId() {
        return logId;
    }

    public BigInteger getBenfUserId() {
        return benfUserId;
    }

    public BigInteger getPayerUserId() {
        return payerUserId;
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
