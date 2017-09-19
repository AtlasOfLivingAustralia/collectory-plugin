<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <g:set var="entityName" value="${entityType}" />
    <g:set var="entityNameLower" value="${cl.controller(type: entityType)}"/>
    <title>GBIF Syncing Healthcheck - Linked resources</title>
</head>
<body>
<div class="body content">
    <h1>GBIF Syncing Healthcheck - Linked resources </h1>
    <p class="lead">Some statistics on data resources and our ability to provision these to GBIF</p>
    <table class="table">
        <tr>
            <td>Linked data sets with data</td>
            <td>Data resources with data indexed by ALA today</td>
            <td>${linkedToInstitution.size() + linkedToDataProvider.size()}</td>
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

    </table>
</div>
</body>
</html>
