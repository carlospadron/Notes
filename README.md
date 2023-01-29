# Notes
Compilation of notes for different technologies

When to use what for data analysis:
- Python: 
	- the data fits the memory
	- there is a library that does what you need in a efficient manner
	- you manage pandas and geopandas very well
- SQL
	- the data does not fit the memory
	- you plan to use many other tables already present in the database
	- you plan to write very complex queries
- Scala/Kotlin
	- the data fits the memory
	- you didn't find a library in Python that does what you need in a efficient way
	- you need to write your own algorithm/solutions in an environment that is fast and capable by nature
- Rust 
	- you need to write your own algorithm/solutions that runs very fast and that you can port to Python as a binding
- Pyspark
	- your data is massive and needs to be spread on different clusters
