import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

if (!session) {
	response.sendError 403
	return
}

def id = helper.fetch('id')

query1 = """
select * from ST_CLASS where CLASS_ID=?
"""

sql.eachRow(query1, [id]) {
	name		= it.CLASS_NAME
	school		= it.SCHOOL
	department	= it.DEPARTMENT
	password	= it.PASSWORD
}

html.doubleQuotes = true
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title('修改課程設定 - PLWeb')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/reset.css', media: 'all')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/default.css', media: 'all')
		script (type: 'text/javascript', src: 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js', '')
	}
	body (class: 'page') {
		h1 ('修改課程設定')

		p ("課程代號：${id}")

		a (href: response.encodeUrl('index.groovy'), '返回課程管理')

		span(' | ', style: 'color: gray')

		a (href: 'javascript:location.reload()', '重新整理')
			
		hr ()
		
		form(action: 'class_edit_save.groovy', method: 'post') {
			input (type: 'hidden', name: 'id', value: id)
			
			table (width: '480') {
				tr {
					th (colspan: 2, '課程基本資料')
				}
				tr {
					td (width: 100, '代號：')
					td ("${id} （唯讀）")
				}
				tr {
					td ('課程名稱：')
					td {
						input (name: 'name', value: name, style: 'width:100%')
					}
				}
				tr {
					td ('授課學校:')
					td {
						input (name: 'school', value: school)
					}
				}
				tr {
					td ('授課系所：')
					td {
						input (name: 'department', value: department)
					}
				}
				tr {
					td ('密碼：')
					td {
						input (type: 'password', name: 'password', value: password, autocomplete: 'off')
						span ('（測驗專用）')
					}
				}
				tr {
					td (colspan: 2, align: 'center') {
						input (type: 'submit', value: '儲存')
						button ('取消', onclick: "location.href='index.groovy';return false")
					}
				}
			}
		}
	}
}
