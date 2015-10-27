
<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    %{--TODO At the moment this is performing a complete reload every 15 seconds while the load has not finished. Make this AJAXy--}%
    <title><g:message code="manage.gbifdds.title" /> ${datasetKey}</title>

    <g:if test="${gbifSummary}">
        <script type="text/javascript">
            if (!window.console) console = {log: function() {}};
            //setup the page
            $(document).ready(function(){
                window.setTimeout(refresh, 15000)
            });

            function refresh() {
                var isRunning = ${! gbifSummary.isComplete()};
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
    <h1>${gbifSummary.gbifResourceUid} - ${gbifSummary.phase}</h1>
</g:if>
<g:else>
    <h1><g:message code="manage.gbifdds.title04" /> ${datasetKey}</h1>
    <p>
        <g:message code="manage.gbifdds.title05" /> <g:link controller="manage" action="gbifDatasetDownload" id=""> <g:message code="manage.gbifdds.link01" />.</g:link>
    </p>
</g:else>
</body>
</html>