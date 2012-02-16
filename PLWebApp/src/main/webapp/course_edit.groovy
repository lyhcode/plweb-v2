import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext

def id    = request.getParameter('id')

def ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
def sql = new Sql(ds.connection)

row = sql.firstRow('select * from COURSE where COURSE_ID=?', [id])
id    = row.course_id
name  = row.course_name
title = row.course_title

sql.close()

//訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.setDoubleQuotes(true)
html.html {
	head {
		title("PLWeb - Content Editing")
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
		script(type:'text/javascript', src:'course_edit.js', '')
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h2("Content Editing")

		h3("Edit Content Set")
		form(action:"course_edit_save.groovy", method:"post") {
			input (type:"hidden", name:"id", value:id)
			
			table {
				tr {
					th (colspan: 2, 'Basic Settings')
				}
				tr {
					td ('Content ID: ')
					td {
							input(type:'hidden', name:'name', value:name)
							span(name)
					}
				}
				tr {
					td ('Display Name: ')
					td {
						input (name:"title", value:title, size: 40)
					}
				}
				tr {
					td (colspan: 2, align: 'right') {
						input (type:"submit", value:"Save")
						input (type:"button", value:"Cancel", onclick: "location.href='course_center.groovy';")
					}
				}
			}	
		}
	}
}
