<%--
  Created by IntelliJ IDEA.
  User: markew
  Date: Jun 29, 2010
  Time: 3:45:49 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title><g:message code="public.listinstitutions.title" /></title></head>
  <body>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:each var="i" in="${institutions}">
      <p>${i.name} (${i.acronym})</p>
    </g:each>
  </body>
</html>