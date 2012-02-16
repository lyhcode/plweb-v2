import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat
import net.tanesha.recaptcha.ReCaptcha
import net.tanesha.recaptcha.ReCaptchaFactory

helper = new CommonHelper(request, response, session)

__SQL_001 = """
select count(USER_ID) as USER_COUNT
from ST_USER
"""

sql = new Sql(helper.connection)

//計算總數
totalUserSize = 0
try {
	row = sql.firstRow(__SQL_001)
	totalUserSize = row?new DecimalFormat('#,###').format(row.USER_COUNT):0
}
catch (e) {}

sql.close()

helper.attr 'totalUserSize', totalUserSize
helper.attr 'signup_errmsg', helper.sess('signup_errmsg')

//產生圖片驗證碼
public_key = "6LfVocMSAAAAANwnuKgRfnnMEv6Fx8yU-h_X53xl"
private_key = "6LfVocMSAAAAAHvhuNUHuPIZYRgEhygpG3pnpVOk"
c = ReCaptchaFactory.newReCaptcha(public_key, private_key, false)
helper.attr 'reCaptcha',  c.createRecaptchaHtml(null, null)

helper.include 'signup.gsp'