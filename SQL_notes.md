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

* notes 
  * for pg_hba.conf: provide the right permissions otherwise Qgis might complain
  * for postgresql.conf: adjust working memory if needed
  * for Qgis: when connecting to postgis, leave empty the field host if the connection is local
  
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

* formatting
  * use plugin pg_formatter from atom
    
* logging
  * psql --log-file   

* read only databases
  * ALTER DATABASE gis SET default_transaction_read_only=on;
  * ALTER DATABASE gis SET default_transaction_read_only=off;
  
* series
  * SELECT generate_series(min, max, step);

* terminate queries
  * list active queries
    * select * from pg_stat_activity where state = 'active';
    
  * cancel query
    * select pg_cancel_backend(id);
    
  * terminate query
    * select pg_terminate_backend(id);

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
# pgRouting
## create nodes in the network (produces a copy)
```sql
SELECT 
  pgr_nodeNetwork(
    'schema1.network', 
    0.001
  );
```

## create topology
```sql
SELECT 
  pgr_createTopology(
    'schema1.network_noded', 
    0.001
  );
```
## analyse the network
```sql
SELECT
  pgr_analyzegraph('schema1.network_noded', 0.000001);
```

## calculate route
```sql
SELECT 
  * 
FROM 
  pgr_dijkstra(
    'SELECT 
       id,
       source,
       cost
     FROM 
       schema1.network_noded',
     node1,
     node2,
     FALSE --directed);
```

## calculate route and return map
```sql
WITH route AS (
  SELECT 
    * 
  FROM 
    pgr_dijkstra(
      'SELECT 
         id,
         source,
         target,
         cost
       FROM 
         schema1.network_noded',
       node1,
       node2,
       FALSE --directed))
SELECT 
  A.the_geom
FROM
  schema1.network_noded AS A
WHERE
  A.id IN (SELECT route.edge FROM route);
```

## driving distance
```sql
WITH driving_distance AS (
  SELECT 
    * 
  FROM 
    pgr_drivingDistance(
      'SELECT 
         id,
         source, 
         target, 
         cost 
       FROM 
         schema1.network_noded AS A',
       (SELECT array_agg(points.id) FROM schema1.access_points WHERE type = 'train station'),
       node2,
       FALSE --directed ))  
SELECT 
  A.*
FROM
  schema1.network_noded AS A,
  driving_distance AS source,
  driving_distance AS target
WHERE
  A.source = source.node
  AND A.target  = target.node
```

## produces alpha shape from driving distance vertices
```sql
WITH driving_distance AS (
  SELECT 
    * 
  FROM 
    pgr_drivingDistance(
      'SELECT 
         id,
         source, 
         target, 
         cost 
       FROM 
         schema1.network_noded',
       node1,
       node2,
       FALSE --directed )),
vertices AS (
  SELECT 
    A.*,
    B.cost,
    B.agg_cost  
  FROM
    schema1.network_noded_vertices_pgr AS A,
    driving_distance AS B
  WHERE
    A.id = B.node),
shape AS (
  SELECT
    * 
  FROM 
    pgr_pointsAsPolygon(
      'SELECT 
         id::int4,
         ST_X(the_geom)::float8 AS x, 
         ST_Y(the_geom)::float8 AS y
       FROM 
         vertices'))
SELECT 
  *
FROM
  shape;
```

# Environment variables
* change encoding on shell
  * PGCLIENTENCODING=LATIN1 
