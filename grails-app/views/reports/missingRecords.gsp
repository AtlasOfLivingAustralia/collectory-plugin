<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
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
            <h1><g:message code="reports.mr.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <h4><g:message code="reports.mr.title02" />.</h4>
              <table class="table table-striped table-bordered">
                <colgroup><col width="50%"/><col width="30%"/><col width="20%"/></colgroup>
                <thead>
                  <th><g:message code="reports.mr.th01" /></th><th><g:message code="reports.mr.th02" /></th><th><g:message code="reports.mr.th03" /></th>
                </thead>

                <g:each var='m' in="${mrs}">
                  <tr>
                    <td><g:link controller="public" action="show" id="${m.collection.uid}">${m.collection.name}</g:link></td>
                    <td>${m.claimed}</td>
                    <td>${m.biocacheCount}</td>
                  </tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
