package com.redman.client.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    protected String name;
    protected String contactName;
    protected String contactEmail;
    protected Integer maxAccounts;
    protected Integer maxSize;
}
