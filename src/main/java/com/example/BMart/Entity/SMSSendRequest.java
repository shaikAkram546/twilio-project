package com.example.BMart.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
@Entity
@Data
@Table(name="otp_table")
public class SMSSendRequest {
	
	@Id
	@Column(name="sNo")
	private int sNo;
	@Column(name="phoneNum")
	private String phoneNum;
	private String name;
	private String otp;
	private String status;
	private String recentlogin;
	private String firstlogin;
	
	
	
}
