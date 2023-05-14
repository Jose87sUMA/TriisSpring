package com.example.application.data.repositories;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository("reportRepository")

public interface ReportRepository extends CrudRepository<Report, BigInteger> {



}
