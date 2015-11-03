
<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
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
    <h1>Reloading dataset - ${gbifSummary.gbifResourceUid}</h1>
    <h2>Status: ${gbifSummary.phase}</h2>
    <g:if test="${gbifSummary.isComplete()}">
        <g:link controller="dataResource" action="show" id="${gbifSummary.dataResourceUid}"><g:message code="manage.gbifdds.returnToDataResource" args="${[gbifSummary.dataResourceUid]}"></g:message></g:link>
    </g:if>
</g:if>
</body>
</html>