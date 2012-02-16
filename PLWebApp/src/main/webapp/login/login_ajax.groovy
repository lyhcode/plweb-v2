import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper

def helper = new CommonHelper(request, response, session)

_SQL_01 = """
select NAME
from ST_USER
where EMAIL=?
and (PASSWORD=? or PASSWORD=md5(?))
"""
_SQL_02 = """
select NAME from ST_USER where EMAIL=?
"""

email = helper.fetch('email', null)
password = helper.fetch('password', null)

sql = new Sql(helper.connection)

is_email_ok = false
is_password_ok = false
msg_email = null
msg_password = null

try {
    if (email != null) {
        if (password != null) {
            row = sql.firstRow(_SQL_01, [email, password, password])
            
            if (row) {
                msg_password = '密碼正確'
                is_password_ok = true
            }
            else {
                msg_password = '密碼不正確'
            }
        }
        
        row = sql.firstRow(_SQL_02, [email])
        
        if (row) {
            msg_email = "符合 ${row.NAME}"
            is_email_ok = true
        }
        else {
            msg_email = '此電子郵件信箱不正確或尚未註冊'
        }
    }
} catch (e) { msg_all = e }

sql.close()

def json = new groovy.json.JsonBuilder()
json email_ok: is_email_ok, email: msg_email, password_ok: is_password_ok, password: msg_password

println json.toString()
