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
            <h1><g:message code="reports.mrd.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <h4>${statistics.size()} <g:message code="reports.mrd.title02" />.</h4>
              <table class="table table-striped table-bordered">
                <colgroup><col width="50%"/><col width="30%"/><col width="20%"/></colgroup>
                <thead>
                  <th><g:message code="reports.mrd.th01" /></th><th><g:message code="reports.mrd.th02" /></th><th><g:message code="reports.mrd.th03" /></th>
                </thead>

                <g:each var='m' in="${statistics}">
                  <tr>
                    <td><g:link controller="public" action="show" id="${m.uid}">${m.name}</g:link></td>
                    <td></td>
                    <td>${m.numBiocacheRecords > 0 ? m.numBiocacheRecords : '-'}</td>
                  </tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
