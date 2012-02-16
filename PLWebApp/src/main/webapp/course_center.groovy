import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid = session.get('uid')

def dataPath = request.getRealPath('/data')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)
def rows = []

query = """
select COURSE.*, USER_COURSE.IS_OWNER,
(select count(*)
	from COURSE_FILE
	where COURSE_FILE.COURSE_ID=COURSE.COURSE_ID
	and COURSE_FILE.VISIBLED='y') as LESSON_NUM,
(select sum(TEXT_SIZE)
	from COURSE_FILE
	where COURSE_FILE.COURSE_ID=COURSE.COURSE_ID
	and COURSE_FILE.VISIBLED='y') as LESSON_SIZE
from COURSE
inner join USER_COURSE on USER_COURSE.COURSE_ID=COURSE.COURSE_ID
where USER_COURSE.USER_ID=?
and COURSE.VISIBLED='y'
order by COURSE.COURSE_ID
"""


// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title("PLWeb - Teaching Materials")
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h2("Teaching Materials")

		a(href:'course_add.groovy') {
			img (src:'icons/application_add.png', border:0)
			span ('Add New Book')
		}
		hr()

		h3("Current Books")
		table(width:"100%") {
			tr {
				th (width: 30, '#')
				th ('Book ID / ISBN')
				th ('Book Name')
				th ('Size')
				th (class: 'small', width: 40, 'List')
				th (class: 'small', width: 80, 'Action')
			}
			c = 0
			t_size = 0
			sql.eachRow(query, [uid]) {
				row ->
				href_select = "lesson_list.groovy?course_id=${row.course_id}"
				href_remove = "course_remove.groovy?id=${row.course_id}&course="+URLEncoder.encode("${row.course_name}")
				href_edit   = "course_edit.groovy?id=${row.course_id}"

				if (row.lesson_size) {
					t_size += row.lesson_size
				}
				
				tr (class: c%2==0?'even':'odd') {
					td (align:'center', ++c)
					td {
						img (src:'icons/application.png', border:0, class: 'icon')
						span (row.course_name)
						span ('(')
						a (href:href_select, title: 'List chapters') {
							span ("${row.lesson_num} chapters")
						}
						span (')')
					}
					td (row.course_title)
					td (align: 'right', ((int)(row.lesson_size==null?0:row.lesson_size/1024))+" kb")
					td (align:'center') {
						a (href:href_select, title: 'List chapters') {
							img (src:'icons/application_view_list.png', border:0)
						}
					}
					td (align:'center') {
						if (row.is_owner == 'y') {
							a(class:'icon', href: href_edit, title: 'Edit') {
								img (src:'icons/application_edit.png', border:0)
							}
						}
						if (row.is_owner == 'y') {
							a(class:'icon', href: href_remove, title: 'Remove', onclick: "return confirm('Are you sure?');") {
								img (src:'icons/application_delete.png', border:0)
							}
						}
					}
				}
			}
			tr {
				th (colspan: 3) {
					span('Total')
				}
				td (align: 'right') {
					span(((int)(t_size==null?0:t_size/1024))+" kb")
				}
				td (colspan: 2)
			}
		}
	}
}

sql.close()