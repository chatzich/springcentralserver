package com.centralserver.repository;

import com.centralserver.model.NumberArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NumberArrayRepository extends JpaRepository<NumberArray, Long> {
}
