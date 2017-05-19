
<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    %{--TODO At the moment this is performing a complete reload every 15 seconds while the load has not finished. Make this AJAXy--}%
    <title><g:message code="manage.extloads.title" /> ${configuration.name}</title>

    <g:if test="${load}">
    <script type="text/javascript">
        if (!window.console) console = {log: function() {}};
        //setup the page
        $(document).ready(function(){
            window.setTimeout(refresh, ${refreshInterval})
        });

        function refresh() {
            var isRunning = ${!load.isComplete()}
            if(isRunning){
                console.log("Refreshing page...")
                window.location.reload(true);
                window.setTimeout(refresh, ${refreshInterval});
            }
        }
    </script>
    </g:if>
</head>
<body>
<g:if test="${load}">
<h1>${load.isComplete() ? 'Finished' : 'Automatically'} loading ${load.resources.size()} resources for ${configuration.name}
</h1>
<h3><g:message code="manage.extloads.title.completed" /> <g:formatNumber number="${load.getPercentageComplete()}" format="#0.00"/> %</h3>
<h4><g:message code="manage.extloads.title.started" />: ${load.startTime}
    <g:if test="${load.finishTime}">
        <br/><g:message code="manage.extloads.title.finished" />: ${load.finishTime}
    </g:if>
</h4>

<table class="table table-bordered table-striped">
    <thead>
        <th><g:message code="manage.extloads.th.resource" /></th>
        <th><g:message code="manage.extloads.th.status" /></th>
        <th><g:message code="manage.extloads.th.notes" /></th>
        <th><g:message code="manage.extloads.th.link" /></th>
    </thead>
<g:each in="${load.resources}" var = "resource" >
    <tr class="prop">
        <td class="name span2">${resource.name}</td>
        <td class="name span2"><span title="<g:message code="manage.phase.${resource.phase}.detail"/>"><g:message code="manage.phase.${resource.phase}"/></span></td>
        <td class="name span4"><cl:notes notes="${resource.notes}"/></td>
        <td class="name span2"><g:if test="${resource.uid && resource.phase.terminal}"><a href="${createLink(controller:'dataResource',action:'show', id:resource.uid)}"> View Data Resource Page </a></g:if></td>
    </tr>
</g:each>
</table>
</g:if>
<g:else>
    <h1><g:message code="manage.extloads.title04" /> ${configuration.name}</h1>
    <p>
        <g:message code="manage.extloads.title05" /> <g:link controller="manage" action="gbifLoadCountry"> <g:message code="manage.extloads.link01" />.</g:link>
    </p>

</g:else>

</body>
</html>