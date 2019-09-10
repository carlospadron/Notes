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
