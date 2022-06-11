This folder contains different approaches to the KNN problem using the
same data and machine.
The exercise is that for 82461 uprns, find the closest 0f 4121 geometries

Results:

SKLearn: 1.37s (only works for points)
Pygeos, STRTree: 2.08 s
JTS, STRTree: 3.6s
JTS, smallest distance in an array: 22s
SQL lateral: 50.1s
Pygeos, smallest distance in an array: 1min 24s
SQL distinct: 4min 15s
Shapely, smallest distance in an array: 7min 5 s
Shapely STRtree: 19min 23s (perhaps I'm doing something wrong, it is too slow)


