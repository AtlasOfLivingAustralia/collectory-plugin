<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <g:set var="entityName" value="${entityType}" />
    <g:set var="entityNameLower" value="${cl.controller(type: entityType)}"/>
    <title>GBIF Syncing Healthcheck</title>
</head>
<body>
<div class="body content">
    <h1>GBIF Sync - sync results</h1>
    <div class="pull-right">
        <g:link class="btn btn-primary" action="syncAllResources"
                onclick="return confirm('${message(code: 'default.button.updateall.confirm.message', default: 'Are you sure you want to sync all ? This will take some time to complete.')}');">
            <i class="ui-icon-arrow-1-s"></i> Sync resources
        </g:link>
    </div>
    <g:if test="${errorMessage}">
        <span class="alert alert-warning">${errorMessage}</span>
    </g:if>
    <g:else>
        <p class="lead">Syncing results</p>
        <table class="table">
            <thead>
                <th></th>
                <th>newly registered</th>
                <th>updated</th>
            </thead>
            <tbody>
            <tr>
                <td>Data resources</td>
                <td>${results.resourcesRegistered}</td>
                <td>${results.resourcesUpdated}</td>
            </tr>
            <tr>
                <td>Institutions</td>
                <td>${results.dataProviderRegistered}</td>
                <td>${results.resourcesUpdated}</td>
            </tr>
            <tr>
                <td>Data providers</td>
                <td>${results.institutionsRegistered}</td>
                <td>${results.institutionsUpdated}</td>
            </tr>
            </tbody>
        </table>
    </g:else>
</div>
</body>
</html>
