import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import java.text.NumberFormat

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def uid	  = session.get('uid')
def uname = session.get('uname')
def utype = session.get('utype')

if (!uid) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

def course_id = request.getParameter('course_id')

def ds = new InitialContext().lookup("java:comp/env/jdbc/plweb")
def sql = new Sql(ds.connection)

row = sql.firstRow('select COURSE_NAME, COURSE_TITLE from COURSE where COURSE_ID=?', [course_id])
course_name  = row.course_name
course_title = row.course_title

row = sql.firstRow('select * from USER_COURSE where COURSE_ID=? and USER_ID=?', [course_id, uid])
is_owner = row.is_owner=='y'

//日期及數字格式設定
def sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
def nf = NumberFormat.getInstance()
nf.setMaximumFractionDigits( 2 )
nf.setMinimumFractionDigits( 2 )

query0 = """
select count(*) as cc
from COURSE_FILE
where COURSE_ID=?
and VISIBLED='y'
"""

query1 = """
select TITLE, UPDATED, COURSE_ID, LESSON_ID, TEXT_SIZE, TASKNUM
from COURSE_FILE
where COURSE_ID=?
and VISIBLED='y'
order by SEQNUM
"""

query_member = """
select USER_COURSE.*, ST_USER.NAME, ST_USER.EMAIL
from USER_COURSE
inner join ST_USER
on USER_COURSE.USER_ID=ST_USER.USER_ID
where USER_COURSE.COURSE_ID=?
order by USER_COURSE.USER_ID
"""

