println 'hello'

def db = [url:'jdbc:hsqldb:mem:testDB', user:'sta', password:'stastasta', driver:'com.mysql.jdbc.Driver']
def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)

sql.eachRow('select * from ST_USER limit 10') {
    println it.
}
 
