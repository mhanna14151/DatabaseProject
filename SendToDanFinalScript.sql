drop schema if exists project;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema project
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS project DEFAULT CHARACTER SET utf8 ;
USE project ;


-- -----------------------------------------------------
-- Table project.Person
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Person (
  person_id INT NOT NULL AUTO_INCREMENT,
  person_first_name VARCHAR(45) NOT NULL,
  person_last_name VARCHAR(45) NOT NULL,
  person_email VARCHAR(45) NOT NULL,
  person_password VARCHAR(45) NOT NULL,
  PRIMARY KEY (person_id),
  UNIQUE INDEX person_email_UNIQUE (person_email ASC))
ENGINE = InnoDB;

insert into Person (person_first_name, person_last_name, person_email, person_password) values 
('System', 'Admin', 'admin@ars.com', 'password'), 		-- 1
('Alice', 'Adams', 'alice@adams.com', 'password'), 		-- 2
('Bob', 'Barnes', 'bob@barnes.com', 'password'), 		-- 3
('Clint', 'Carson', 'clint@carson.com', 'password'), 	-- 4
('Dave', 'Davis', 'dave@davis.com', 'password'),		-- 5
('Ed', 'Edwards', 'ed@edwards.com', 'password'),		-- 6
('Frank', 'Farmer', 'frank@farmer.com', 'password'),	-- 7
('Greg', 'Gregson', 'greg@gregson.com', 'password'),	-- 8
('Henry', 'Harris', 'henry@harris.com', 'password');	-- 9


