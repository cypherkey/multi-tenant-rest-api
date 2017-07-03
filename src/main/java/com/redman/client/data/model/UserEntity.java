package com.redman.client.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"login"})
	})
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue
    protected int id;
    
    @NotEmpty
    @Column(updatable = false, nullable = false)
    protected String companyName;
    
    @Column(unique = true, updatable = false, nullable = false)
    @NotEmpty
    protected String login;

    @NotEmpty(message = "password cannot be null or empty")
    protected String password;
    
    protected Integer quota;
    
    protected Boolean enabled;
    
}
