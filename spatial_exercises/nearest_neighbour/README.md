This folder contains different approaches to the KNN problem using the
same data and machine.
The exercise is that for 82464 uprns, find the closest 0f 4121 geometries

Results:

| test						         | time					          | multiple equally distant results | able to sort equally distant | comments                              |
| ------------------------ |------------------------|----------------------------------|------------------------------|---------------------------------------|
|Sklearn nearest neighbour |0 days 00:00:00.142799	|False								             |False                         | only works for points                 |
|Geopandas sjoin_nearest	 |0 days 00:00:01.074799	|True								               |True                          | single process                        |
|Pygeos strtree				     |0 days 00:00:02.376158	|False								             |False                         | single process                        |
|JTS, STRTree				       |0 days 00:00:03.6       |False								             |False                         | single process                        |
|Rust (opti), all vs all	 |0 days 00:00:05			    |False								             |True                          | single process                        |
|Shapely strtree				   |0 days 00:00:07.610000	|False								             |False                         | single process                        |
|JST, all vs all				   |0 days 00:00:22			    |False								             |True                          | single process                        |
|SQL lateral 16GB			     |0 days 00:00:51			    |False								             |True                          | flexible, might use multiple processes|
|SQL lateral 2GB				   |0 days 00:00:51			    |False								             |True                          | flexible, might use multiple processes|
|Pygeos all vs all			   |0 days 00:01:25.121942	|False								             |True                          | single process                        |
|SQL distinct 16GB			   |0 days 00:02:57.778057	|False								             |True                          | flexible, might use multiple processes|
|SQL distinct 2GB			     |0 days 00:03:08			    |False								             |True                          | flexible, might use multiple processes|
|Shapely all vs all			   |0 days 00:06:42.439090	|False								             |True                          | single process                        |
|pyspark sql					     |0 days 00:09:29			    |False								             |True                          | flexible, will use multiple processes, scalable |
