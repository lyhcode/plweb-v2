import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def course_id = request.getParameter("id")

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

try {	
	sql.execute('update COURSE set VISIBLED=? where COURSE_ID=?', ['n', course_id])

	session.setAttribute('alert_message', 'course removed');
}
catch (e) {
	session.setAttribute('error_message', e.message);
}

sql.close()

response.sendRedirect('index.groovy')

