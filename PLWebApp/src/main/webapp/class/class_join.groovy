import groovy.xml.MarkupBuilder
import groovy.sql.Sql
import javax.naming.InitialContext
import java.text.SimpleDateFormat
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

def sql = new Sql(helper.connection)

if (!session) {
	response.sendError 403
	return
}


def utype = session.get('utype')

html.setDoubleQuotes(true)
html.html {
	head {
		meta ('http-equiv': 'Content-Type', content: 'text/html; charset=utf-8')
		title('學生選修課程 - PLWeb')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/reset.css', media: 'all')
		link (rel: 'stylesheet', type: 'text/css', href: '../css/default.css', media: 'all')
		script (type: 'text/javascript', src: 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js', '')
	}
	body (class: 'page') {
		h1 ('選修課程')

		a (href: 'index.groovy', '返回課程管理')
		span (' | ')
		a (href: 'javascript:location.reload()', '重新整理')
		
		hr()

		form (action: 'class_join_save.groovy', method: 'post') {
			table (width: 500) {
				tr {
					th (colspan: 2, '輸入課程代碼')
				}
				tr {
					th (class: 'verticle', '課程代碼')
					td {
						input (name: 'class_id', size: 25, style: 'padding:2px;font-size:20px;font-family:Georgia')
						div (style: 'padding:5px;font-size:13px', '請向任課教師或註教取得課程代碼！')
					}
				}
				tr {
					td (colspan: 2, align: 'center') {
						input (type: 'submit', value: '確認送出')
						button ('取消', onclick: "location.href='index.groovy';return false")
					}
				}
			}
		}
	}
}
