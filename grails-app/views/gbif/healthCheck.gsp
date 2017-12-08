<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <g:set var="entityName" value="${entityType}" />
    <g:set var="entityNameLower" value="${cl.controller(type: entityType)}"/>
    <title>GBIF Syncing Healthcheck</title>
</head>
<body>

<g:set var="org" value="${grailsApplication.config.skin.orgNameLong}"/>
<div class="body content">
    <h1>GBIF Syncing Healthcheck</h1>
    <div class="pull-right">
        <g:link class="btn btn-primary" action="downloadCSV"><i class="ui-icon-arrow-1-s"></i> Download CSV</g:link>

        <cl:ifGranted role="${grailsApplication.config.gbifRegistrationRole}">
        <g:link class="btn btn-primary" action="syncAllResources"
                onclick="return confirm('${message(code: 'default.button.updateall.confirm.message', default: 'Are you sure you want to sync all ? This will take some time to complete.')}');">
            <i class="ui-icon-arrow-1-s"></i> Sync resources
        </g:link>
        </cl:ifGranted>
    </div>

    <p class="lead">Some statistics on data resources and ${org} ability to provision these to GBIF</p>
    <table class="table">
        <tr>
            <td>Records in ${org}</td>
            <td>Records indexed by ${org} today</td>
            <td>${indexedRecords}</td>
        </tr>
        <tr>
            <td>Records shareable</td>
            <td>Records shareable by ${org} today (no licencing issues, has an associated organisation & marked as shareable)</td>
            <td>${recordsShareable}
                <g:if test="${indexedRecords}">
                    (${Math.floor(recordsShareable/indexedRecords * 100)}%)
                </g:if>
            </td>
        </tr>
        <tr>
            <td>Data sets with data</td>
            <td>Data resources with data indexed by ${org} today</td>
            <td>${dataResourcesWithData.size()}</td>
        </tr>
        <tr>
            <td>Shareable</td>
            <td>Data resources that can be shared today</td>
            <td>${shareable.size()}</td>
        </tr>
        <tr>
            <td>Licence issues (i.e. not CC)</td>
            <td>Data resources that can NOT be shared today due to licence issues</td>
            <td>${licenceIssues.size()}</td>
        </tr>
        <tr>
            <td>Marked as not shareable</td>
            <td>Data resources have been marked explicitly as not to be shared with GBIF</td>
            <td>${notShareable.size()}</td>
        </tr>
        <tr>
            <td>Provided by GBIF</td>
            <td>Data resources originally provided by GBIF - so we dont share them back</td>
            <td>${providedByGBIF.size()}</td>
        </tr>
        <tr>
            <td>Not shareable (no owner)</td>
            <td>Data resources that are not associated with either a institution or a data provider</td>
            <td>${notShareableNoOwner.size()}</td>
        </tr>
        <tr>
            <td><g:link controller="gbif" action="healthCheckLinked">Linked to institution / data provider </g:link></td>
            <td>Data resources that are associated with institution/data partner</td>
            <td>${linkedToInstitution.size() + linkedToDataProvider.size()}</td>
        </tr>
        <tr>
            <td>Linked to institution</td>
            <td>Data resources that are associated with institution</td>
            <td>${linkedToInstitution.size()}</td>
        </tr>
        <tr>
            <td>Linked to data provider</td>
            <td>Data resources that are associated with data provider</td>
            <td>${linkedToDataProvider.size()}</td>
        </tr>
    </table>
</div>
</body>
</html>
