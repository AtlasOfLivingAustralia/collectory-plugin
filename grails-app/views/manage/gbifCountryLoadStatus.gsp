
<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    %{--TODO At the moment this is performing a complete reload every 15 seconds while the load has not finished. Make this AJAXy--}%
    <title><g:message code="manage.gbifcls.title" /> ${country}</title>

    <g:if test="${gbifSummary}">
    <script type="text/javascript">
        if (!window.console) console = {log: function() {}};
        //setup the page
        $(document).ready(function(){
            window.setTimeout(refresh, 15000)
        });

        function refresh() {
            var isRunning = ${gbifSummary.isLoadRunning()}
            if(isRunning){
                console.log("Refreshing page...")
                window.location.reload(true);
                window.setTimeout(refresh, 15000);
            }
        }
    </script>
    </g:if>
</head>
<body>
<g:if test="${gbifSummary}">
<h1>${gbifSummary.isLoadRunning() ? 'Automatically' : 'Finished'} loading ${gbifSummary.loads.size} resources for ${country}
</h1>
<h3><g:message code="manage.gbifcls.title.completed" /> <g:formatNumber number="${gbifSummary.getPercentageComplete()}" format="#0.00"/> %</h3>
<h4><g:message code="manage.gbifcls.title.started" />: ${gbifSummary.startTime}
    <g:if test="${gbifSummary.finishTime}">
        <br/><g:message code="manage.gbifcls.title.finished" />: ${gbifSummary.finishTime}
    </g:if>
</h4>

<table class="table table-bordered table-striped">
    <thead>
        <th><g:message code="manage.gbifcls.th.resource" /></th>
        <th><g:message code="manage.gbifcls.th.status" /></th>
        <th><g:message code="manage.gbifcls.th.link" /></th>
    </thead>
<g:each in="${gbifSummary.loads}" var = "load" >
    <tr class="prop">
        <td class="name col-md-4">${load.name}</td>
        <td class="name col-md-2">${load.phase}</td>
        <td class="name col-md-4"><g:if test="${load.dataResourceUid}"><a href="${createLink(controller:'dataResource',action:'show', id:load.dataResourceUid)}"> View Data Resource Page </a></g:if></td>
    </tr>
</g:each>
</table>
</g:if>
<g:else>
    <h1><g:message code="manage.gbifcls.title04" /> ${country}</h1>
    <p>
        <g:message code="manage.gbifcls.title05" /> <g:link controller="manage" action="gbifLoadCountry"> <g:message code="manage.gbifcls.link01" />.</g:link>
    </p>

</g:else>

</body>
</html>