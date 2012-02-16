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

query1 = """
select CLASS_NAME
from ST_CLASS
where CLASS_ID=?
"""

query2 = """
select CLASS_COURSE.*,
(select COURSE_NAME from COURSE where COURSE_ID=CLASS_COURSE.COURSE_ID) as COURSE_NAME,
(select COURSE_TITLE from COURSE where COURSE_ID=CLASS_COURSE.COURSE_ID) as COURSE_TITLE
from CLASS_COURSE
where CLASS_ID=?
order by SEQNUM
"""

class_id = request.getParameter('id')
class_name = sql.firstRow(query1, [class_id]).class_name

rows = sql.rows(query2, [class_id])

// Date Format Setting
sdf1 = new SimpleDateFormat('yyyy/MM/dd')
sdf2 = new SimpleDateFormat('HH:mm:ss')

html.setDoubleQuotes(true)
html.html {
	head {
		title ('課程進度管理 - PLWeb')

		script (type: 'text/javascript', src: '../lesson_play.js', '')
		script (type: 'text/javascript', src: '../jquery/jquery-1.3.2.min.js', '') 
		script (type: 'text/javascript', src: '../jquery/jquery-ui-1.7.1.custom.min.js', '')
		
		script (type: 'text/javascript', src: 'schedule.js', '')
		link (rel:'stylesheet', type:'text/css', href:'../css/reset.css', media:'all')
		link (rel: 'stylesheet', type: 'text/css', href: 'default.css')
		link (rel: 'stylesheet', type: 'text/css', href: 'schedule.css')
		link (rel: 'stylesheet', type: 'text/css', href: '../jquery/css/smoothness/jquery-ui-1.7.1.custom.css')
	}
	body (class: 'page') {
		h1 ('課程進度管理')

		div {
			a (href: 'index.groovy', '返回課程管理')
			span (' | ')
			a (href: 'javascript:location.reload()', '重新整理')
		}

		hr ()
		p ("課程代碼：${class_id}")
		p ("課程名稱：${class_name}")

		p {
			a (href: "schedule_lessons.groovy?id=${class_id}") {
				img (src: '../icons/book_add.png', border: 0)
				span ('加入新的教材')
			}
		}
		
		p {
			span ('編輯模式：')
			input (type: 'button', value: '簡易', onclick: "\$('.detail').hide();\$('.simple').show();")
			input (type: 'button', value: '詳細', onclick: "\$('.detail').show();\$('.simple').hide();")
		}
		
		form (action: 'schedule_save.groovy', method: 'post') {
			input(type: 'hidden', name: 'class_id', value: class_id)

			table (width: '100%') {
				tr {
					th (width: 30, class: 'small', '#')
					th ('教材 / 單元名稱')
					th (class: 'small', width: 120, '練習開放日期(起)')
					th (class: 'small', width: 120, '練習開放日期(訖)')
					th (class: 'small', width: 40, '播放')
					th (class: 'small', width: 40, '報表')
					th (class: 'small', width: 40, '排序')
					th (class: 'small', width: 30, '刪除')
				}
				
				c = 0
				
				rows.each {
					row->

					tr (class: c%2==0?'odd':'even') {
						td (align: 'center', class: 'small', style: 'font-family: Georgia;', c+1)
						td {
							div (class: "detail") {
								span ("${row.course_title} (${row.course_name})")
								br ()
								input(name: 'title[]', value: row.title, style: 'width:100%')
							}
							div (class: 'simple') {
								span ("${row.title}")
							}
							
							
							input(type: 'hidden', name: 'course_id[]', value: row.course_id)
							input(type: 'hidden', name: 'lesson_id[]', value: row.lesson_id)
						}
						td {
							date_str = ''
							if (row.begindate) {
								date_str = sdf1.format(new Date(row.begindate.toLong()))
							}
							time_str = ''
							if (row.begindate) {
								time_str = sdf2.format(new Date(row.begindate.toLong()))
							}
							input (type: 'text', name: 'begindate[]', value: date_str, style: 'width: 100%; font-family: Georgia', class: 'datebox')
							div (class: "detail") {
								input(type: 'text', name: 'begintime[]', value: time_str, style: 'width: 100%; font-family: Georgia')
							}
						}
						td {
							date_str = ''
							if (row.duedate) {
								date_str = sdf1.format(new Date(row.duedate.toLong()))
							}
							time_str = ''
							if (row.duedate) {
								time_str = sdf2.format(new Date(row.duedate.toLong()))
							}
							input (type: 'text', name: 'duedate[]', value: date_str, style: 'width: 100%; font-family: Georgia', class: 'datebox')
							div (class: "detail") {
								input(type: 'text', name: 'duetime[]', value: time_str, style: 'width: 100%; font-family: Georgia')
							}
						}

						td (align: 'center') {
							a (href: "javascript: lessonPlay(${row.course_id}, ${row.lesson_id});", title: 'Play Lesson Content') {
								img (src: '../icons/application_go.png', border: 0)
							}
						}
						td (align: 'center') {
							a (href: "lamp.groovy?class_id=${class_id}&course_id=${row.course_id}&lesson_id=${row.lesson_id}") {
								img (src:'../icons/chart_bar.png', border:0)
							}
						}
						td {
							input(type: 'text', name: 'order[]', value:c, style: 'width:100%')
						}
						td (align:'center') {
							input(type: 'checkbox', name: 'del[]', value: c)
						}
					}
					c++
				}
				
				tr {
					td (colspan: 8, align: 'right') {
						if (rows) {
							input (type: 'submit', value: '確認送出')
						}
						else {
							span (style: 'color:red;font-weight:bold', '本課程尚未加入任何教材')
						}
						button ('取消', onclick: "location.href='index.groovy';return false")
					}
				}
			}
		}
	}
}