import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

id			= helper.fetch('id')
name		= helper.fetch('name')
school		= helper.fetch('school')
department	= helper.fetch('department')
password	= helper.fetch('password')

query1 = """
update ST_CLASS
set CLASS_NAME=?,
SCHOOL=?,
DEPARTMENT=?,
PASSWORD=?
where CLASS_ID=?
"""

try {
	sql.executeUpdate(query1, [name, school, department, password, id])
}
catch (e) {
	session.setAttribute('error_message', e.message)
}

sql.close()

response.sendRedirect('index.groovy')

