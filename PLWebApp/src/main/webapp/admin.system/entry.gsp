<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<% helper = request.get('helper') %>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<title>PLWeb - <%=helper.attr('html_title')?helper.attr('html_title'):'System Administration'%></title>
		<%=helper.htmlhead()%>
	    
	    <link rel="stylesheet" type="text/css" href="<%=helper.basehref%>extjs/resources/css/ext-all.css"/>
	    <link rel="stylesheet" type="text/css" href="<%=helper.basehref%>admin.system/entry.css"/>
	    
	    <script type="text/javascript" src="<%=helper.basehref%>extjs/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="<%=helper.basehref%>extjs/ext-all.js"></script>
	    
		<script type="text/javascript" src="<%=helper.basehref%>admin.system/entry.js"></script>
		<script type="text/javascript" src="<%=helper.basehref%>admin.system/entry.MenuPanel.js"></script>
	</head>
<body>
<div id="entry-main"></div>
</body>
</html>
