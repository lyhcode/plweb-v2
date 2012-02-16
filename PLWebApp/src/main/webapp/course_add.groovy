import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

id    = helper.fetch('id')
name  = helper.fetch('name')
title = helper.fetch('title')

//訊息處理
error_message = session.getAttribute('error_message')?session.getAttribute('error_message'):''
alert_message = session.getAttribute('alert_message')?session.getAttribute('alert_message'):''
session.setAttribute('error_message', null)	
session.setAttribute('alert_message', null)

html.html {
	head {
		title("PLWeb - Content Editing")
		link (rel:'stylesheet', type:'text/css', href:'default.css', media:'all')
		script(type:"text/javascript", src: 'course_add.js', '') {}
	}
	body {
		if (error_message) {
			div (class: 'error_message', error_message)
		}
		if (alert_message) {
			div (class: 'alert_message', alert_message)
		}
		
		h2("Content Editing")

		h3("Add Content Set")
		form(action:"course_add_save.groovy", method:"post") {
			input (type:"hidden", name:"id", value:id)
			input (type:"hidden", name:"oname", value:name)
			
			table {
				tr {
					th (colspan: 2, 'Basic Settings')
				}
				tr {
					td ('Content ID: ')
					td {
						input (name:"name", value:name)
						br ()
						span ('輸入書籍ISBN或自訂代碼')
						br ()
						span ('若使用程式語言名稱建議加上個人代號, 例如 User1_Java')
						br ()
						span ('(限大小寫英數字及底線, 儲存後無法修改)')
					}
				}
				tr {
					td ('Display Name: ')
					td {
						input (name:"title", value:title, size: 40)
						br ()
						span ('輸入書籍顯示名稱(中英文數字符號皆可)')
					}
				}
				tr {
					td (colspan: 2, align: 'right') {
						input (type:"submit", value:"Save")
						input (type:"button", value:"Cancel", onclick: 'cancel();')
					}
				}
			}	
		}
	}
}
