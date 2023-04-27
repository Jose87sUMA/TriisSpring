package com.example.application.data.repositories;

import com.example.application.data.entities.Tree;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository("treesRepository")
public interface TreesRepository extends CrudRepository<Tree, BigInteger> {



}
