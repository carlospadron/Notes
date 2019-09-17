# Postgresql
## Server setting

* READ https://fedoraproject.org/wiki/PostgreSQL for Fedora
* READ https://wiki.debian.org/PostgreSql for Debian

### create cluster (not required for debian) 

```
sudo postgresql-setup --initdb --unit postgresql
sudo systemctl enable postgresql
```
	
### run server (not required for debian)

`sudo systemctl start postgresql`
  
### user roles

create superuser with password from terminal

```
sudo passwd postgres
su postgres
createuser carlos -s -P
```

create superuser with password from psql

```
sudo passwd postgres
su postgres
psql
CREATE USER "carlos" WITH SUPERUSER PASSWORD 'xyz';
\q
```

Alter roles

```
psql
ALTER USER carlos SUPERUSER;
ALTER USER carlos PASSWORD 'pass';
\q
```

### databases
		
create db

`createdb gis`

### configuration files

configure server (fedora)

```
sudo nano /var/lib/pgsql/data/pg_hba.conf 
sudo nano /var/lib/pgsql/data/postgresql.conf 
sudo systemctl restart postgresql
```
	    
configure server (debian, ubuntu)

```
sudo nano /etc/postgresql/10/main/pg_hba.conf 
sudo nano /etc/postgresql/10/main/postgresql.conf 
sudo /etc/init.d/postgresql reload	
```

optional password file

`nano pgpass.conf`

### firewall config

add ports:

```
sudo firewall-cmd --permanent --add-port=5432/tcp
sudo firewall-cmd --add-port=5432/tcp
```

adjust config to permissive:

`sudo nano /etc/selinux/config`

## Qgis

when connecting to postgis, leave empty the field host if the connection is local
  
## Pgadmin4
* installing and running Pgadmin4
  * create virtual environment
    * python3 -m venv --copies /home/carlos/Dropbox/virtualEnv/pgadmin
  * activate virtual environment
    * source Dropbox/virtualEnv/pgadmin/bin/activate
  * download pgadmin4
    * wget https://ftp.postgresql.org/pub/pgadmin/pgadmin4/v3.6/pip/pgadmin4-3.6-py2.py3-none-any.whlD
  * install pgadmin4
    * pip3 install pgadmin4-3.6-py2.py3-none-any.whl
  * run pgadmin4
    * python3 Dropbox/virtualEnv/pgadmin/lib64/python3.6/site-packages/pgadmin4/pgAdmin4.py 
  
# POSTGIS 
  * setting
    * psql -d gis -c "CREATE EXTENSION postgis;"
    * psql -d gis -c "CREATE EXTENSION postgis_topology;"
    * psql -d gis -c "CREATE EXTENSION postgis_sfcgal;"
    
  * spatial column
    * create table
      * CREATE TABLE schema.table (id serial primary key, geom geometry('POLYGON', 27700))
    * create column
      * SELECT AddGeometryColumn ('ucl','road_data','geom',27700,'POINT',2);
      
    * update from lat and lon columns
      * UPDATE ucl.road_data SET geom = ST_SetSRID(ST_Point(lon, lat), 27700);
      
    * reading from hex code (hex is the typical postgis output)
      * UPDATE ucl.road_data SET geom = ST_GeomFROMEWKB(geom_text::geometry) 
      
    * create index
      * CREATE INDEX road_data_gix ON ucl.road_data USING GIST (geom);
      
    * add id column for qgis (if not present Qgis will not allow updates to the table)
      * ALTER TABLE table1 ADD PRIMARY KEY (id);

  * openstreetmap
    * loading openstreetmap to postgis database (writes to gis database by default).
      * osm2pgsql -C 27000 Documents/british-isles-latest.osm.pbf 
    
    * loading openstreetmap to pgrouting
      * osm2pgrouting --f "P:\somewhere\latest.osm_01.osm" --clean --dbname gis --username carlos --schema osm_routing --conf "C:\somewhere\mapconfig.xml"

     
# SQL/PSQL
* alter table
  * add column
    * ALTER TABLE planet_osm_line ADD COLUMN "gid" serial;
