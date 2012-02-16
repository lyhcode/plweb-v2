import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import javax.naming.InitialContext
import java.text.SimpleDateFormat
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

class_id	= request.getParameter("class_id")
delX		= request.getParameterValues("del[]")
titleX		= request.getParameterValues("title[]")
course_idX	= request.getParameterValues("course_id[]")
lesson_idX	= request.getParameterValues('lesson_id[]');
orderX		= request.getParameterValues("order[]")
duedateX	= request.getParameterValues("duedate[]")
duetimeX	= request.getParameterValues("duetime[]")
begindateX	= request.getParameterValues("begindate[]")
begintimeX	= request.getParameterValues("begintime[]")

query1 = """
update CLASS_COURSE
set TITLE=?,
	SEQNUM=?,
	DUEDATE=?,
	BEGINDATE=?
where CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
"""

query2 = """
delete from CLASS_COURSE
where CLASS_ID=?
and COURSE_ID=?
and LESSON_ID=?
"""

// Date Time Format
sdf = new SimpleDateFormat('yyyy/MM/dd HH:mm:ss')

delL = []
delX.each {
	delL.add(it.toInteger())
}

for (i = 0; i < lesson_idX.length; i++) {
	course_id	= course_idX[i]
	lesson_id	= lesson_idX[i]
	duedate		= duedateX[i]
	duetime		= duetimeX[i]
	begindate	= begindateX[i]
	begintime	= begintimeX[i]
	order		= orderX[i]
	title		= titleX[i]
	
	if (duedate.equals('')) {
		duedate = null
	}
	else {
		if (duetime.equals('')) {
			duedate += ' 23:59:59'
		}
		else {
			duedate += ' '
			duedate += duetime
		}
		duedate = sdf.parse(duedate).time
	}
	
	if (begindate.equals('')) {
		begindate = null
	}
	else {
		if (begintime.equals('')) {
			begindate += ' 00:00:00'
		}
		else {
			begindate += ' '
			begindate += begintime
		}
		begindate = sdf.parse(begindate).time
	}
	
	if (delL.contains(i)) {
		sql.execute(query2, [class_id, course_id, lesson_id])
	}
	else {
		sql.executeUpdate(query1, [title, order, duedate, begindate, class_id, course_id, lesson_id])
	}
}

sql.close()

response.sendRedirect("schedule.groovy?id=${class_id}")
