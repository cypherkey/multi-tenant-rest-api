Introduction
============

This projects is a proof-of-concept for a multi-tenant REST based API. The API is secured using OAUTH2 and therefore acts as an
OAUTH2 resource server. The goal of this project is to demonstrate how a REST API can support multi-tenant with respect to 
delagated authorization.

There are two types of objects that can be managed by this API:
  * Company
  * User

There are 3 basic security roles that a user can have:
  * ROLE_SUPERADMIN - Allowed to do anything
  * ROLE_COMPANYADMIN:[COMPANY] - A client with this role is able to perform adminstrative functions on the company to which the roles is assigned.
  * ROLE_USER:[USER] - A client with this role is able to perform administrative functions on the user to which the role is assigned.


Data
====
The data is stored in a mysql table and sample data is included and loaded as part of the launch of the SpringBoot application.

Data flows from the database to the controller in the following manner:

Repository -> Model -> Mapper -> DTO -> Controller


Using the service
=================

This project is acting as an OAUTH2 resource server. As such an OAUTH2 bearer token is required. Currently this service is configured
to accept OAUTH2 tokens (non-JWT). These tokens contain no data as they are just a GUID. The authorization service must be running so
that the resource server can validate and obtain information on the user. 

Obtaining an access token
-------------------------
Using a testing tool like SoapUI, Advanced REST client or POSTMAN, a HTTP POST request must be made to the authorization server running on port 8081:

```
METHOD: POST
HEADER: Authorization: Basic bXlhcHA6c2VjcmV0
URL: http://localhost:8081/oauth/token?client_id=myapp&grant_type=password&scope=read&username=admin@provider.com&password=secret
```

This should return a HTTP 200 and an access token. For example:

```
{
"access_token": "4da99d56-59f4-47b1-a066-79d3cf4d4af3",
"token_type": "bearer",
"refresh_token": "c680406d-80ad-4fa6-8e33-aa6f05024e99",
"expires_in": 3033,
"scope": "read"
}
```

Using the token to access the service
-------------------------------------
Using the testing tool, a HTTP request is made to the resource server running on port 8082.

```
METHOD: GET
HEADER: Authorization: Bearer 4da99d56-59f4-47b1-a066-79d3cf4d4af3
URL: http://localhost:8082/resource-server/company/acme/user/acme-user1
```

This performs obtains the details on ACME-USER1.

User accounts
-------------
The following shows the available users and their roles.

| User              | Roles                   |
|:------------------|:------------------------|
|admin@provider.com | ROLE_SUPERADMIN         |
|admin@acme.com     | ROLE_COMPANYADMIN:ACME  |
|admin@initech.com  | ROLE_COMPANYADMIN:ACME, ROLE_COMPANYADMIN:INITECH |
|user1@acme.com     | ROLE_USER:ACME-USER1    |
|user2@acme.com     | ROLE_USER:ACME-USER1, ROLE_USER:ACME-USER2 |
|user1@initech.com  | ROLE_USER:INITECH-USER1 |
|user2@initech.com  | ROLE_USER:INITECH-USER2 |

The password for each of these user acocunts is secret.

Security
========

Authentication
--------------
Users authenticate to the sample OAUTH AS to obtain a JWT based access token.

Authorization
-------------
The OAUTH AS assigned roles/granted authorities for the user. These are evaluated when accessing the various controllers.
The following table desribes the required roles:

| Controller | Action | ROLE_SUPERUSER | ROLE_COMPANYAMDIN | ROLE_USER |
|:-----------|:-------|      :---:     |       :---:       |   :---:   |
| Company    | Add    |       X        |                   |           |
| Company    | Edit   |       X        |         X         |           |
| Company    | Delete |       X        |                   |           |
| User       | Add    |       X        |         X         |           |
| User       | Edit   |       X        |         X         |     X     |
| User       | Delete |       X        |         X         |           |

A ROLE_SUPERUSER is a static role that grants global rights across all objects. The ROLE_COMPANYAMDIN and ROLE_USER rights 
are assigned to particular objects. For example:
  * **ROLE_COMPANYAMDIN:INITECH** - This grants the client access to perform any action that requires the ROLE_COMPANYAMDIN role but only against the INITECH company.
  * **ROLE_USER:INITECH-USER1** - This grants the client access to perform any action that requires the ROLE_USER role but only against USER1 of the INITECH company.

This is currently implemented in the controller using the @PreAuthorize annotation. It directs the framework to call the RoleChecker.hasValidRole() method passing in the 
value of the company and user that are being passed on the query string.

Field level security
--------------------
The granted roles extend beyond the actions that can be performed against the controller. Specific fields
in the object can only be modified based on the role of the user. The following tables describes the necessary roles:

| Object  | Field        | ROLE_SUPERUSER (read) | ROLE_COMPANYAMDIN (read) | ROLE_USER (read) | ROLE_SUPERUSER (write) | ROLE_COMPANYAMDIN (write) | ROLE_USER (write) |
|:--------|:-------------|:--------------:       |:-----------------:       |:---------:       |:--------------:        |:-----------------:        |:---------:        |
| Company | name         |        X              |         X                |                  |       X                |                           |                   |
| Company | contactName  |        X              |         X                |                  |       X                |         X                 |                   |
| Company | contactEmail |        X              |         X                |                  |       X                |         X                 |                   |
| Company | maxAccounts  |        X              |         X                |                  |       X                |                           |                   |
| Company | maxsize      |        X              |                          |                  |       X                |                           |                   |
| User    | companyName  |        X              |         X                |                  |       X                |                           |                   |
| User    | login        |        X              |         X                |     X            |       X                |                           |                   |
| User    | password     |        X              |         X                |     X            |       X                |         X                 |     X             |
| User    | quota        |        X              |         X                |                  |       X                |                           |     X             |
| User    | enabled      |        X              |         X                |                  |       X                |         X                 |     X             |

