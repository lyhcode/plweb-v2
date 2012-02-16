import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import javax.naming.InitialContext
import org.plweb.webapp.helper.CommonHelper

helper = new CommonHelper(request, response, session)

if (!session) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

uid	  = session.get('uid')
uname = session.get('uname')
utype = session.get('utype')

if (!uid) {
	response.sendRedirect('permission_denied.groovy')
	return;
}

course_id = request.getParameter('course_id')
class_id  = request.getParameter('class_id')
lesson_id = request.getParameter('lesson_id')
mode      = request.getParameter('mode')

// 80 port防止存取課號為5(測驗課號)
/*
if (class_id != null) {
	if (class_id[4..4].equals('5')) {
		response.sendRedirect('permission_denied.groovy')
		return;
	}
}
*/

if (['T'].contains(utype) && mode == 'teacher') {
	if (request.getParameter('user_id')) {
		uid = request.getParameter('user_id')
	}
}

deployPath = request.getRealPath('/plwebstart')
packPath = request.getRealPath('/suite')
plugPath = request.getRealPath('/suite/plugins')
dataPath = request.getRealPath('/data')

sql = new Sql(helper.connection)

server_host = request.getServerName()
server_port = request.getServerPort()

codebase = server_host=='localhost'?"http://${server_host}:${server_port}/plwebstart/":'http://cdn.plweb.org/plwebstart/'
version  = "suite-20080918001"

//產生WS_TICKET
session_id  = session.id
ticket_no   = new Date().time
ticket_date = new Date().time

try {
	
	data = [
		'SESSION_ID':	session_id,
		'TICKET_NO':	ticket_no,
		'TICKET_DATE':	ticket_date,
		'USER_ID':		uid,
		'COURSE_ID':	course_id,
		'CLASS_ID':		class_id,
		'LESSON_ID':	lesson_id,
		'LESSON_MODE':	mode,
		'REMOTE_AGENT':	helper.getHeader('User-Agent'),
		'REMOTE_IP':	helper.remoteAddr,
		'REMOTE_HOST':	helper.remoteHost,
		'SERVER_HOST':	helper.serverName,
		'SERVER_PORT':	helper.serverPort
	]

	// write WS_TICKET
	// insert
	helper.simpleSqlInsert 'WS_TICKET', data, sql
}
catch (e) {
	println e
}

course_name = sql.firstRow("""
	select COURSE_NAME
	from COURSE
	where COURSE_ID=?
""", [course_id]).course_name

sql.close()

lesson_xml  = "http://${server_host}:${server_port}/ServerLesson.groovy?s=${session_id}&t=${ticket_no}"
request_url = "http://${server_host}:${server_port}/ServerRequest.groovy?s=${session_id}&t=${ticket_no}"

props = [
 	"plweb.urlpackage"	    : "${codebase}core/jedit-package-4.5.zip",
 	"plweb.urlpackage_asc"	: new Date().time,
    "plweb.urllesson"	    : "${lesson_xml}",
 	"plweb.urlrequest"	    : "${request_url}",
 	"plweb.lessonpath"	    : "${uid}/${class_id}/${course_name}/${lesson_id}",
 	"plweb.lessonfile"	    : "lesson${lesson_id}.xml",
 	"plweb.lessonxml"	    : "${uid}/${class_id}/${course_name}/lesson${lesson_id}.xml",
 	"plweb.lessonid"	    : "${lesson_id}",
 	"plweb.lessonmode"	    : "${mode}",
 	"plweb.jeditpath"	    : "jEdit",
 	"plweb.pluginpath"	    : "jEdit/workspace/jars",
 	"plweb.adimage"			: "http://${server_host}:${server_port}/ad-image/plweb-ad.png",
 	"plweb.adurl"			: "http://${server_host}:${server_port}/",
 	
 	"plweb.var.user_id"		: uid,
 	"plweb.var.user_name"	: uname,
 	"plweb.var.course_id"	: course_id,
 	"plweb.var.lesson_id"	: lesson_id,
 	"plweb.var.class_id"	: class_id,

    "file.encoding"         : 'UTF-8'
]

props_windows = [
	"plweb.diskroot"	    : "C:/myplweb",
	"plweb.shell"			: "cmd /C",
	"plweb.explorer"		: "start explorer /root, \${root}"
]

props_linux = [
	"plweb.diskroot"	    : "myplweb",
	"plweb.shell"			: "bash -c",
	"plweb.explorer"		: "nautils \${root}"
]

