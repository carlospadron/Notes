# Postgresql
## Server setting

* READ https://fedoraproject.org/wiki/PostgreSQL for Fedora
* READ https://wiki.debian.org/PostgreSql for Debian

* create cluster (not required for debian)
  * root# postgresql-setup --initdb --unit postgresql
  * root# systemctl enable postgresql
	
* run server (not required for debian)
  * root# systemctl start postgresql
  
* create user role
  * root# passwd postgres
  * postgres$ createuser carlos
  * postgres$ psql
    * ALTER USER carlos SUPERUSER;
    * ALTER USER carlos PASSWORD 'pass';
		
* create db
  * carlos$ createdb gis
  
* configure server (fedora)
  * root# nano /var/lib/pgsql/data/pg_hba.conf 
  * root# nano /var/lib/pgsql/data/postgresql.conf 
  * root# systemctl restart postgresql
	    
* configure server (debian, ubuntu)
  * root# nano /etc/postgresql/10/main/pg_hba.conf 
  * root# nano /etc/postgresql/10/main/postgresql.conf 
  * root# /etc/init.d/postgresql reload	  

* configure firewall
  * root# firewall-cmd --permanent --add-port=5432/tcp
  * root# firewall-cmd --add-port=5432/tcp
  * adjust config to permissive:
    * root# nano /etc/selinux/config ()
  * reboot

* Notes 
  * For pg_hba.conf: provide the right permissions otherwise Qgis might complain
  * For postgresql.conf: adjust working memory if needed
  * For Qgis: when connecting to postgis, leave empty the field host if the connection is local
  
## Pgadmin4
* installing and running Pgadmin4
  * create virtual environment
    * python3 -m venv --copies /home/carlos/Dropbox/virtualEnv/pgadmin
  * activate virtual environment
    * source Dropbox/virtualEnv/pgadmin/bin/activate
  * Download pgadmin4
    * wget https://ftp.postgresql.org/pub/pgadmin/pgadmin4/v3.6/pip/pgadmin4-3.6-py2.py3-none-any.whlD
  * install pgadmin4
    * pip3 install pgadmin4-3.6-py2.py3-none-any.whl
  * run pgadmin4
    * python3 Dropbox/virtualEnv/pgadmin/lib64/python3.6/site-packages/pgadmin4/pgAdmin4.py 
  

     
    Read only databases
		ALTER DATABASE cartografia SET default_transaction_read_only=on;
	    SET default_transaction_read_only=off;
	    ALTER DATABASE cartografia SET default_transaction_read_only=off;
			
    Backup
        one database
	    	pg_dump > base.txt
			psql base < base.txt
		        
	    all databases
		    pg_dumpall > base.txt 
		    psql -f base.txt postgres


    
    Sql
			  
   Add column
     ALTER TABLE planet_osm_line ADD COLUMN "gid" serial;

   Describe columns
	   \d table

    change encoding on shell
		   PGCLIENTENCODING=LATIN1 

    Copy
	   COPY (or \copy) table to 'test.csv' WIT CSV HEADER;
	   psql gis -c "COPY schema.table FROM 'test.csv' DELIMITER ',' CSV HEADER"

    Logfile
	   psql --log-file 

POSTGIS 
    setting
        psql -d gis -c "CREATE EXTENSION postgis;"
	    psql -d gis -c "CREATE EXTENSION postgis_topology;"
	    psql -d gis -c "CREATE EXTENSION postgis_sfcgal;"
	column
	    SELECT AddGeometryColumn ('ucl','road_data','geom',27700,'POINT',2);
	    UPDATE ucl.road_data SET geom = ST_SetSRID(ST_Point(lon, lat), 27700);
	Index
	    CREATE INDEX road_data_gix ON ucl.road_data USING GIST (geom);
    Openstreetmap
	    Loading openstreetmap to postgis database (read to gis database by default).
		    osm2pgsql -C 27000 Documents/british-isles-latest.osm.pbf 	    
	    
Data mangement and solutions
-unknown attributes: use xml or json columns
-complex joins: try to keep a simple data structure 
	-no dpulications
	-no unnecessary splits of tables
	
-data management: databases ara for data, leave management to qgis or any other platfform