This is is currently implemented in the mapper classes that ModelDTOMapper class. It uses reflections to copy data from the entity to the DTO or vise-versa and analyzes the ModelMapper annotations on the properties of the DTO class. For example:

```
public class UserDTO {
	protected String companyName;
    protected String login;
    
    @ModelMapper(readRoles = {"ROLE_SUPERADMIN", "ROLE_COMPANYADMIN"}, writeRoles = {"ROLE_SUPERADMIN", "ROLE_COMPANYADMIN"} )
    protected String password;

    @ModelMapper(readRoles = {"ROLE_SUPERADMIN"}, writeRoles = {"ROLE_SUPERADMIN"} )
    protected Long quota;

    @ModelMapper(readRoles = {"ROLE_SUPERADMIN"}, writeRoles = {"ROLE_SUPERADMIN"} )
    protected Boolean enabled;
}
```

The solution should be generic and ideally use annotations on the DTO. Logic should not be built-in to the program using if statements.

Tests
=====

OAUTH tests
-----------
The purpose of the following tests is to ensure the controller performs the necessary access control checks. The tests should emulate  the prinicipal and roles that would have been granted by the OAUTH2 bearer token. 

| Object | Test | Expected Result |
|--------|------|-----------------|
| Company | List all companies with role ROLE_SUPERADMIN | Successfully retrieve all 2 companies |
| Company | List all companies with role ROLE_COMPANYADMIN:INITECH | Success and result should only contain INITECH |
| Company | List all companies with roles ROLE_COMPANYADMIN:INITECH and ROLE_COMPANYADMIN:ACME | Successful and result should only contain INITECT and ACME and not UMBRELLA |
| Company | List all companies with role ROLE_USER:INITECH-USER1 | Access denied |
| Company | Create a new company with role ROLE_SUPERADMIN | Company is successfully created |
| Company | Create a new company with role ROLE_COMPANYADMIN:INITECH | Access denied |
| Company | Create a new company with role ROLE_USER:INITECH-USER1 | Access denied |
| Company | Retreive (get) the INITECH company with role ROLE_SUPERADMIN | Success and all fields are present |
| Company | Retreive (get) the INITECH company with role ROLE_COMPANYADMIN:INITECH | Success and fields maxAccounts and maxsize are null |
| Company | Retreive (get) the INITECH company with role ROLE_COMPANYADMIN:ACME | Access denied |
| Company | Retreive (get) the INITECH company with role ROLE_USER:INITECH-USER1 | Access denied |
| Company | Edit field maxAccounts of company object INITECH as ROLE_SUPERUSER | Success |
| Company | Edit field maxAccounts of company object INITECH as ROLE_COMPANYADMIN:INITECH | Access Denied |
| Company | Edit field maxAccounts of company object INITECH as ROLE_COMPANYADMIN:ACME | Access Denied |
| Company | Edit field maxAccounts of company object INITECH as ROLE_USER:INITECH-USER1 | Access Denied |
| Company | Edit field contactEmail of company object INITECH as ROLE_SUPERUSER | Success |
| Company | Edit field contactEmail of company object INITECH as ROLE_COMPANYADMIN:INITECH | Success |
| Company | Edit field contactEmail of company object INITECH as ROLE_COMPANYADMIN:ACME | Access Denied |
| Company | Edit field contactEmail of company object INITECH as ROLE_USER:INITECH-USER1 | Access Denied |

Field security tests
--------------------
The objective of these tests is to ensure the field level security properly works.

| Object | Test | Expected Result |
|--------|------|-----------------|
| Company | Get company as ROLE_SUPERADMIN | Success and all 5 attributes (name, contactName, contactEmail, maxAccounts, maxSize) are visible |
| Company | Get company as ROLE_COMPANYADMIN:[COMPANY] | Success but the maxSize attribute is null or missing |
| Company | Update contactEmail for a company as ROLE_SUPERADMIN | Success |
| Company | Update maxAccounts for a company as ROLE_SUPERADMIN | Success |
| Company | Update maxSize for a company as ROLE_SUPERADMIN | Success |
| Company | Update contactEmail for a company as ROLE_COMPANYADMIMN:[COMPANY] | Success |
| Company | Update maxAccounts for a company as ROLE_COMPANYADMIMN:[COMPANY] | Access denied |
| Company | Update maxSize for a company as ROLE_COMPANYADMIMN:[COMPANY] | Access denied |


To Do
=====
The primary items that are required are:

* Upgrade to latest spring-boot (1.5.4 or otherwise)
* Move application.properties to yml
* Evalution of the current implementation of field level security. Is there a better *Springy* way to implement this?
* Integration tests without the need for the OAUTH2 authorization server (AS)
* End to end tests that require the AS (?)

Reference/Notes
===============
* https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#el-access
* http://www.cowtowncoder.com/blog/archives/2011/02/entry_443.html
* https://stackoverflow.com/questions/28878488/dynamic-selection-of-jsonview-in-spring-mvc-controller
* http://www.baeldung.com/jackson-json-view-annotation
* https://github.com/timtebeek/resource-server-testing
* http://www.concretepage.com/spring-4/spring-4-security-junit-test-with-withmockuser-and-withuserdetails-annotation-example-using-webappconfiguration
* https://stackoverflow.com/questions/30638047/deserialize-jackson-in-spring-boot
* https://dzone.com/articles/simple-attribute-based-access-control-with-spring
* http://www.baeldung.com/rest-with-spring-course#master-class
