import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendRedirect('/permission_denied.groovy')
	return;
}

uid = session.get('uid')

// get/post data
class_id       = helper.fetch('class_id')

_SQL_LINK_USER_CLASS_ = """
insert into USER_CLASS (USER_ID, CLASS_ID, IS_TEACHER)
values (?, ?, ?)
"""

_SQL_CHECK_CLASS_ = """
select count(1) as cc from ST_CLASS where CLASS_ID=?
"""
sql = new Sql(helper.connection)

done = false

try {
	if (class_id != null) {
		cc = sql.firstRow(_SQL_CHECK_CLASS_, [class_id]).cc
		
		if (cc > 0) {		
			sql.execute(_SQL_LINK_USER_CLASS_, [uid, class_id, 'n'])
			done = true
			session.setAttribute('alert_message', "您已經加選 ${class_id} 完成，返回我的課程頁面請記得重新整理。");
		}
		else {
			session.setAttribute('error_message', '課程代碼不存在！')
		}
	}
	else {
		session.setAttribute('error_message', '未輸入課程代碼！')
	}
}
catch (e) {
	session.setAttribute('error_message', e.message)
}

response.sendRedirect('index.groovy')

sql.close()
