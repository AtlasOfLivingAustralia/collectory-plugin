<%@ page import="au.org.ala.collectory.Collection" %>
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
            <h1><g:message code="reports.collections.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>

            <div class="dialog">
                <g:if test="${simple != 'true'}">
                    <p><strong><g:message code="reports.collections.view" /></strong> <g:message code="reports.collections.des01" />.</p>
                    <p><strong><g:message code="reports.collections.edit" /></strong> <g:message code="reports.collections.des02" />.</p>
                </g:if>
                <p><g:message code="reports.collections.des03" args="[collections.size()]" />.
                <g:if test="${simple == 'true'}">
                    <g:link controller="reports" action="collections" params="[simple:'false']"><g:message code="reports.collections.des04" />.</g:link></p>
                </g:if>
                <g:else>
                    <g:link controller="reports" action="collections" params="[simple:'true']"><g:message code="reports.collections.des05" />.</g:link></p>
                </g:else>

              <table class="table table-striped table-bordered">
                <g:if test="${simple != 'true'}">
                    <colgroup><col width="60%"/><col width="20%"/><col width="10%"/><col width="10%"/></colgroup>
                </g:if>

                <g:each var='c' in="${collections}" status="i">
                  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td>${c.name}</td>
                    <g:if test="${simple != 'true'}">
                        <td>${c.acronym}</td>
                        <td><g:link controller="public" action="show" id="${c.uid}"><g:message code="reports.collections.view" /></g:link></td>
                        <td><g:link controller="collection" action="show" id="${c.uid}"><g:message code="reports.collections.edit" /></g:link></td>
                    </g:if>
                  </tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