* backup
  * one database, plain text
    * pg_dump gis > newbackup.txt
    * psql gis < newbackup.txt
    
  * one database, custom format
    * pg_dump -Fc -h 192.168.0.1 -p 5432 -U carlos -d gis -n schema1 > newbackup.backup
    * pg_restore -h localhost -C -d gis newbackup.backup
    
  * all databases
    * pg_dumpall > database.txt 
    * psql -f database.txt postgres
    
* execute  
  * command
    psql -c "sql command"
  
  * read commands from file
    psql -f "file with commands"
    
* copy
  * standard
    * COPY table to 'test.csv' WIT CSV HEADER;
  * for non-superuser
    * \COPY table to 'test.csv' WIT CSV HEADER;
  * with psql
    * psql gis -c "COPY schema.table FROM 'test.csv' DELIMITER ',' CSV HEADER"  
    
* describe columns
  * \d table

* duplicates
  * find and count duplicates
    * SELECT id, count( * ) FROM table1 GROUP BY id HAVING count( * ) > 1
 
* foreign tables
  * extension
    * CREATE EXTENSION postgres_fdw;
    
  * create validator
    * CREATE FOREIGN DATA WRAPPER giswrapper HANDLER postgres_fdw_handler VALIDATOR postgres_fdw_validator;
    
  * create server
    * CREATE SERVER externalgis FOREIGN DATA WRAPPER giswrapper OPTIONS (host '192.168.0.1', dbname 'gis', port '5432')
    
  * create user mapping (user and password must be present on foreign server, user on first part can be PUBLIC or role)
    * CREATE USER MAPPING FOR "carlos" SERVER externalgis OPTIONS (user "carlos", password "pass"); 
    
  * create foreign table
    * CREATE FOREIGN TABLE  schema1.table1 (col1 serial NOT NULL,, col2 text) SERVER externalgis;
    
  * list foreign tables
    * SELECT * FROM information_schema.foreign_tables;
  * modify foreign table
    * ALTER FOREFG TABLE schema.table ADD COLUMN col;

* formatting
  * use plugin pg_formatter from atom
    
* logging
  * psql --log-file   

* primary key
  * add primary key when table is created
    * CREATE TABLE schema.table (id serial primary key);
  * create sequence and add column to existing table
    * CREATE SEQUENCE schema.serial1 START (SELECT max(A.col) FROM A);
    * ALTER TABLE schema.table ALTER COLUMN col SET DEFAULT nextval('schema.serial1');
    * ALTER TABLE table_name ADD PRIMARY KEY (col);

* permissions and constraints 
  * permissions
    * GRANT CONNECT ON DATABASE db TO role/user;
    * GRANT SELECT ON TABLE schema.table TO role/user;
    * REVOKE ALL ON TABLE schema.table FROM role/user;
    * GRANT USAGE ON SCHEMA schema TO role/user;
    * GRANT SELECT ON ALL TABLES IN SCHEMA schema TO role/user;

  * read only databases
    * ALTER DATABASE db SET default_transaction_read_only=on;
    * ALTER DATABASE db SET default_transaction_read_only=off;
  
* series
  * SELECT generate_series(min, max, step);

* terminate queries
  * list active queries
    * select * from pg_stat_activity where state = 'active';
    
  * cancel query
    * select pg_cancel_backend(id);
    
  * terminate query
    * select pg_terminate_backend(id);
* users
  * CREATE USER "user" WITH PASSWORD 'pass';
* values

  * SELECT * FROM (VALUES (a,b,c), (1,2,3)) AS A;

# PL/pgPSQL

## triggers
### trigger function
```sql
CREATE OR REPLACE FUNCTION myfunction() RETURNS trigger AS $$
BEGIN
  NEW.col1 = val1;
  NEW.col2 = (SELECT val2 FROM table 2);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```
### create trigger
```sql
CREATE TRIGGER myfunction
BEFORE INSERT OR UPDATE ON schema1.table1
FOR EACH ROW EXECUTE PROCEDURE myfunction(); 
```