// 訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title('PLWeb - Teaching Materials')
		script (type: 'text/javascript', src: 'lesson_play.js', '')
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
		h3 ("${course_name} / ${course_title} (${course_id})")

		a(href:"course_center.groovy") {
			img (src:'icons/arrow_undo.png', border:0)
			span ('Back')
		}

		hr ()
		h3 ('Contents')

		table (width:'100%') {
			tr {
				th (width:30, '#')
				th (width:60, 'Sort')
				th ('Chapter Title')
				th (width:40, 'Num')
				th (width:70, 'Size')
				th (width:110, 'Last Modified')
				
				th (class: 'small', width:40, 'Edit')
				th (class: 'small', width:100, 'Action')
			}
			
			rows = sql.rows(query1, [course_id])
				
			c = 0
			t_size = 0
			t_task = 0
			rows.each {
				row->
				
				t_size += row.text_size
				t_task += row.tasknum
				
				href_export	= "lesson_export.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_play   = "javascript: lessonPlay('${row.course_id}', '${row.lesson_id}');"
				href_edit	= "webstart.groovy?mode=author&course_id=${row.course_id}&lesson_id=${row.lesson_id}&class=0"
				href_up		= "lesson_up.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_down	= "lesson_down.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_copy	= "lesson_copy.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"
				href_remove	= "lesson_remove.groovy?course_id=${row.course_id}&lesson_id=${row.lesson_id}"

				tr (class: c%2==0?'even':'odd') {
					td (align:'center', ++c)
					td (align:'center') {
						if (c > 1)
						a(class: 'icon', href: href_up) {
							img (src:'icons/arrow_up.png', border:0, align:'left')
						}
						if (c < rows.size())
						a(class: 'icon', href: href_down) {
							img (src:'icons/arrow_down.png', border:0, align:'right')
						}
					}
					td {
						span {
							img (src:'icons/book.png', border:0)
							span ("${row.title}")
							font(color:'darkgray', "(${row.lesson_id})")
						}
					}
					td (row.tasknum)
					td (class:'small', align:'right', nf.format(row.text_size/1024)+' kb')
					td (class:'small', align:'center', sdf.format(new Date(row.updated.toLong())))
					
					td (align:'center') {
						a(class:'icon', href:href_edit) {
							img (src:'icons/book_edit.png', border:0)
						}
					}
					td (align:'center') {
						a(class:'icon', title:'Play', href: href_play) {
							img (src: 'icons/book_go.png', border:0)
						}
						a(class:'icon', title:'Export', href: href_export) {
							img (src: 'icons/book_link.png', border:0)
						}
						a(class:'icon', title:'Copy', href:href_copy) {
							img (src:'icons/book_add.png', border:0)
						}
						a(class:'icon', title:'Remove', href:href_remove, onclick: "return confirm('Are you sure???');") {
							img (src:'icons/book_delete.png', border:0)
						}
					}

				}
			}
			tr {
				th (colspan:3, 'Total')
				td (t_task)
				td (class:'small', align: 'right', nf.format(t_size/1024)+' kb')
				td (colspan:3)
			}
			tr {
				td (colspan: 8, align: 'right') {
					input (type:'button', onclick:"location.href='course_update.groovy?course_id=${course_id}'", value:'Update')
				}
			}
		}
		hr()
		h3 ('Add New Chapter')
		form (action:'lesson_add.groovy', method:'post') {
			input (type:'hidden', name:'course_id', value:course_id)
			table {
				tr {
					th (colspan: 2, 'Select Template')
				}
				tr {
					td ('Template: ')
					td {
						select (name:'template', '') {
							sql.eachRow(query1, [0]) {
								row->
								option(value:row.lesson_id, row.title)
							}
						}
					}
				}
				tr {
					td (colspan: 2, align: 'right') {
						input (type:'submit', value:'Add')
					}
				}
			}
		}
		hr()
		h3 ('Course Preferences')
		row = sql.firstRow('select * from COURSE where COURSE_ID=?', [course_id])
		table (width: '100%') {
			tr {
				th ('Setting Name')
				th ('Value')
				th ('Options')
				th ('Description')
			}
			tr {
				td ('Shareable?')
				td (row.is_share)
				td {
					a (href: "course_setting.groovy?course_id=${course_id}&action=is_share&value=y", 'y')
					span (' | ')
					a (href: "course_setting.groovy?course_id=${course_id}&action=is_share&value=n", 'n')
				}
				td ('Share this material to other teachers. (y = Yes, n = No)')
			}
		}
		hr()
		h3 ('Members')
		table (width: '100%'){
			tr {
				th ('#')
				th (width: 100, 'User ID')
				th ('E-Mail')
				th ('Name')
				th (width: '100', 'Owner')
				th (class: 'small', width: '40', 'Remove')
			}
			c = 0
			sql.eachRow(query_member, [course_id]) {
				row->
				tr (class: c%2==0?'even':'odd') {
					td (align: 'center', ++c)
					td (row.user_id)
					td (row.email)
					td (row.name)
					td (align: 'center') {
						if (row.is_owner=='y') {
							strong ('YES')
							span('(')
							a (href: "course_member_owner.groovy?action=unset&course_id=$course_id&user_id=${row.user_id}", "NO")
							span(')')
						}
						else {
							strong ('NO')
							span('(')
							a (href: "course_member_owner.groovy?action=set&course_id=$course_id&user_id=${row.user_id}", "YES")
							span(')')
						}
					}
					td (align: 'center'){
						if (row.user_id != uid) {
							a (href: "course_member_remove.groovy?course_id=$course_id&user_id=${row.user_id}", onclick: "return confirm('Are you sure?');") {
								img (src: 'icons/user_delete.png', border: 0)
							}
						}
					}
				}
			}
		}
		div (align: 'right') {
			span ("* You can't remove yourself.")
			br()
			span ("* Owners have permission to add new member.")
		}
		if (is_owner) {
			form (action: 'course_member_add.groovy', method: 'post') {
				input (type: 'hidden', name: 'course_id', value: course_id)
				table {
					tr {
						th (colspan: 2, 'New Member')
					}
					tr {
						td ('User ID / E-Mail:')
						td {
							input (name: 'user_id')
						}
					}
					tr {
						td (colspan: 2, align: 'right') {
							input (type: 'submit', value: 'Add')
						}
					}
				}
			}
		}
		hr()
		h3 ('Upload/Import(*.xml)')
		form (action: 'lesson_upload.groovy', method: 'post', enctype: 'multipart/form-data') {
			input (type: 'hidden', name: 'course_id', value: course_id)
			table {
				tr {
					th (colspan: 2, 'Open File')
				}
				tr {
					td ('File:')
					td {
						input (type: 'file', name: 'picture', size: '30')
					}
				}
				tr {
					td (colspan: 2, align: 'right') {
						input (type: 'submit', value: 'Upload')
					}
				}
			}
		}
		hr()
		h3 ('Download/Export(*.zip)')
		table (width: 500) {
			tr {
				th (colspan: 2, 'Save File')
			}
			tr {
				td (width: 100, 'File Name:')
				td {
					a (href: "course_export.groovy?course_id=${course_id}") {
						big {
							strong("${course_name}.zip")
						}
					}
					br()
					br()
					div ("* Clcik and save the file to your selected disk location.")
					div ("* This archived file includes serval lesson*.xml files.")
				}
			}
		}
	}
}

sql.close()