package com.gcaldas.conceptualmodel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcaldas.conceptualmodel.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
