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
    <div class="span5">
        <table>
            <tr class="prop">
                <td valign="top" class="name"><label for="guid"><g:message code="manage.gbifdldataset.label01" />:</label></td>
                <td valign="top" class="value"><g:field type="text" name="guid" required="true" value="${guid}" readonly="true" /></td>
            </tr>
            <tr class="prop">
                <td valign="top" class="name"><label for="gbifUsername"><g:message code="manage.gbifdldataset.label02" />:</label></td>
                <td valign="top" class="value"><g:field type="text" name="gbifUsername" required="true" value="" /></td>
            </tr>
            <tr class="prop">
                <td valign="top" class="name"><label for="gbifPassword"><g:message code="manage.gbifdldataset.label03" />:</label> </td>
                <td valign="top" class="value"><g:field type="password" name="gbifPassword" required="true" value="" /></td>
            </tr>
        </table>
        <span class="button">
            <input type="submit" name="performGBIFLoad" value="Reload" class="save btn">
        </span>
    </div>

    <div class="well pull-right span5">
        <p>
            <g:message code="manage.gbifdldataset.des01" />.<br/>
            <g:message code="manage.gbifdldataset.des02" />.
            <br/>
            <g:message code="manage.gbifdldataset.des03" />
            <a href="http://www.gbif.org/user/register"><g:message code="manage.gbifdldataset.link01" /></a>.
        </p>
        <p>
            <b><g:message code="manage.gbifdldataset.des04" /></b>: <g:message code="manage.gbifdldataset.des05" />.<br/>
            <g:message code="manage.gbifdldataset.des06" />.
        </p>
    </div>
    </div>

</g:form>
</div>

</body>
</html>