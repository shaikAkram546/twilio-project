package com.example.BMart.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.BMart.Entity.CallingNumbers;

public interface CallingNumbersRepository extends JpaRepository<CallingNumbers, Integer> {

	
	CallingNumbers findByPhoneNum(String phoneNum);
	
}
