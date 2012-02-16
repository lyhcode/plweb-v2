<%helper=request.get('helper')%>
<%helper.attr('files').each{file->%>
<div>${file.name}</div>
<div class="prettyhtml"><pre>${file.content}</pre></div>
<%}%>