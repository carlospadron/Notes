This folder contains different approaches to the KNN problem using the
same data and machine.
The exercise is that for 82461 uprns, find the closest 0f 4121 geometries

Results:

test						          time					          multiple equally distant results	able to sort equally distant
Sklearn nearest neighbour	0 days 00:00:00.142799	False								              False
Geopandas sjoin_nearest		0 days 00:00:01.074799	True								              True
Pygeos strtree				    0 days 00:00:02.376158	False								              False
JTS, STRTree				      0 days 00:00:03.6       False								              False
Rust (opti), all vs all		0 days 00:00:05			    False								              True
Shapely strtree				    0 days 00:00:07.610000	False								              False
JST, all vs all				    0 days 00:00:22			    False								              True
SQL lateral 16GB			    0 days 00:00:51			    False								              True
SQL lateral 2GB				    0 days 00:00:51			    False								              True
Pygeos all vs all			    0 days 00:01:25.121942	False								              True
SQL distinct 16GB			    0 days 00:02:57.778057	False								              True
SQL distinct 2GB			    0 days 00:03:08			    False								              True
Shapely all vs all			  0 days 00:06:42.439090	False								              True
pyspark sql					      0 days 00:09:29			    False								              True

