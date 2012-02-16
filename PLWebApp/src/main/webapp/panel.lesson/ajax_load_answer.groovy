/**
 * 
 * @param course_id		:integer
 * @param lesson_id		:integer
 */
import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import org.plweb.suite.common.xml.XmlFactory
import org.apache.commons.lang.StringEscapeUtils


helper = new CommonHelper(request, response, session)

uid = helper.sess('uid')

if (!uid) {
	println "error"
	return
}

course_id = helper.fetch('course_id')
lesson_id = helper.fetch('lesson_id')

sql = new Sql(helper.connection)

text_xml = sql.firstRow("""
	select TEXT_XML
	from COURSE_FILE
	where COURSE_ID=?
	and LESSON_ID=?
""", [course_id, lesson_id]).TEXT_XML

sql.close()

is = new ByteArrayInputStream(text_xml.getBytes("UTF-8"))
project = XmlFactory.readProject(is)

files = []

project.tasks.each {
	task ->
	file_name = project.getTaskPropertyEx(task, 'file.main')
	file_main = project.getFile(file_name)
	
	files << [
		name: file_name,
		content: StringEscapeUtils.escapeHtml(new String(file_main.decodedContent, 'UTF-8'))
	]
}

helper.attr 'files', files
helper.attr 'helper', helper

helper.forward 'ajax_load_answer.gsp'

