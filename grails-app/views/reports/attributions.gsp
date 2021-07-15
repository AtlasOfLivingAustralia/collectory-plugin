<%@ page import="au.org.ala.collectory.Collection; au.org.ala.collectory.Institution" %><html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.attributions.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.attributions.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <p><a href="#institutions"><g:message code="reports.attributions.link01" /></a></p>

            <div class="dialog" id="collections">
                <table>
                    <tr class="reportGroupTitle"><td><g:message code="reports.attributions.collections.td01" /></td><td>${Collection.count()} <g:message code="reports.attributions.collections.td02" />.</td></tr>
                    <g:each var='c' in="${collAttributions}">
                        <tr><td><g:link controller="public" action="show" id="${c.pgs.uid}">${c.pgs.name}</g:link></td>
                        <g:each var='a' in="${c.attribs}" status="i">
                            <g:if test="${i>0}"><tr></tr><td></td></g:if>
                            <td>
                                <g:if test="${a?.url}">
                                    <a href="${a.url}" target="_blank" class="external_icon">${a.name}</a>
                                </g:if>
                                <g:else>
                                    ${a?.name}
                                </g:else>
                            </td></tr>
                        </g:each>
                    </g:each>
                </table>
            </div>

            <div class="dialog" id="institutions">
                <table>
                    <tr class="reportGroupTitle"><td><g:message code="reports.attributions.institutions.td01" /></td><td>${Institution.count()} <g:message code="reports.attributions.institutions.td02" />.</td></tr>
                    <g:each var='c' in="${instAttributions}">
                        <tr><td><g:link controller="public" action="show" id="${c.pgs.uid}">${c.pgs.name}</g:link></td>
                        <g:each var='a' in="${c.attribs}" status="i">
                            <g:if test="${i>0}"><td></td></g:if>
                            <td>
                                <g:if test="${a?.url}">
                                    <a href="${a.url}" target="_blank" class="external_icon">${a.name}</a>
                                </g:if>
                                <g:else>
                                    ${a?.name}
                                </g:else>
                            </td></tr>
                        </g:each>
                    </g:each>
                </table>
            </div>

        </div>
    </body>
</html>
