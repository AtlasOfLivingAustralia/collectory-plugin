<%@ page import="au.org.ala.collectory.Contact; au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.contacts.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <div id="full">
                <table class="table table-striped table-bordered">
                  <colgroup><col width="80%"/><col width="10%"/><col width="10%"/></colgroup>
                  <tr><td><g:message code="reports.contacts.full.tr01" />.</td>
                    <td colspan="2"><a href="#"
                          onclick="document.getElementById('names-only').style.display='block';document.getElementById('full').style.display='none'">
                    <g:message code="reports.contacts.full.link01" /></a></td></tr>
                  <tr class="reportGroupTitle"><td><g:message code="reports.contacts.full.table0201" /></td><td><g:message code="reports.contacts.full.table0202" /></td><td>Phone</td></tr>
                  <g:each var='c' in="${Contact.findAll([sort: 'lastName'])}">
                    <tr>
                      <td><g:link controller="contact" action="show" id="${c.id}">${c.buildName()}</g:link></td>
                      <td>${c.email?'Y':' '}</td>
                      <td>${c.phone?'Y':' '}</td>
                    </tr>
                  </g:each>
                </table>
              </div>
              <div id="names-only" style="display:none;">
                <table>
                  <colgroup><col width="80%"/><col width="10%"/><col width="10%"/></colgroup>
                  <tr><td><g:message code="reports.contacts.namesonly.table0101" /></td>
                    <td colspan="2"><a href="#"
                          onclick="document.getElementById('names-only').style.display='none';document.getElementById('full').style.display='block'">
                    (Show attributes)</a></td></tr>
                  <tr class="reportGroupTitle"><td colspan="3"><g:message code="reports.contacts.namesonly.table0201" /></td></tr>
                  <g:each var='c' in="${Contact.findAll([sort: 'lastName'])}">
                    <tr>
                      <td>${c.buildName()}</td>
                      <td></td>
                      <td></td>
                    </tr>
                  </g:each>
                </table>
              </div>
            </div>
        </div>
    </body>
</html>
