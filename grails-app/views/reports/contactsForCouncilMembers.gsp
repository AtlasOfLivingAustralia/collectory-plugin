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
            <h1><g:message code="reports.cfcm.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <colgroup><col width="60%"/><col width="40%"/></colgroup>

                <tr class="reportGroupTitle"><td colspan="2"><g:message code="reports.chafc" /> (${chafc.size()})</td>
                <g:each var="p" in="${chafc}">
                  <tr>
                    <td><g:link controller="collection" action="show" id="${p.id}">${p.name}</g:link></td>
                    <g:if test="${p.email}">
                      <td>${p.email}</td>
                    </g:if>
                    <g:elseif test="${p.contact}">
                      <td style="color:blue;">(${p.contact})</td>
                    </g:elseif>
                    <g:else>
                      <td style="color:red;"><g:message code="reports.cfcm.noun" /></td>
                    </g:else>
                  </tr>
                </g:each>

                <tr class="reportGroupTitle"><td colspan="2"><g:message code="reports.chaec" /> (${chaec.size()})</td>
                <g:each var="p" in="${chaec}">
                  <tr>
                    <td><g:link controller="collection" action="show" id="${p.id}">${p.name}</g:link></td>
                    <g:if test="${p.email}">
                      <td>${p.email}</td>
                    </g:if>
                    <g:elseif test="${p.contact}">
                      <td style="color:blue;">(${p.contact})</td>
                    </g:elseif>
                    <g:else>
                      <td style="color:red;"><g:message code="reports.cfcm.noun" /></td>
                    </g:else>
                  </tr>
                </g:each>

                <tr class="reportGroupTitle"><td colspan="2"><g:message code="reports.chacm" /> (${chacm.size()})</td>
                <g:each var="p" in="${chacm}">
                  <tr>
                    <td><g:link controller="collection" action="show" id="${p.id}">${p.name}</g:link></td>
                    <g:if test="${p.email}">
                      <td>${p.email}</td>
                    </g:if>
                    <g:elseif test="${p.contact}">
                      <td style="color:blue;">(${p.contact})</td>
                    </g:elseif>
                    <g:else>
                      <td style="color:red;"><g:message code="reports.cfcm.noun" /></td>
                    </g:else>
                  </tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
