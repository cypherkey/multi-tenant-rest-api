package com.redman.client.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "company",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"name"})
	})
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue
    protected int id;
    
    @Column(unique = true, updatable = false, nullable = false)
    @NotEmpty
    protected String name;

    @NotEmpty
    protected String contactName;
    
    @NotEmpty
    @Email
    protected String contactEmail;

    protected Integer maxAccounts;
    
    protected Integer maxSize;
}
