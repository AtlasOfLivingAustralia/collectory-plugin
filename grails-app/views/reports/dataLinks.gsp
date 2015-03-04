<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
    </head>
    <body>
        <div class="nav">
            <ul>
            <li><span class="menuButton"><cl:homeLink/></span></li>
            <li><span class="menuButton"><g:link class="list" action="list"><g:message code="reports.li.reports" /></g:link></span></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.dl.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <p><g:message code="reports.dl.des01" />.</p>
              <p><g:message code="reports.dl.des02" />.</p>
              <table class="table table-striped table-bordered">
                <colgroup><col width="45%"/><col width="10%"/><col width="45%"/></colgroup>
                <tr class="reportHeaderRow"><td><g:message code="reports.dl.provider" /></td><td></td><td><g:message code="reports.dl.consumer" /></td></tr>
                <g:each var='link' in="${links}">
                  <g:set var="provider" value="${ProviderGroup._get(link.provider)}"/>
                  <g:set var="consumer" value="${ProviderGroup._get(link.consumer)}"/>
                  <tr>
                    <td><g:link controller="${cl.controllerFromUid(uid: link.provider)}" action="show" id="${link.provider}">
                      ${provider.name} <cl:entityIndicator entity="${provider}"/>
                    </g:link></td>
                    <td> &lt;=&gt; </td>
                    <td><g:link controller="${cl.controllerFromUid(uid: link.consumer)}" action="show" id="${link.consumer}">
                      ${consumer.name} <cl:entityIndicator entity="${consumer}"/>
                    </g:link></td>
                  </tr>
                </g:each>
              </table>
            </div>
        </div>
    </body>
</html>
