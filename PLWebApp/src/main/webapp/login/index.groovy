import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat

helper = new CommonHelper(request, response, session)

email = helper.fetch('email')

query1 = """
select COUNT(*) as USER_COUNT
from ST_USER
where LAST_UPDATE >= ?
and IS_LOGIN='y'
"""

sql = new Sql(helper.connection)

ucount = 0
try {
	ucount = sql.firstRow(query1, [new Date().time-(3*60*60*1000)]).USER_COUNT
}
catch (e) {
}

sql.close()

helper.attr 'ucount', ucount

helper.forward 'index.gsp'
