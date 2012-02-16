import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat
import net.tanesha.recaptcha.ReCaptcha
import net.tanesha.recaptcha.ReCaptchaFactory

helper = new CommonHelper(request, response, session)

//產生圖片驗證碼
public_key = "6LfVocMSAAAAANwnuKgRfnnMEv6Fx8yU-h_X53xl"
private_key = "6LfVocMSAAAAAHvhuNUHuPIZYRgEhygpG3pnpVOk"
c = ReCaptchaFactory.newReCaptcha(public_key, private_key, false)
helper.attr 'reCaptcha',  c.createRecaptchaHtml(null, null)

helper.include 'password.gsp'