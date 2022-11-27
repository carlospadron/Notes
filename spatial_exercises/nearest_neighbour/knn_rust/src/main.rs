use std::io;
use postgres::{Client, NoTls};
use wkt::TryFromWkt;
use geo::{Point, LineString};
use geo::EuclideanDistance;
use std::collections::HashMap;
use std::time::{Duration, Instant};

fn db_manager(
    user: &String, 
    pass: &String,
    host: &str,
    db: &str,
    sql: &str
) -> HashMap<String, Point> {

    let url = format!("host={} user={} password={} dbname={}", host, user, pass, db);
    let mut client = Client::connect(
        &url,
        NoTls).unwrap();
    
    let mut geom:HashMap<String, Point> = HashMap::new();

    for row in client.query(sql, &[]).unwrap() {
        geom.insert(
            row.get(0),
            Point::try_from_wkt_str(row.get(1)).unwrap()
        );
        
    }

    geom
}

fn nearest_neighbour(geoma: HashMap<String, Point>, geomb: HashMap<String, Point>) -> HashMap<i64, (String, f64)>
{
    let geoma: HashMap<i64, Point> = geoma.into_iter().map(
        |(a,b)| (a.parse().unwrap(), b)
    ).collect();

    let mut output = HashMap::new();
    for (a, b) in geoma.iter() {
        let mut min = f64::INFINITY;
        let mut i = String::new();

        for (c, d) in geomb.iter() {
            let dist = b.euclidean_distance(d);
            if dist < min {
                min = dist;
                i = c.clone();
            }
        }
        output.insert(a.clone(), (i, min));
    }
    output
    
}
fn nearest_neighbour2(geoma: HashMap<String, Point>, geomb: HashMap<String, Point>) //-> HashMap<i64, (String, f64)>
{
    let geoma: HashMap<i64, LineString<f64>> = geoma.into_iter().map(
        |(a,b)| (a.parse().unwrap(), vec![b.x_y(), b.x_y()].into())
    ).collect();

/*    let mut output = HashMap::new();
    for (a, b) in geoma.iter() {
        let mut min = f64::INFINITY;
        let mut i = String::new();

        for (c, d) in geomb.iter() {
            let dist = b.euclidean_distance(d);
            if dist < min {
                min = dist;
                i = c.clone();
            }
        }
        output.insert(a.clone(), (i, min));
    }
    output
*/
}
fn main() {
    let mut user = String::new();
    let mut password = String::new();

    io::stdin().read_line(&mut user).unwrap();
    io::stdin().read_line(&mut password).unwrap();

    let host = "localhost";
    let db = "gis";
    let sql = "SELECT \"UPRN\"::text, ST_AsText(geom) FROM os.open_uprn_white_horse";
    let uprn = db_manager(&user, &password, &host, &db, &sql);
    let sql = "SELECT \"postcode\", ST_AsText(geom) FROM os.code_point_open_white_horse ";
    let codepoint = db_manager(&user, &password, &host, &db, &sql);    

    let start = Instant::now();
    let output = nearest_neighbour(uprn.clone(), codepoint.clone());
    let duration = start.elapsed();
    //println!("{:?}", output);
    println!("Time elapsed is: {:?}", duration);
    let start = Instant::now();
    let output = nearest_neighbour2(uprn.clone(), codepoint.clone());
    let duration = start.elapsed();
    println!("Time elapsed is: {:?}", duration);
}
