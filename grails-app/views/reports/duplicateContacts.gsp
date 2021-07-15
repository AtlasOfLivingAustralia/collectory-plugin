<%@ page import="au.org.ala.collectory.Classification; au.org.ala.collectory.Collection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /> - <g:message code="reports.dc.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body reports">
            <h1><g:message code="reports.dc.title01" /></h1>
            <p><g:message code="reports.dc.des01" />.</p>
            <h2><g:message code="reports.dc.title02" /></h2>
            <p><g:message code="reports.dc.des02" />:</p>
            <table class="separate-rows">
                <col width="35%"><col width="65%">
                <g:each in="${dupEmails}" var="de">
                    <tr>
                        <td>${de.email}</td>
                        <td>
                            <g:each in="${de.contacts}" var="c">
                                ${c.firstName} ${c.lastName} - (id=${c.id})
                                    <g:link controller="contact" action="show" id="${c.id}">View</g:link>
                                <br/>
                            </g:each>
                        </td>
                    </tr>
                </g:each>
            </table>
            <h2><g:message code="reports.dc.title03" /></h2>
            <p><g:message code="reports.dc.des03" />:</p>
            <table class="separate-rows">
                <col width="35%"><col width="65%">
                <g:each in="${dupNames}" var="dn">
                    <tr>
                        <td>${dn.firstName} ${dn.lastName}</td>
                        <td>
                            <g:each in="${dn.contacts}" var="c">
                                ${c.email} - (id=${c.id})
                                    <g:link controller="contact" action="show" id="${c.id}">View</g:link>
                                <br/>
                            </g:each>
                        </td>
                    </tr>
                </g:each>
            </table>
        </div>
    </body>
</html>