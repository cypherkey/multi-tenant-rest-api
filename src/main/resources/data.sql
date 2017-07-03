INSERT INTO company (name, contact_name, contact_email, max_accounts, max_size) 
  VALUES ('acme','John Doe','jdoe@acme.com',10,100);
INSERT INTO company (name, contact_name, contact_email, max_accounts, max_size) 
  VALUES ('initech','Bill Lumbergh','bill@initech.com',20,300);
  
INSERT INTO user (company_name, login, password, quota, enabled)
  VALUES ('acme', 'acme-user1', 'secret', 100, 1);
INSERT INTO user (company_name, login, password, quota, enabled)
  VALUES ('acme', 'acme-user2', 'secret', 100, 1);
INSERT INTO user (company_name, login, password, quota, enabled)
  VALUES ('acme', 'acme-user3', 'secret', 100, 1);
INSERT INTO user (company_name, login, password, quota, enabled)
  VALUES ('initech', 'initech-user1', 'secret', 200, 1);
INSERT INTO user (company_name, login, password, quota, enabled)
  VALUES ('initech', 'initech-user2', 'secret', 200, 1);
INSERT INTO user (company_name, login, password, quota, enabled)
  VALUES ('initech', 'initech-user3', 'secret', 200, 1);  