-- -----------------------------------------------------
-- Table project.Admin
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Admin (
  admin_id INT NOT NULL,
  PRIMARY KEY (admin_id),
  CONSTRAINT admin_person_fk
    FOREIGN KEY (admin_id)
    REFERENCES project.Person (person_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

insert into Admin values (1);	-- person 1 is a system admin


-- -----------------------------------------------------
-- Table project.Users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Users (
  user_id INT NOT NULL,
  user_phone_number VARCHAR(45) NOT NULL,
  PRIMARY KEY (user_id),
  CONSTRAINT user_person_fk
    FOREIGN KEY (user_id)
    REFERENCES project.Person (person_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

insert into Users values	-- persons 2-9 are users (with phone numbers)
(2, '617-555-0002'),
(3, '617-555-0003'),
(4, '617-555-0004'),
(5, '617-555-0005'),
(6, '617-555-0006'),
(7, '617-555-0007'),
(8, '617-555-0008'),
(9, '617-555-0009');


-- -----------------------------------------------------
-- Table project.Owners
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Owners (
  owner_id INT NOT NULL,
  owner_routing_number VARCHAR(45) NOT NULL,
  PRIMARY KEY (owner_id),
  CONSTRAINT owner_user_fk
    FOREIGN KEY (owner_id)
    REFERENCES project.Users (user_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

insert into Owners values	-- only users 2, 3, 4, 7, 8, 9 are owners initially
(2, '222-222'),
(3, '333-333'),
(4, '444-444'),
(7, '222-222'),
(8, '333-333'),
(9, '444-444');


-- -----------------------------------------------------
-- Table project.Customer
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Customer (
  customer_id INT NOT NULL,
  customer_status ENUM('STANDARD', 'PREFERRED') NOT NULL DEFAULT 'STANDARD', 
  PRIMARY KEY (customer_id),
  CONSTRAINT customer_user_fk
    FOREIGN KEY (customer_id)
    REFERENCES project.Users (user_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

insert into Customer values		-- all users are customers initially, with a customer_status of 'STANDARD'
(2, 'STANDARD'),
(3, 'STANDARD'),
(4, 'STANDARD'),
(5, 'STANDARD'),
(6, 'STANDARD'),
(7, 'STANDARD'),
(8, 'STANDARD'),
(9, 'STANDARD');


-- -----------------------------------------------------
-- Table project.Make
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Make (
  make_name VARCHAR(45) NOT NULL,
  make_address VARCHAR(45) NOT NULL,
  PRIMARY KEY (make_name))
ENGINE = InnoDB;

INSERT INTO MAKE VALUES ('AUDI', 'Germany');
INSERT INTO MAKE VALUES ('TESLA', 'California');
INSERT INTO MAKE VALUES ('GMC', 'Detroit');
INSERT INTO MAKE VALUES ('FORD', 'Detroit');


-- -----------------------------------------------------
-- Table project.Model
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Model (
  model_name VARCHAR(45) NOT NULL,
  make_name VARCHAR(45) NOT NULL,
  PRIMARY KEY (model_name),
  CONSTRAINT model_make_fk
    FOREIGN KEY (make_name)
    REFERENCES project.Make (make_name)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

INSERT INTO MODEL VALUES ('A4', 'AUDI');
INSERT INTO MODEL VALUES ('A6', 'AUDI');
INSERT INTO MODEL VALUES ('A8', 'AUDI');
INSERT INTO MODEL VALUES ('MODEL S', 'TESLA');
INSERT INTO MODEL VALUES ('MODEL X', 'TESLA');
INSERT INTO MODEL VALUES ('MODEL 3', 'TESLA');
INSERT INTO MODEL VALUES ('FOCUS', 'FORD');
INSERT INTO MODEL VALUES ('EXPLORER', 'FORD');
INSERT INTO MODEL VALUES ('YUKON', 'GMC');


-- -----------------------------------------------------
-- Table project.Type_of
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Type_Of (
  type_id INT NOT NULL AUTO_INCREMENT,
  type_description ENUM('SEDAN', 'LUXURY', 'SUV') NOT NULL,
  type_base_rate DECIMAL(9,2) NOT NULL,
  UNIQUE INDEX type_description_UNIQUE (type_description ASC),
  PRIMARY KEY (type_id))
ENGINE = InnoDB;

INSERT INTO TYPE_OF VALUES (null, 'SEDAN', 1);
INSERT INTO TYPE_OF VALUES (null, 'SUV', 1.33);
INSERT INTO TYPE_OF VALUES (null, 'LUXURY', 1.66);


-- -----------------------------------------------------
-- Table project.Years
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Years (
  year_id INT NOT NULL,
  year_base_rate DECIMAL(9,2) NOT NULL,
  PRIMARY KEY (year_id))
ENGINE = InnoDB;

INSERT INTO YEARS VALUES (2015, 1);
INSERT INTO YEARS VALUES (2016, 1.05);
INSERT INTO YEARS VALUES (2017, 1.10);
INSERT INTO YEARS VALUES (2018, 1.15);


-- -----------------------------------------------------
-- Table project.Model_Year
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Model_Year (
  model_name VARCHAR(45) NOT NULL,
  year_id INT NOT NULL,
  type_id INT NOT NULL,
  PRIMARY KEY (model_name, year_id),
  INDEX type_fk_idx (type_id ASC),
  INDEX year_fk_idx (year_id ASC),
  CONSTRAINT model_year_model_fk
    FOREIGN KEY (model_name)
    REFERENCES project.Model (model_name)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT model_year_type_fk
    FOREIGN KEY (type_id)
    REFERENCES project.Type_of (type_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT model_year_year_fk
    FOREIGN KEY (year_id)
    REFERENCES project.Years (year_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

INSERT INTO MODEL_YEAR VALUES ('A4', 2015, 3);
INSERT INTO MODEL_YEAR VALUES ('A6', 2015, 3);
INSERT INTO MODEL_YEAR VALUES ('A8', 2015, 3);
INSERT INTO MODEL_YEAR VALUES ('A4', 2016, 1);
INSERT INTO MODEL_YEAR VALUES ('A6', 2016, 3);
INSERT INTO MODEL_YEAR VALUES ('A8', 2016, 3);
INSERT INTO MODEL_YEAR VALUES ('A4', 2017, 1);
INSERT INTO MODEL_YEAR VALUES ('A6', 2017, 3);
INSERT INTO MODEL_YEAR VALUES ('A8', 2017, 3);
INSERT INTO MODEL_YEAR VALUES ('MODEL S', 2015, 3);
INSERT INTO MODEL_YEAR VALUES ('MODEL X', 2015, 2);
INSERT INTO MODEL_YEAR VALUES ('MODEL S', 2016, 3);
INSERT INTO MODEL_YEAR VALUES ('MODEL X', 2016, 3);
INSERT INTO MODEL_YEAR VALUES ('MODEL 3', 2017, 1);
INSERT INTO MODEL_YEAR VALUES ('MODEL S', 2017, 3);
INSERT INTO MODEL_YEAR VALUES ('MODEL X', 2017, 3);
INSERT INTO MODEL_YEAR VALUES ('FOCUS', 2017, 1);
INSERT INTO MODEL_YEAR VALUES ('FOCUS', 2018, 1);
INSERT INTO MODEL_YEAR VALUES ('EXPLORER', 2017, 2);
INSERT INTO MODEL_YEAR VALUES ('EXPLORER', 2016, 2);
INSERT INTO MODEL_YEAR VALUES ('YUKON', 2015, 2);
INSERT INTO MODEL_YEAR VALUES ('YUKON', 2016, 2);
INSERT INTO MODEL_YEAR VALUES ('YUKON', 2017, 2);


-- -----------------------------------------------------
-- Table project.Vehicle
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Vehicle (
  vehicle_id INT NOT NULL AUTO_INCREMENT,
  vehicle_vin VARCHAR(45) NOT NULL,
  vehicle_color VARCHAR(45) NOT NULL,
  vehicle_plate_number VARCHAR(45) NOT NULL,
  model_name VARCHAR(45) NOT NULL,
  year_id INT NOT NULL,
  owner_id INT NOT NULL,
  PRIMARY KEY (vehicle_id),
  INDEX model_year_fk_idx (model_name ASC, year_id ASC),
  INDEX owner_fk_idx (owner_id ASC),
  CONSTRAINT vehicle_model_year_fk
    FOREIGN KEY (model_name , year_id)
    REFERENCES project.Model_Year (model_name , year_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT vehicle_owner_fk
    FOREIGN KEY (owner_id)
    REFERENCES project.Owners (owner_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table project.Registration
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Registration (
  registration_id INT NOT NULL AUTO_INCREMENT,
  vehicle_id INT NOT NULL,
  admin_id INT,
  registration_status ENUM('PENDING','ACTIVE','INACTIVE') NOT NULL DEFAULT 'PENDING',
  registration_status_change_date TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (registration_id),
  INDEX vehicle_fk_idx (vehicle_id ASC),
  INDEX admin_fk_idx (admin_id ASC),
  CONSTRAINT registration_vehicle_fk
    FOREIGN KEY (vehicle_id)
    REFERENCES project.Vehicle (vehicle_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT registration_admin_fk
    FOREIGN KEY (admin_id)
    REFERENCES project.Admin (admin_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table project.Trip
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Trip (
  trip_id INT NOT NULL AUTO_INCREMENT,
  trip_start_x DECIMAL(8,6) NOT NULL,
  trip_start_y DECIMAL(8,6) NOT NULL,
  trip_start_time TIMESTAMP NOT NULL,
  vehicle_id INT NOT NULL,
  PRIMARY KEY (trip_id),
  INDEX vehicle_fk_idx (vehicle_id ASC),
  CONSTRAINT trip_vehicle_fk
    FOREIGN KEY (vehicle_id)
    REFERENCES project.Vehicle (vehicle_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table project.Service
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Service (
  service_id INT NOT NULL,
  service_description VARCHAR(45) NOT NULL,
  service_duration TIME NOT NULL,
  PRIMARY KEY (service_id),
  CONSTRAINT service_trip_fk
    FOREIGN KEY (service_id)
    REFERENCES project.Trip (trip_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table project.One_Way
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.One_Way (
  one_way_id INT NOT NULL,
  one_way_destination_x DECIMAL(8,6) NOT NULL,
  one_way_destination_y DECIMAL(8,6) NOT NULL,
  one_way_distance DECIMAL(9,2) NOT NULL,
  one_way_avg_mph DECIMAL(9,2) NOT NULL,
  PRIMARY KEY (one_way_id),
  CONSTRAINT one_way_trip_fk
    FOREIGN KEY (one_way_id)
    REFERENCES project.Trip (trip_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table project.Personal
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Personal (
  personal_id INT NOT NULL,
  PRIMARY KEY (personal_id),
  CONSTRAINT personal_one_way_fk
    FOREIGN KEY (personal_id)
    REFERENCES project.One_Way (one_way_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table project.Credit_Card
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Credit_Card (
  credit_card_id INT NOT NULL AUTO_INCREMENT,
  customer_id INT NOT NULL,
  credit_card_status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE', 
  credit_card_name VARCHAR(45) NOT NULL,
  credit_card_number BIGINT NOT NULL,
  credit_card_cvv INT NOT NULL,
  credit_card_type ENUM('AMERICAN EXPRESS', 'CAPITAL ONE',
   'CHASE', 'CITIBANK', 'DISCOVER', 'MASTERCARD', 'VISA') NOT NULL,
  credit_card_expiration_date DATE NOT NULL,
  PRIMARY KEY (credit_card_id),
  INDEX customer_id_idx (customer_id ASC),
  CONSTRAINT credit_card_customer_fk
    FOREIGN KEY (customer_id)
    REFERENCES project.Customer (customer_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

insert into credit_card (customer_id, credit_card_status, credit_card_name, credit_card_number, 
							credit_card_cvv, credit_card_type, credit_card_expiration_date) values
(2, 'ACTIVE', 'Alice Adams', 4837465362738746, 943, 'AMERICAN EXPRESS', '2020-01-01'),	-- 1
(2, 'ACTIVE', 'Alice Adams', 4827457238428348, 426, 'CAPITAL ONE', '2019-02-01'),		-- 2
(2, 'ACTIVE', 'Alice Adams', 5432738482348259, 725, 'CHASE', '2021-03-01'),				-- 3
(3, 'ACTIVE', 'Robert Barnes', 1435671984519824, 235, 'CITIBANK', '2023-04-01'),		-- 4
(3, 'ACTIVE', 'Robert Barnes', 5671495864951873, 753, 'DISCOVER', '2022-05-01'),		-- 5
(4, 'ACTIVE', 'Clint Carson', 1049530570390535, 135, 'MASTERCARD', '2018-06-01'),		-- 6
(4, 'ACTIVE', 'Clint Carson', 5674109512794105, 754, 'VISA', '2025-07-01'),				-- 7
(5, 'ACTIVE', 'David Davis', 9832184275612974, 834, 'AMERICAN EXPRESS', '2024-08-01'),	-- 8
(5, 'ACTIVE', 'David Davis', 7524051790341075, 542, 'CAPITAL ONE', '2023-09-01'),		-- 9
(6, 'ACTIVE', 'Edward Edwards', 2347951978405789, 836, 'CHASE', '2022-10-01'),			-- 10
(6, 'ACTIVE', 'Edward Edwards', 2387450508917403, 134, 'CITIBANK', '2021-11-01'),		-- 11
(7, 'ACTIVE', 'Frank Farmer', 5724849598278945, 963, 'DISCOVER', '2020-12-01'),			-- 12
(7, 'ACTIVE', 'Frank Farmer', 7284592987452938, 235, 'MASTERCARD', '2019-01-01'),		-- 13
(7, 'ACTIVE', 'Alice Adams', 6724858923984728, 856, 'VISA', '2018-02-01');				-- 14


-- -----------------------------------------------------
-- Table project.Public
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS project.Public (
  public_id INT NOT NULL,
  customer_id INT NOT NULL,
  credit_card_id INT NOT NULL,
  public_booked_rate DECIMAL(9,2) NOT NULL,
  PRIMARY KEY (public_id),
  INDEX customer_fk_idx (customer_id ASC),
  INDEX credit_card_fk_idx (credit_card_id ASC),
  CONSTRAINT public_one_way_fk
    FOREIGN KEY (public_id)
    REFERENCES project.One_Way (one_way_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT public_customer_fk
    FOREIGN KEY (customer_id)
    REFERENCES project.Customer (customer_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT public_credit_card_fk
    FOREIGN KEY (credit_card_id)
    REFERENCES project.Credit_Card (credit_card_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- ---------------------------------------------------------
-- 	Given an email address and a password, returns the 
--  corresponding person_id (must be correctly 
--  matched within the system).
-- ---------------------------------------------------------
drop procedure if exists get_person_id;

delimiter $$

create procedure get_person_id
(
	in email_param varchar(45),
    in password_param varchar(45)
)
begin

start transaction;

select person_id
	from person
	where person_email = email_param and person_password = password_param;

end$$

delimiter ;


-- ---------------------------------------------------------
-- Creates an account for a new user (inserts data 
-- into the needed tables). If email address is present, a 
-- new user is not created, ‘user already exists’ message thrown. 
-- ---------------------------------------------------------
drop procedure if exists create_account;

delimiter $$

create procedure create_account
(
	in first_name_param varchar(45),
    in last_name_param varchar(45),
    in email_param varchar(45),
    in password_param varchar(45),
	in phone_number_param varchar(45)
)
begin

declare person_id_var int;
declare duplicate_email boolean default false;
declare sql_exception boolean default false;

declare continue handler for 1062
	set duplicate_email = true;

declare continue handler for sqlexception
	set sql_exception = true;

start transaction;

insert into person (person_first_name, person_last_name, person_email, person_password)
	values (first_name_param, last_name_param, email_param, password_param);

select person_id into person_id_var
	from person
	where person_email = email_param;
    
insert into users (user_id, user_phone_number)
	values (person_id_var, phone_number_param);

insert into customer (customer_id, customer_status)
	values (person_id_var, 'STANDARD');

if duplicate_email then
	rollback;
    select 'a user with this email address is already registered with the system';
elseif sql_exception then
	rollback;
    select 'sql exception encountered';
else
	commit;
end if;

end$$

delimiter ;


-- ---------------------------------------------------------
--  Returns the personal info, given a person_id. If the
--  person_id belongs to an admin, all user data is returned,
--  else just the specific person’s data is returned. 
-- ---------------------------------------------------------


drop procedure if exists get_personal_info;

delimiter $$

create procedure get_personal_info
(
	in person_id_param int
)
begin

declare admin_count int default 0;

start transaction;

select count(*) into admin_count
	from admin
	where admin_id = person_id_param;

if admin_count > 0 then
	select person_first_name, person_last_name, person_email, person_password,
	 user_phone_number, owner_routing_number
		from person p join users u on p.person_id = u.user_id
			left join owners o on u.user_id = o.owner_id
			left join customer c on u.user_id = c.customer_id
		order by person_last_name, person_first_name, person_email;
else
select person_first_name, person_last_name, person_email, person_password,
 user_phone_number, owner_routing_number
	from person p join users u on p.person_id = u.user_id
		left join owners o on u.user_id = o.owner_id
		left join customer c on u.user_id = c.customer_id
	where p.person_id = person_id_param;
end if;

end$$

delimiter ;


-- ---------------------------------------------------------
-- Allows one to update the first name of a given user.
-- ---------------------------------------------------------

drop procedure if exists update_first_name;

delimiter $$

create procedure update_first_name
(
	in person_id_var int,
	in first_name_var varchar(45),
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare admin_count int;
declare not_found boolean default false;
declare sql_exception boolean default false;

declare continue handler for not found
	set not_found = true;

declare continue handler for sqlexception
	set sql_exception = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
	from admin
	where admin_id = person_id_var;

if admin_count = 0 then
	update person
		set person_first_name = first_name_var
		where person_id = person_id_var;
else
    set error_encountered = true;
    set error_message = 'admins cannot edit personal info';
end if;

if not_found then
	rollback;
    set error_encountered = true;
    set error_message = 'person not found';
elseif sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql error encountered';
else
	commit;
end if;

end$$

delimiter ;


-- ---------------------------------------------------------
-- Allows one to update the last name of a given user.
-- ---------------------------------------------------------

drop procedure if exists update_last_name;

delimiter $$

create procedure update_last_name
(
	in person_id_var int,
	in last_name_var varchar(45),	
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare admin_count int;
declare not_found boolean default false;
declare sql_exception boolean default false;

declare continue handler for not found
	set not_found = true;

declare continue handler for sqlexception
	set sql_exception = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
	from admin
	where admin_id = person_id_var;

if admin_count = 0 then
	update person
		set person_last_name = last_name_var
		where person_id = person_id_var;
else
    set error_encountered = true;
    set error_message = 'admins cannot edit personal info';
end if;

if not_found then
	rollback;
    set error_encountered = true;
    set error_message = 'person not found';
elseif sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql error encountered';
else
	commit;
end if;

end$$

delimiter ;

-- ---------------------------------------------------------
-- Allows one to update the email of a given user.
-- ---------------------------------------------------------

drop procedure if exists update_email;

delimiter $$

create procedure update_email
(
	in person_id_var int,
	in email_var varchar(45),
    out error_encountered boolean,
    out error_message varchar(45)

)
begin

declare admin_count int;
declare not_found boolean default false;
declare duplicate_found boolean default false;
declare sql_exception boolean default false;

declare continue handler for not found
	set not_found = true;

declare continue handler for 1062
	set duplicate_found = true;

declare continue handler for sqlexception
	set not_found = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
	from admin
	where admin_id = person_id_var;

if admin_count = 0 then
	update person
		set person_email = email_var
		where person_id = person_id_var;
else
    set error_encountered = true;
    set error_message = 'admins cannot edit personal info';
end if;

if not_found then
	rollback;
    set error_encountered = true;
    set error_message = 'person not found';
elseif duplicate_found then
	rollback;
    set error_encountered = true;
    set error_message = 'duplicate email found';
elseif sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql error encountered';
else
	commit;
end if;

end$$

delimiter ;

-- ---------------------------------------------------------
-- Allows one to update the password of a given user.
-- ---------------------------------------------------------

drop procedure if exists update_password;

delimiter $$

create procedure update_password
(
	in person_id_var int,
	in password_var varchar(45),
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare admin_count int;
declare not_found boolean default false;
declare sql_exception boolean default false;

declare continue handler for not found
	set not_found = true;

declare continue handler for sqlexception
	set not_found = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
	from admin
	where admin_id = person_id_var;

if admin_count = 0 then
	update person
		set person_password = password_var
		where person_id = person_id_var;
else
    set error_encountered = true;
    set error_message = 'admins cannot edit personal info';
end if;

if not_found then
	rollback;
    set error_encountered = true;
    set error_message = 'person not found';
elseif sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql error encountered';
else
	commit;
end if;

end$$

delimiter ;

-- ---------------------------------------------------------
-- Allows one to update the phone number of a given user.
-- ---------------------------------------------------------

drop procedure if exists update_phone_number;

delimiter $$

create procedure update_phone_number
(
	in person_id_var int,
	in phone_number_var varchar(45),
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare admin_count int;
declare not_found boolean default false;
declare sql_exception boolean default false;

declare continue handler for not found
	set not_found = true;

declare continue handler for sqlexception
	set not_found = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
	from admin
	where admin_id = person_id_var;

if admin_count = 0 then
	update users
		set user_phone_number = phone_number_var
		where user_id = person_id_var;
else
    set error_encountered = true;
    set error_message = 'admins cannot edit personal info';
end if;

if not_found then
	rollback;
    set error_encountered = true;
    set error_message = 'person not found';
elseif sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql error encountered';
else
	commit;
end if;

end$$

delimiter ;

-- ---------------------------------------------------------
-- Allows one to update the routing number of a given user.
-- ---------------------------------------------------------

drop procedure if exists update_routing_number;

delimiter $$

create procedure update_routing_number
(
	in person_id_var int,
	in routing_number_var varchar(45),
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare admin_count int;
declare owner_count int default 0;

declare not_found boolean default false;
declare sql_exception boolean default false;

declare continue handler for not found
	set not_found = true;

declare continue handler for sqlexception
	set not_found = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
	from admin
	where admin_id = person_id_var;

if admin_count = 0 then
	select count(*) into owner_count
		from owners
		where owner_id = person_id_var;

	if owner_count = 0 then
		insert into owners values (person_id_var, routing_number_var);
	else
		update owners
			set owner_routing_number = routing_number_var
			where owner_id = person_id_var;
	end if;
else
    set error_encountered = true;
    set error_message = 'admins cannot edit personal info';
end if;

if not_found then
	rollback;
    set error_encountered = true;
    set error_message = 'owner not found';
elseif sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql error encountered';
else
	commit;
end if;

end$$

delimiter ;

-- ---------------------------------------------------------
-- 	Returns all the makes in the database
-- ---------------------------------------------------------

DROP PROCEDURE IF EXISTS get_makes;

DELIMITER //

CREATE PROCEDURE get_makes()
BEGIN

start transaction;

SELECT make_name FROM make;

END//

DELIMITER ;

-- ---------------------------------------------------------
-- 	Returns all the makes in the database
-- ---------------------------------------------------------

DROP PROCEDURE IF EXISTS get_models;

DELIMITER //

CREATE PROCEDURE get_models
(
	IN makeOfModel varchar(45)
) 
BEGIN

start transaction;

SELECT model_name FROM model
	where make_name = makeOfModel;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- 	Returns all the distinct years (of vehicles) in the database
-- ---------------------------------------------------------

DROP PROCEDURE IF EXISTS get_years;

DELIMITER //

CREATE PROCEDURE get_years
(
	IN modelOfYear varchar(45)
) 
BEGIN

start transaction;

SELECT distinct year_id from model_year
	where model_name = modelOfYear;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- 	Adds a vehicle to the database; will only work if the owner who 
-- the vehicle belongs to, has a routing number in the system.
-- ---------------------------------------------------------


DROP PROCEDURE IF EXISTS add_vehicle;

DELIMITER //

CREATE PROCEDURE add_vehicle(
	in vehicleVin varchar(45),
	in vehicleColor varChar(45),
	in vehiclePlateNumber varChar(45),
	in modelName varChar(45),
	in yearID int,
	in ownerID int,
    out error_encountered boolean,
    out error_message varchar(45)
)
BEGIN

DECLARE vehicleID int;
declare fk_violation boolean default false;

declare continue handler for 1452
	set fk_violation = true;

START TRANSACTION;

set error_encountered = false;
set error_message = '';

INSERT INTO VEHICLE VALUES 
	(null, vehicleVin, vehicleColor, vehiclePlateNumber, modelName, yearID, ownerID);

select MAX(vehicle_id) into vehicleID from vehicle;

INSERT INTO REGISTRATION VALUES (null, vehicleID, null, 'PENDING', (SELECT NOW()));

if fk_violation then
	rollback;
    set error_encountered = true;
    set error_message = 'owner must have a routing # to add a vehicle';
else
	commit;
end if;

END//

DELIMITER ;

call add_vehicle('48376273', 'red', '480-WHK', 'A4', 2017, 2, @encountered, @message);			-- 1
call add_vehicle('69829293', 'blue', '236-QHR', 'MODEL S', 2015, 2, @encountered, @message);	-- 2
call add_vehicle('28476378', 'green', '843-XBS', 'FOCUS', 2018, 3, @encountered, @message);		-- 3
call add_vehicle('46727372', 'black', '235-ISH', 'EXPLORER', 2016, 4, @encountered, @message);	-- 4
call add_vehicle('27428387', 'whie', '845-WIH', 'YUKON', 2015, 7, @encountered, @message);		-- 5
call add_vehicle('23857372', 'silver', '234-HIS', 'A6', 2015, 8, @encountered, @message);		-- 6
call add_vehicle('45723747', 'brown', '483-XHK', 'MODEL X', 2016, 9, @encountered, @message);	-- 7
call add_vehicle('45792374', 'yellow', '723-KSH', 'EXPLORER', 2017, 9, @encountered, @message);	-- 8


-- ---------------------------------------------------------
-- Returns all the vehicles that belong to a given owner (or all of
-- the vehicles registered in the database if the admin calls this query). 
-- ---------------------------------------------------------

DROP PROCEDURE IF EXISTS get_vehicles;

DELIMITER //

CREATE PROCEDURE get_vehicles
(
	IN personID int
)
BEGIN 

declare admin_count int default 0;

start transaction;

select count(*) into admin_count
	from admin
	where admin_id = personID;

if admin_count > 0 then
	select owner_id, make_name, m.model_name, my.year_id, type_description, vehicle_color, vehicle_vin, vehicle_plate_number
		from vehicle v join model_year my on v.model_name = my.model_name and v.year_id = my.year_id
			join type_of t using(type_id)
			join model m on m.model_name = my.model_name
		order by owner_id, make_name, m.model_name, my.year_id;
else
	select owner_id, make_name, m.model_name, my.year_id, type_description, vehicle_color, vehicle_vin, vehicle_plate_number
		from vehicle v join model_year my on v.model_name = my.model_name and v.year_id = my.year_id
			join type_of t using(type_id)
			join model m on m.model_name = my.model_name
		where owner_id = personID
		order by owner_id, make_name, m.model_name, my.year_id;
end if;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- Returns all the registrations belonging to a specific owner 
-- (or if the user logged into the system is an admin, return all registrations).
-- ---------------------------------------------------------

DROP PROCEDURE IF EXISTS get_registrations;

DELIMITER //

CREATE PROCEDURE get_registrations
(
	personID int
)
BEGIN

DECLARE instancesOfAdmin int;

START TRANSACTION;

SELECT COUNT(*) into instancesOfAdmin FROM Admin where personID = admin_id;

if instancesOfAdmin > 0 then
	SELECT registration_id as 'REG ID',
			owner_id as 'OWNER ID', 
			vehicle.vehicle_id as 'VEHICLE ID',
			vehicle.vehicle_vin as 'VIN',
			vehicle.vehicle_color as 'COLOR',
			model.make_name as 'MAKE', 
			vehicle.model_name as 'MODEL',
			vehicle.year_id as 'YEAR',
			vehicle.vehicle_plate_number as 'LICENSE PLATE',
			registration_status as 'STATUS',
			registration_status_change_date as 'LAST REGISTRATION STATUS CHANGE DATE'
		FROM registration INNER JOIN vehicle on
			 registration.vehicle_id = vehicle.vehicle_id
			INNER JOIN model on vehicle.model_name = model.model_name
			ORDER BY registration_status ASC;
else
	SELECT registration_id as 'REG ID',
			owner_id as 'OWNER ID', 
			vehicle.vehicle_id as 'VEHICLE ID',
			vehicle.vehicle_vin as 'VIN',vehicle.vehicle_color as 'COLOR',
			model.make_name as 'MAKE', 
			vehicle.model_name as 'MODEL',
			vehicle.year_id as 'YEAR',
			vehicle.vehicle_plate_number as 'LICENSE PLATE',
			registration_status as 'STATUS',
			registration_status_change_date as 'LAST REGISTRATION STATUS CHANGE DATE'
		FROM registration inner join vehicle on registration.vehicle_id = vehicle.vehicle_id
			inner join model on vehicle.model_name = model.model_name
		where personID = owner_id
		ORDER BY registration_status desc;
end if;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- Allows an owner to change a registration status of a vehicle 
-- to  ‘INACTIVE’ or ‘PENDING’, and an admin to change freely 
-- between  ‘ACTIVE’, ‘INACTIVE’, and ‘PENDING’.
-- ---------------------------------------------------------


DROP PROCEDURE IF EXISTS set_registration_status;

DELIMITER //

CREATE PROCEDURE set_registration_status
(
	personID int, 
    registrationID int, 
    statusChange varChar(25)
)
BEGIN

DECLARE instancesOfAdmin int;

START TRANSACTION;

SELECT COUNT(*) into instancesOfAdmin FROM Admin where personID = admin_id;

if instancesOfAdmin > 0 AND (statusChange = 'PENDING' OR statusChange = 'ACTIVE' OR
	 statusChange = 'INACTIVE') then
	UPDATE registration
		set registration_status = statusChange where registration_ID = registrationID;
	
    UPDATE registration
		set registration_status_change_date = (select now()) 
		where registration_ID = registrationID;
elseif instancesOfAdmin = 0 AND (statusChange = 'INACTIVE' OR statusChange = 'PENDING')
 then
	UPDATE registration
		set registration_status = statusChange 
		where registration_ID = registrationID;
	UPDATE registration
		set registration_status_change_date = (select now()) 
		where registration_ID = registrationID;
end if;

END//

DELIMITER ;

call set_registration_status(1, 1, 'ACTIVE');
call set_registration_status(1, 2, 'ACTIVE');
call set_registration_status(1, 3, 'ACTIVE');
call set_registration_status(1, 4, 'ACTIVE');
call set_registration_status(1, 5, 'ACTIVE');
call set_registration_status(1, 6, 'ACTIVE');
call set_registration_status(1, 7, 'ACTIVE');


-- ---------------------------------------------------------
-- Allows a user/customer to add a credit card to a system.
-- ---------------------------------------------------------


DROP PROCEDURE IF EXISTS add_credit_card;

Delimiter //

CREATE PROCEDURE add_credit_card
(
	in customerID int,
	in creditCardName varChar(45), 
    in creditCardNumber BIGINT , 
    in CVV int, 
    in ccType varChar(45),
	in expirationMonth int, 
    in expirationYear int,
    out error_encountered boolean,
    out error_message varchar(45)
)
BEGIN

declare admin_count int default 0;
declare sql_exception boolean default false;

declare continue handler for sqlexception
	set sql_exception = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count from admin where admin_id = customerID;

if admin_count > 0 then
	set error_encountered = true;
    set error_message = 'admins cannot add credit cards';
else
	INSERT INTO CREDIT_CARD 
		VALUES (null, customerID, 'ACTIVE', creditCardName, CreditCardNumber, CVV, ccType, 
		str_to_date(concat('01,', expirationMonth, ',', expirationYear),'%d,%m,%Y'));
end if;

if sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql exception encountered';
else
	commit;
end if;

END//

DELIMITER ;

-- ---------------------------------------------------------
-- Get all credit_cards associated with a given user (admin accesses all cards). 
-- ---------------------------------------------------------


DROP PROCEDURE IF EXISTS get_credit_cards;

DELIMITER //

CREATE PROCEDURE get_credit_cards
(
	personID int
)
BEGIN

DECLARE instancesOfAdmin int default 0;

START TRANSACTION;

SELECT COUNT(*) into instancesOfAdmin FROM Admin where personID = admin_id;

if instancesOfAdmin > 0 then
	select customer_id as 'Customer ID', credit_card_status as 'STATUS', credit_card_name
	 as 'NAME', credit_card_number as 'Number', 
    credit_card_type as 'Type', credit_card_expiration_date as 'Expiration Date'
		from credit_card
        order by customer_id, credit_card_number;
else    
	select customer_id as 'Customer ID', credit_card_status as 'STATUS', credit_card_name 
	as 'NAME', credit_card_number as 'Number', 
    credit_card_type as 'Type', credit_card_expiration_date as 'Expiration Date'
		from credit_card
        where customer_id = personID
        order by customer_id, credit_card_number;
end if;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- Get all trips associated with a given person; admin gets access to all trips.
-- ---------------------------------------------------------


drop procedure if exists get_trip_history;

delimiter $$

create procedure get_trip_history
(
	in person_id_param int,
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare admin_count int default 0;

declare sql_exception boolean default false;

declare continue handler for sqlexception
	set sql_exception = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
from admin
where admin_id = person_id_param;

if admin_count > 0 then
	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id,
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    p.credit_card_id, p.public_booked_rate, (o.one_way_distance * p.public_booked_rate) 
    as cost, 'CANCEL TRIP' as 'CANCEL TRIP'
		from trip t join one_way o on t.trip_id = o.one_way_id
            join public p on o.one_way_id = p.public_id
            join vehicle v on t.vehicle_id = v.vehicle_id

	union

	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id, 
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    -1, 0.00, 0.00 as cost, 'CANCEL TRIP' as 'CANCEL TRIP'
		from trip t join one_way o on t.trip_id = o.one_way_id
            join personal p on o.one_way_id = p.personal_id
            join vehicle v on t.vehicle_id = v.vehicle_id;
else
	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id,
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    p.credit_card_id, p.public_booked_rate, (o.one_way_distance * p.public_booked_rate) 
    as cost, 'CANCEL TRIP' as 'CANCEL TRIP'
		from trip t join one_way o on t.trip_id = o.one_way_id
            join public p on o.one_way_id = p.public_id
            join vehicle v on t.vehicle_id = v.vehicle_id
            where p.customer_id = person_id_param

	union

	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id, 
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    -1, 0.00, 0.00 as cost, 'CANCEL TRIP' as 'CANCEL TRIP'
		from trip t join one_way o on t.trip_id = o.one_way_id
            join personal p on o.one_way_id = p.personal_id
            join vehicle v on t.vehicle_id = v.vehicle_id
            where v.owner_id = person_id_param;
end if;

if sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql exception encountered';
else
	commit;
end if;

end$$

delimiter ;


-- ---------------------------------------------------------
-- Allows a customer to cancel a trip for which he/she has not 
-- yet been picked up for (i.e. start_time is in future).
-- ---------------------------------------------------------


drop procedure if exists cancel_trip;

delimiter $$

create procedure cancel_trip
(
	in trip_id_param int,
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare trip_start_time_var timestamp;
declare sql_exception boolean default false;

declare continue handler for sqlexception
	set sql_exception = true;

start transaction;

set error_encountered = false;
set error_message = '';

select trip_start_time into trip_start_time_var from trip where trip_id = trip_id_param;

if trip_start_time_var > now() then
	delete from personal where personal_id = trip_id_param;
	delete from public where public_id = trip_id_param;
	delete from one_way where one_way_id = trip_id_param;
	delete from trip where trip_id = trip_id_param;
else
	rollback;
	set error_encountered = true;
	set error_message = 'cannot cancel a started/completed trip';
end if;

if sql_exception then
	rollback;
	set error_encountered = true;
	set error_message = 'sql exception encountered';
else
	commit;
end if;

end$$

delimiter ;


-- ---------------------------------------------------------
-- Given potential trip details filled by a user, queries the
-- database for all vehicles which have an ‘ACTIVE’ registration_status
-- in order for the user to be able to vehicle to book for a trip. 
-- Returns details about the trip, car, cost, pick-up and drop-off time, etc.  
-- ---------------------------------------------------------

DROP PROCEDURE if exists get_available;

DELIMITER //

CREATE PROCEDURE get_available(
	startX dec(11,6),
	startY dec(11,6),
	endX dec(11,6),
	endY dec(11,6),
    personID int,
    out error_encountered boolean,
	out error_message varchar(45)
)
BEGIN

declare avgSpeed dec(9,2);
declare sql_exception boolean default false;

declare continue handler for sqlexception
	set sql_exception = true;

START TRANSACTION;

set error_encountered = false;
set error_message = '';

set avgSpeed = 5.0;

select vehicle.vehicle_id,
		vehicle.vehicle_color,
		vehicle.model_name, 
		pickup_time(vehicle.vehicle_id, startX, startY, avgSpeed) as 'Pick-up Time',
		timestamp(pickup_time(vehicle.vehicle_id, startX, startY, avgSpeed), 
		sec_to_time(distance_between_points(startX, startY, endX, endY)/avgSpeed * 3600)) 
			as 'Drop-off Time',
		costAlgorithm(startX, startY, endX, endY, baseRateCalc(personID, vehicle.owner_id)
		, type_of.type_base_rate, years.year_base_rate) as 'Cost', 
		avgSpeed as 'Average Speed', 
		distance_between_points(startX, startY, endX, endY) as 'Distance',
		(baseRateCalc(personID, vehicle.owner_id) * type_of.type_base_rate 
			* years.year_base_rate) as 'Rate', 'BOOK NOW' as 'BOOK THIS TRIP'
    from vehicle inner join registration on vehicle.vehicle_id = registration.vehicle_id
    INNER JOIN model_year on vehicle.model_name = model_year.model_name and
    	 vehicle.year_id = model_year.year_id
    INNER JOIN type_of on model_year.type_id = type_of.type_id
    INNER JOIN years on model_year.year_id = years.year_id
    where registration.registration_status = 'ACTIVE';

if sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql exception encountered';
else
	commit;
end if;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- Calculates the baseRate for a given user. As of now 
-- it’s a simple procedure which only differentiates itself
-- based on if the user is the owner of the vehicle (no cost) or not.
-- ---------------------------------------------------------


DROP FUNCTION IF EXISTS baseRateCalc;

DELIMITER //

CREATE FUNCTION baseRateCalc
(
	personID int, vehicleOwnerID int
)
returns dec(9,2)

BEGIN

DECLARE baseRate int;

if personID = vehicleOwnerID then
	set baseRate = 0.00;
else 
	set baseRate = 1.15;
end if;

return baseRate;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- Calculates the cost of trip, using modifiers (year of the car,
-- and type of car (Sedan, SUV, Luxury)). 
-- ---------------------------------------------------------

DROP FUNCTION IF EXISTS costAlgorithm;

Delimiter //

CREATE FUNCTION costAlgorithm
(
	startX dec(11,6),
	startY dec(11,6), 
    endX dec(11,6), 
    endY dec(11,6), 
    baseRate dec(9,2),
	typeOfCarModifier dec(9,2), 
    yearOfCarModifier dec(9,2)
)
returns dec(9,2)

BEGIN

DECLARE cost dec(9,2);
DECLARE distance dec(9,2);

set distance = distance_between_points(startX, startY, endX, endY);
set cost = distance * baseRate * yearOfCarModifier * typeOfCarModifier;

return cost;

END//

DELIMITER ;

-- ---------------------------------------------------------
-- Calculates the time of pick up for a potential trip. 
-- This calculates regardless if the vehicle is in a trip or 
-- not (i.e. this is to say, for a vehicle in the midst of the trip,
-- it will include finishing it’s current trip as part of it’s 
-- estimated pickup time).  
-- ---------------------------------------------------------

DROP FUNCTION IF EXISTS pickup_time;

Delimiter //

CREATE FUNCTION pickup_time
(
	vehicleID int,
	pickupLong dec(11,6), 
    pickupLat dec(11,6),
	avgSpeed dec(9,2)
)
RETURNS timestamp

BEGIN

declare lastTripID int;
DECLARE lastTripStartLong dec(11,6);
DECLARE lastTripStartLat dec(11,6);
DECLARE lastTripStartTime timestamp;
DECLARE lastTripEndLong dec(11,6);
DECLARE lastTripEndLat dec(11,6);
DECLARE lastTripEndTime timestamp;
DECLARE durationInSeconds dec(12, 6);
DECLARE durationInTime time;
DECLARE distanceDividedBySpeed dec (9,2);
DECLARE pickupTime timestamp;
declare trip_count int default 0;

select count(*) into trip_count from trip where vehicle_id = vehicleID;

if trip_count = 0 then
	return now();
end if;

set lastTripID = (select trip_id 
					from trip
					where vehicleID = trip.vehicle_ID 
					order by trip_start_time desc limit 1);

set lastTripStartLong = (select trip_start_x 
							from trip
							where trip_id = lastTripID);

set lastTripStartLat = (select trip_start_y 
							from trip
							where trip_id = lastTripID);
            
set lastTripStartTime = (select trip_start_time 
							from trip
							where trip_id = lastTripID);

set lastTripEndLong = (select one_way_destination_x 
							from trip inner join one_way on trip.trip_id = one_way.one_way_id
							where one_way.one_way_id = lastTripID);

set lastTripEndLat = (select one_way_destination_y 
							from trip inner join one_way on trip.trip_id = one_way.one_way_id
							where one_way.one_way_id = lastTripID);

set distanceDividedBySpeed = distance_between_points(lastTripStartLong, lastTripStartLat, lastTripEndLong, lastTripEndLat)/avgSpeed;
set durationInSeconds = distanceDividedBySpeed * 3600;
set durationInTime = SEC_TO_TIME(durationInSeconds);
set lastTripEndTime = TIMESTAMP(lastTripStartTime, durationInTime);

if lastTripEndTime > now() then
	set pickupTime = get_trip_end_time(lastTripEndTime, pickupLong, pickupLat,
	 lastTripEndLong, lastTripEndLat, avgSpeed);
else
	set pickupTime = get_trip_end_time(now(), pickupLong, pickupLat, lastTripEndLong,
	 lastTripEndLat, avgSpeed);
end if;

return pickupTime;

END//

DELIMITER ;

-- ---------------------------------------------------------
-- Calculates how many hours, minutes. and seconds it takes 
-- to finish a trip, and adds this to the start time of the
-- trip, ultimately estimating the arrival time of any trip. 
-- ---------------------------------------------------------

DROP FUNCTION IF EXISTS get_trip_end_time;

Delimiter //

CREATE FUNCTION get_trip_end_time
(
	startTime timestamp,
	passengerLong dec(11,6), 
    passengerLat dec(11,6),
	vehicleLong dec(11,6), 
    vehicleLat dec(11,6), 
    avgSpeed dec(9,2)
)
RETURNS timestamp

BEGIN

DECLARE distanceA dec(11,6);
DECLARE durationInSeconds dec(12, 6);
DECLARE durationInTime time;
Declare tripEndTime timestamp;
declare duration dec(12,6);

set distanceA = distance_between_points(passengerLong, passengerLat,
 vehicleLong, vehicleLat);
set duration = distanceA / avgSpeed;
set durationInSeconds = duration * 3600;
set durationInTime = SEC_TO_TIME(durationInSeconds);
set tripEndTime = TIMESTAMP(startTime, durationInTime);
    
return tripEndTime;

END//

DELIMITER ;

-- ---------------------------------------------------------
-- Calculates the Euclidian distance between two points. 
-- This is a simple placeholder to calculate distance but 
-- would ideally be replaced by Google Maps data.  
-- ---------------------------------------------------------


DROP FUNCTION IF EXISTS distance_between_points;

Delimiter //

CREATE FUNCTION distance_between_points
(
	startLong dec(11,6), 
    startLat dec(11,6),
	endLong dec(11,6), 
    endLat dec(11,6)
)
RETURNS dec(11,6)

BEGIN

DECLARE diffX dec(11,6);
DECLARE diffY dec(11,6);
DECLARE euclidianDistance dec(11,6);

set diffX = (startLong - endLong)*(startLong - endLong);
set diffY = (startLat - endLat)*(startLat - endLat);
set euclidianDistance = SQRT(diffx + diffy);

return euclidianDistance;

END//

DELIMITER ;


-- ---------------------------------------------------------
-- Books a one way trip (public or personal depending on owner/customer).
-- -- ---------------------------------------------------------


DROP PROCEDURE IF EXISTS book_one_way;

DELIMITER //

CREATE PROCEDURE book_one_way
(
	startX dec(11,6),
    startY dec(11,6),
    startTime timestamp,
    vehicleID int,
    endX dec(11, 6),
    endY dec(11,6),
    distance dec(11,6),
    avgMPH dec(9,2),
    personID int,
    creditCardID int,
    bookedRate dec(9,2),
    out error_encountered boolean,
    out error_message varchar(45)
)
BEGIN

declare vehicle_owner int;

declare new_id int;
declare sql_exception boolean default false;

declare continue handler for sqlexception
	set sql_exception = true;

START TRANSACTION;

set error_encountered = false;
set error_message = '';

INSERT into trip VALUES (null, startX, startY, startTime, vehicleID);
select max(trip_id) into new_id from trip;
INSERT into one_way VALUES(new_id, endX, endY, distance, avgMPH);

set vehicle_owner = (select owner_id from vehicle where vehicle.vehicle_id = vehicleID);

if vehicle_owner = personID then
	INSERT into personal values(new_id);
else
	INSERT into public values(new_id, personID, creditCardID, bookedRate);
end if;

if sql_exception then
	rollback;
	set error_encountered = true;
	set error_message = 'sql exception encountered';
    select 'error!!!!';
else
	commit;
end if;
    
END//

DELIMITER ;

-- ---------------------------------------------------------
-- Does not allow for insertion of emails to the database that do 
-- not include an ‘@’ sign.
-- --------------------------------------------------------- 

DROP TRIGGER IF EXISTS email_insert_trigger;

DELIMITER //

CREATE TRIGGER email_insert_trigger
	BEFORE INSERT ON person
	FOR EACH ROW
BEGIN  

IF NEW.person_email NOT LIKE'%@%' then
	SIGNAL SQLSTATE 'HY000'
	set MESSAGE_TEXT = 'Email must have an @ sign in it';
end if;
		
END//

DELIMITER ;


-- -----------------------------------------------------------------
-- Gets the customer id, credit card type, and credit card number 
-- for a given customer (used to build drop down menu to select a 
-- credit card to be used as payment for a ride). 
-- -----------------------------------------------------------------

DROP PROCEDURE IF EXISTS get_ccs;

DELIMITER //

CREATE PROCEDURE get_ccs
(
	personID int
)
BEGIN

START TRANSACTION;

select credit_card_id, credit_card_type, credit_card_number
		from credit_card
        where customer_id = personID;

END//

DELIMITER ;


-- -----------------------------------------------------------------
-- Gets the history of the vehicle’s trips for a given owner (if 
-- it’s an admin, gets the history of all vehicles)
-- -----------------------------------------------------------------

drop procedure if exists get_vehicle_history;

delimiter $$

create procedure get_vehicle_history
(
	in person_id_param int,
    out error_encountered boolean,
    out error_message varchar(45)
)
begin

declare admin_count int default 0;

declare sql_exception boolean default false;

declare continue handler for sqlexception
	set sql_exception = true;

start transaction;

set error_encountered = false;
set error_message = '';

select count(*) into admin_count
	from admin
	where admin_id = person_id_param;

if admin_count > 0 then
	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id,
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    p.credit_card_id, p.public_booked_rate, (o.one_way_distance * p.public_booked_rate) as cost
		from trip t join one_way o on t.trip_id = o.one_way_id
            join public p on o.one_way_id = p.public_id
            join vehicle v on t.vehicle_id = v.vehicle_id

	union

	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id, 
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    -1, 0.00, 0.00 as cost
		from trip t join one_way o on t.trip_id = o.one_way_id
            join personal p on o.one_way_id = p.personal_id
            join vehicle v on t.vehicle_id = v.vehicle_id;
else
	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id,
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    p.credit_card_id, p.public_booked_rate, (o.one_way_distance * p.public_booked_rate) as cost
		from trip t join one_way o on t.trip_id = o.one_way_id
            join public p on o.one_way_id = p.public_id
            join vehicle v on t.vehicle_id = v.vehicle_id
		where v.owner_id = person_id_param

	union

	select t.trip_id, t.trip_start_x, t.trip_start_y, t.trip_start_time, t.vehicle_id, 
    o.one_way_destination_x, o.one_way_destination_y, o.one_way_distance, o.one_way_avg_mph,
    -1, 0.00, 0.00 as cost
		from trip t join one_way o on t.trip_id = o.one_way_id
            join personal p on o.one_way_id = p.personal_id
            join vehicle v on t.vehicle_id = v.vehicle_id
		where v.owner_id = person_id_param;
end if;

if sql_exception then
	rollback;
    set error_encountered = true;
    set error_message = 'sql exception encountered';
else
	commit;
end if;

end$$

delimiter ;

