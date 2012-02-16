import groovy.sql.Sql
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response)

uid = session.getAttribute('uid')

name  = helper.fetch('name')
title = helper.fetch('title').

ds = new InitialContext().lookup('java:comp/env/jdbc/plweb')
sql = new Sql(ds.getConnection())

try {
	//檢查是否曾經建立
	cc = sql.firstRow('select count(*) as cc from COURSE where COURSE_NAME=?', [name]).cc
	
	if (cc > 0) {
		throw new Exception('name exists')
	}
	
	//插入資料
	newId = sql.firstRow('select max(COURSE_ID)+1 as newid from COURSE').newid

	if (!newId) {
		newId = 1
	}
	
	sql.execute("insert into COURSE (COURSE_ID, COURSE_NAME, COURSE_TITLE) values (?,?,?)", [newId, name, title])

	sql.execute('insert into USER_COURSE(COURSE_ID, USER_ID, IS_OWNER) values (?,?,?)', [newId, uid, 'y'])
	
	session.setAttribute('alert_message', 'Book added.')
	
	//回列表
	response.sendRedirect('course_center.groovy')
	
}
catch (e) {
	session.setAttribute('error_message', e.message)
	
	//發生錯誤, 繼續編輯
    def rd = request.getRequestDispatcher('course_add.groovy')
    rd.forward(request, response)
}

sql.close()