props_mac = [
    "plweb.diskroot"	    : "myplweb",
	"plweb.shell"			: "bash -c",
	"plweb.explorer"		: "nautils \${root}",
    'sun.awt.disableMixing' : 'true'
]

c = 0
new File(deployPath, 'plugins').eachFileMatch(~/.*\.jar/) {
    file->
	props.put("plweb.plugins.${c}", "${codebase}plugins/${file.name}")
	props.put("plweb.plugins_asc.${c}", new Date().time)
	c++
}

if (request.getParameter('debug')==null) {
    response.setContentType('application/x-java-jnlp-file; charset=UTF-8')
    response.setHeader("Content-disposition", "inline; filename=webstart.jnlp")
}
else {
    response.setContentType('text/xml; charset=UTF-8')
}

//response.setContentType("text/plain; charset=UTF-8")

println '<?xml version="1.0" encoding="UTF-8"?>';

xml = new MarkupBuilder(response.getWriter())
xml.setDoubleQuotes(true);
xml.jnlp(spec:"1.6+", codebase: codebase) {
	information() {
		title("Programming Teaching Assistant")
		vendor("PLWeb")
		description("Programming Teaching Assistant")
	}
	security() {
		"all-permissions"()
	}
	resources() {
		j2se(version: '1.6+')
		
		//jar(href: 'suite-webstart.jar')
		//jar(href: 'suite-common.jar')
		//jar(href: 'suite-jedit.jar')
		
		/* Apache Ant Library */
		//jar(href: 'ant.jar')
		//jar(href: 'ant-launcher.jar')
		
		/* Apache Commons Library */
		//jar(href: 'commons-httpclient-3.1.jar')
		//jar(href: 'commons-io-1.4.jar')
		//jar(href: 'commons-lang-2.5.jar')
		//jar(href: 'commons-logging-1.1.1.jar')
		//jar(href: 'commons-logging-api-1.1.1.jar')
		//jar(href: 'commons-logging-adapters-1.1.1.jar')
		//jar(href: 'commons-codec-1.3.jar')

		//jar(href: 'DJNativeSwing.jar')
		//jar(href: 'jna.jar')
		//jar(href: 'jna_WindowUtils.jar')
		//jar(href: 'FCKeditor_2.6.zip')
		//jar(href: 'DJNativeSwingDemo.jar')
		
        new File(deployPath, 'libs').listFiles().each {
            file->
            if (file.name.endsWith('.jar')) {
                jar (href: "libs/${file.name}")
            }
        }

        jar (href: 'core/plwebstart-1.0.jar')

		props.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Windows', arch: 'amd64') {
		nativelib (href: 'swt/swt-3.7.1-win32-win32-x86_64.jar')
		props_windows.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Windows', arch: 'x86_64') {
		nativelib (href: 'swt/swt-3.7.1-win32-win32-x86_64.jar')
		props_windows.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Windows', arch: 'x86') {
		nativelib (href: 'swt/swt-3.7.1-win32-win32-x86.jar')
		props_windows.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Linux', arch: 'x86_64') {
		nativelib (href: 'swt/swt-3.7.1-gtk-linux-x86_64.jar')
		props_linux.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Linux', arch: 'amd64') {
		nativelib (href: 'swt/swt-3.7.1-gtk-linux-x86_64.jar')
		props_linux.each {
			property(name: it.key, value: it.value)
		}
	}
	resources(os: 'Linux', arch: 'x86') {
		nativelib (href: 'swt/swt-3.7.1-gtk-linux-x86.jar')
		props_linux.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Mac', arch: 'x86_64') {
        j2se (version: 1.6, 'java-vm-args': '-XstartOnFirstThread -Xdebug')
		nativelib (href: 'swt/swt-3.7.1-cocoa-macosx-x86_64.jar')
		props_mac.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Mac', arch: 'amd64') {
        j2se (version: 1.6, 'java-vm-args': '-XstartOnFirstThread')
		nativelib (href: 'swt/swt-3.7.1-cocoa-macosx-x86_64.jar')
		props_mac.each {
			property(name: it.key, value: it.value)
		}
	}
    resources(os: 'Mac', arch: 'x86') {
        j2se (version: 1.6, 'java-vm-args': '-XstartOnFirstThread')
		nativelib (href: 'swt/swt-3.7.1-cocoa-macosx.jar')
		props_mac.each {
			property(name: it.key, value: it.value)
		}
	}
    'application-desc' ('main-class': 'org.plweb.suite.webstart.RunEditor') {
		argument('-nosplash')
		argument('-Xnosplash')
        argument('-XstartOnFirstThread')
        argument('-Xdock:name=PLWebStart')
	}
}

