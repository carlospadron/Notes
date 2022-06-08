import java.sql.DriverManager
import java.sql.SQLException
import org.locationtech.jts.io.WKTReader
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.index.strtree.GeometryItemDistance
import org.locationtech.jts.index.strtree.STRtree


class DbManager(
    user: String,
    pass: String,
    host: String,
    db: String
) {
    private val connectionUrl = "jdbc:postgresql://$host/$db?user=$user&password=$pass"

    fun getTable(sql: String): Map<String, Geometry> {
        val geom = mutableMapOf<String, Geometry>()
        val reader = WKTReader()

        try {
            DriverManager.getConnection(this.connectionUrl).use {
                connection -> connection.createStatement().use {
                    statement -> statement.executeQuery(sql).use { resultSet ->
                        while (resultSet.next()) {
                            geom[resultSet.getString("id")] = reader.read(resultSet.getString("geom"))
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return geom.toMap()
    }
}
fun nearestNeighbour(geoma: Map<String, Geometry>, geomb: Map<String, Geometry>): Map<String, Pair<String?, Double>> {
    //for each geometry a get entry of b with the lowest distance, then compute dist to save map
    val dist: Map<String, Pair<String?, Double>> = geoma.map {
            a -> a to geomb.minByOrNull{
                b -> a.value.distance(b.value)}}.toMap().map{ //so far is of the form (id_a, geom_a) to (id_b, geom_b)
                    c -> c.key.key to (c.value?.key to c.key.value.distance(c.value?.value))  //moving to form id_a to pair(id_b, dist)
    }.toMap().toSortedMap()
    return dist
}

fun nearestNeighbour2(geoma: Map<String, Geometry>, geomb: Map<String, Geometry>): Map<String, Pair<String?, Double>> {
    val t = STRtree()
    geomb.forEach {
        t.insert(it.value.envelopeInternal, it.value)
    }
    t.build()
    val dist = geoma.map {
            a -> a to t.nearestNeighbour(a.value.envelopeInternal, a.value, GeometryItemDistance()) //so far is of the form (id_a, geom_a) to geom_b
        }.toMap().map { a -> a.key to geomb.filterValues { b -> b == a.value} //so far is of the form (id_a, geom_a) to (id_b, geom_b)
        }.toMap().map { a -> a.key.key to (a.value.keys.first() to a.key.value.distance(a.value.values.first()))
        }.toMap().toSortedMap()

    return dist
}

fun main() {
    val user = readln()
    val pass = readln()

    val db = DbManager(user, pass, "localhost", "gis")
    val sql1 = """SELECT "UPRN" id, ST_AsText(geom) geom FROM os.open_uprn_white_horse"""
    val sql2 = """SELECT "postcode" id, ST_AsText(geom) geom FROM os.code_point_open_white_horse"""

    val uprn = db.getTable(sql1)
    val codepoint = db.getTable(sql2)

    val startTime = System.currentTimeMillis()
    val out1 = nearestNeighbour(uprn, codepoint) //22sec
    val endTime = System.currentTimeMillis()

    println(startTime - endTime)
    val startTime2 = System.currentTimeMillis()
    val out2 = nearestNeighbour2(uprn, codepoint) //3.6sec
    val endTime2 = System.currentTimeMillis()

    println(startTime2 - endTime2)
}