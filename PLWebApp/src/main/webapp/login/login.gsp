<%
	helper = request.get('helper')
%>
<form action="${response.encodeUrl('/login/login_auth.groovy')}" method="post" class="form-login"><div class="form-login-inner">
	<input type="hidden" name="module" value="login" />
	<input type="hidden" name="ajax_url" value="${response.encodeUrl('/login/ajax.groovy')}" />
	
	<div class="field">
		<span class="field-title">電子郵件</span>
  		<input type="email" name="email" class="input-text" style="width:80%" title="請輸入帳號（電子郵件）" value="${helper.sess_fetch('login_email', '')}" />
	</div>
	
	<div class="field">
		<span class="field-title">密碼</span>
  		<input type="password" name="password" class="input-text" style="width:80%" title="請輸入密碼" value="" />
  	</div>
	
	<button class="button-login"><b>立即登入</b></button>
	<a href="index.groovy?m=signup" class="button-signup">註冊新帳號</a>
	
	<noscript>
		<div class="login-error">
		<span class="error-type">瀏覽器不支援</span>您的瀏覽器不支援 JavaScript！無法繼續使用本系統。
		</div>
	</noscript>

	<% if (helper.sess('login_error') != null) { %>
	<div class="login-error">
	    <span class="error-type">登入失敗</span>
	    ${helper.sess('login_error')}
    </div>
	<% } %>
</div></form>

<!--<a rel="slide1" href="https://lh3.googleusercontent.com/-3mN45lNi2OE/TmmndVIU4-I/AAAAAAAAFBw/FAvarIdS2mU/s800/%2525E8%25259E%2525A2%2525E5%2525B9%252595%2525E5%2525BF%2525AB%2525E7%252585%2525A7%2525202011-09-09%252520%2525E4%2525B8%25258B%2525E5%25258D%2525881.40.55.png" class="embedded-slide" title="我的課程主畫面"><img src="https://lh3.googleusercontent.com/-3mN45lNi2OE/TmmndVIU4-I/AAAAAAAAFBw/FAvarIdS2mU/s144/%2525E8%25259E%2525A2%2525E5%2525B9%252595%2525E5%2525BF%2525AB%2525E7%252585%2525A7%2525202011-09-09%252520%2525E4%2525B8%25258B%2525E5%25258D%2525881.40.55.png" border="0" /></a>
<a rel="slide1" href="https://lh6.googleusercontent.com/-k8PC-N7D-RU/TmmnmOOgzwI/AAAAAAAAFB4/FtfovFKhc_Y/s800/%2525E8%25259E%2525A2%2525E5%2525B9%252595%2525E5%2525BF%2525AB%2525E7%252585%2525A7%2525202011-09-09%252520%2525E4%2525B8%25258B%2525E5%25258D%2525881.42.09.png" class="embedded-slide" title="課程進度管理畫面"><img src="https://lh6.googleusercontent.com/-k8PC-N7D-RU/TmmnmOOgzwI/AAAAAAAAFB4/FtfovFKhc_Y/s144/%2525E8%25259E%2525A2%2525E5%2525B9%252595%2525E5%2525BF%2525AB%2525E7%252585%2525A7%2525202011-09-09%252520%2525E4%2525B8%25258B%2525E5%25258D%2525881.42.09.png" border="0" /></a>
<a rel="slide1" href="https://lh6.googleusercontent.com/-gBvLJVLD9k4/THc-uSoRLRI/AAAAAAAAEMI/RvfGZMfbOvs/s800/plweb-editor.png" class="embedded-slide" title="程式碼編輯器畫面"><img src="https://lh6.googleusercontent.com/-gBvLJVLD9k4/THc-uSoRLRI/AAAAAAAAEMI/RvfGZMfbOvs/s144/plweb-editor.png" border="0" /></a>-->
