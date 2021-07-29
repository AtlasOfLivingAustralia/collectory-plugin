<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <title><g:message code="manage.gbiflc.title" /></title>
</head>
<body>
<h1><g:message code="manage.gbifdldataset.reload.title01" /></h1>
<div id="baseForm">
<g:form action="loadDataset" controller="manage">
    <div class="col-md-5">
        <label for="guid"><g:message code="manage.gbifdldataset.label01" />:</label>
        <g:field type="text" class="form-control" name="guid" required="true" value="${guid}" readonly="true" />
        <br/>
        <label for="repatriationCountry"><g:message code="manage.gbifdldataset.label02" />:</label>
        <g:field type="text" class="form-control" name="repatriationCountry" required="true" value="${dr.repatriationCountry}" readonly="true" />
        <br/>
        <input type="submit" name="performGBIFLoad" value="Reload" class="save btn btn-default">
    </div>

    <div class="well pull-right col-md-5">
        <p>
            <g:message code="manage.gbifdldataset.des01" />.<br/>
            <g:message code="manage.gbifdldataset.des02" />.
            <br/>
            <g:message code="manage.gbifdldataset.des03" />
            <a href="http://www.gbif.org/user/register"><g:message code="manage.gbifdldataset.link01" /></a>.
        </p>
    </div>
    </div>

</g:form>
</div>

</body>
</html>