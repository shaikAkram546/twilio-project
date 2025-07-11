package com.example.BMart.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.BMart.Entity.SMSSendRequest;

@Repository
public interface OTPRepository extends JpaRepository<SMSSendRequest, Integer> {

	
	SMSSendRequest findByPhoneNum(String phoneNum);

	SMSSendRequest deleteByPhoneNum(String phoneNum);
	
	
}
