package com.example.BMart.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="calling_numbers")
public class CallingNumbers {

	@Id
	@Column(name="s_no")
	private int sNo;
	@Column(name="phone_num")
	private String phoneNum;
	
	
}
