<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%
	helper = request.get('helper')
%>
<html>
<head>
	<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
	<title>PLWeb 程式設計練習系統 2.0</title>
	${helper.htmlhead()}

	<!-- Compass -->
	<link href="${helper.basehref}stylesheets/screen.css" media="screen, projection" rel="stylesheet" type="text/css" />
	<link href="${helper.basehref}stylesheets/print.css" media="print" rel="stylesheet" type="text/css" />
	<!--[if IE]>
	<link href="${helper.basehref}stylesheets/ie.css" media="screen, projection" rel="stylesheet" type="text/css" />
	<![endif]-->

	<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}css/jquery.tipsy.css" />
	<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}css/colorbox-style1/colorbox.css" />
	<link rel="stylesheet" type="text/css" media="screen" href="${helper.basehref}css/jquery.jgrowl.css" />
	<script type="text/javascript" src="${helper.basehref}js/jquery.spotlight.min.js"></script>
	<script type="text/javascript" src="${helper.basehref}js/jquery.colorbox.min.js"></script>
	<script type="text/javascript" src="${helper.basehref}js/jquery.tipsy.js"></script>
	<script type="text/javascript" src="${helper.basehref}js/jquery.jgrowl_minimized.js"></script>
	<script type="text/javascript" src="${helper.basehref}login/index.js"></script>
	<!--<script type="text/javascript" src="http://widgets.amung.us/tab.js"></script><script type="text/javascript">WAU_tab('kj5l1p82s0bf', 'bottom-left')</script>-->
</head>
<body class="theme fancy-layout">
	<div class="page-wrapper"><div class="page login-page">
		<div class="header">
			<a href="/"><img class="plweb-logo" src="${helper.basehref}img/plweb_logo.png" alt="PLWeb Logo" border="0" /></a>
			<div class="topNav">
				<div class="topLinks">
					<a href="http://help.plweb.org/installation:jdk" class="embedded-link">Java 安裝設定</a>
					|
					<a href="http://help.plweb.org/technical_support" class="embedded-link">技術支援</a>
					|
					<a href="http://help.plweb.org/ppt" class="embedded-link">檔案下載</a>
				</div>
				
				<div class="formLinks">
					<ul>
						<li><span><a href="http://help.plweb.org/demo" class="link-item embedded-link">功能展示</a></span></li>
						<li><span><a href="http://help.plweb.org/progress" class="link-item embedded-link">開課流程</a></span></li>
						<li><span><a href="http://help.plweb.org/workshop" class="link-item embedded-link">研習活動</a></span></li>
					</ul>
				</div>
			</div>
			
		</div>
		<div class="content-wrapper"><div class="content">
			<div class="content-left"><div class="content-left-inner">
				<img src="${helper.basehref}img/plweb_subtitle.png" />
				<div class="sidebar">
					<ul>
						<li>
							<span><a class="sidebar-link" href="${response.encodeUrl('/login/index.groovy?m=login')}"><strong>登入</strong></a></span>
							<div class="desc">線上有 ${helper.attr('ucount')} 位使用者！</div>
						</li>
						<li>
							<span><a class="sidebar-link" href="${response.encodeUrl('/login/index.groovy?m=signup')}"><strong>註冊</strong></a></span>
							<div class="desc">免費申請 PLWeb 帳號，加入我們的程式設計教學社群！</div>
						</li>
						<li>
							<div style="text-align:right;line-height:1.25em;">
								<a href="${response.encodeUrl('/login/index.groovy?m=account')}">忘記帳號？</a><br/>
								<a href="${response.encodeUrl('/login/index.groovy?m=password')}">忘記密碼？</a>
							</div>
							<div class="desc">使用本功能可以協助您找回帳號或密碼！</div>
						</li>
					</ul>
					
					<!--facebook social widget--->
					<iframe src="http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fplweb.org%2F&amp;layout=standard&amp;show_faces=true&amp;width=230&amp;action=like&amp;font=arial&amp;colorscheme=light&amp;height=80" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:230px; height:80px; padding: 10px;" allowTransparency="true"></iframe>
				</div>
			</div></div>
			<div class="content-center"><div class="content-center-inner">
				<% helper.include "${helper.fetch('m', 'login')}.groovy" %>
			</div></div>
			<div class="content-footer"><div class="content-footer-inner">
				PLWeb 2.0 Copyright &copy; PLWeb School of Programming
			</div></div>
		</div></div>
	</div></div>
	
<!-- Google Analytics -->
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try{
var pageTracker = _gat._getTracker("UA-18484833-1");
pageTracker._trackPageview();
} catch(err) {}
</script>
		
</body>
</html>
