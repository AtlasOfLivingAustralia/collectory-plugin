<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <title><g:message code="manage.extload.title" /></title>
</head>
<body>
<h1><g:message code="manage.extload.title01" /></h1>
<div id="baseForm">
    <g:form action="searchForResources" controller="manage">
        <g:hiddenField name="configuration.guid" value="${configuration.guid}"/>
            <div class="span6">
                    <table>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="name"><g:message code="manage.extload.label01" />:</label></td>
                            <td valign="top" class="value">
                                <g:field name="name" type="text" value="${configuration.name}"/>
                                <cl:helpText code="manage.extload.label01.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="description"><g:message code="manage.extload.label02" />:</label></td>
                            <td valign="top" class="value">
                                <g:field name="description" type="text" size="64" value="${configuration.description}"/>
                                <cl:helpText code="manage.extload.label02.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="dataProviderUid"><g:message code="manage.extload.label03" />:</label></td>
                            <td valign="top" class="value">
                                <g:select name="dataProviderUid" from="${dataProviders}" optionKey="uid" optionValue="name" value="${configuration.dataProviderUid}"/>
                                <cl:helpText code="manage.extload.label03.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="adaptorString"><g:message code="manage.extload.label04" />:</label></td>
                            <td valign="top" class="value">
                                <g:select name="adaptorString" from="${adaptors}" optionKey="adaptorString" optionValue="name" value="${configuration.adaptorString}"/>
                                <cl:helpText code="manage.extload.label04.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="endpoint"><g:message code="manage.extload.label05" />:</label></td>
                            <td valign="top" class="value">
                                <g:field name="endpoint" type="url" value="${configuration.endpoint}"/>
                                <cl:helpText code="manage.extload.label05.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="country"><g:message code="manage.extload.label06" />:</label></td>
                            <td valign="top" class="value">
                                <g:select name="country" from="${countryMap.entrySet()}" optionKey="key" optionValue="value" values="${configuration.country}"/>
                                <cl:helpText code="manage.extload.label06.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="recordType"><g:message code="manage.extload.label07" />:</label></td>
                            <td valign="top" class="value">
                                <g:select name="recordType" from="${datasetTypeMap.entrySet()}" optionKey="key" optionValue="value" values="${configuration.recordType}"/>
                                <cl:helpText code="manage.extload.label07.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="username"><g:message code="manage.extload.label08" />:</label></td>
                            <td valign="top" class="value"><g:field type="text" name="username" required="true" value="${configuration.username}" />
                            <cl:helpText code="manage.extload.label08.help"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="password"><g:message code="manage.extload.label09" />:</label></td>
                            <td valign="top" class="value"><g:field type="text" name="password"  value="${configuration.password}" />
                            <cl:helpText code="manage.extload.label09.help"/>
                            </td>
                        </tr>
                    </table>
                <span class="button"><input type="submit" name="performReview" value="Review" class="save btn"></span>
            </div>

            <div class="well pull-right span5">
                <p>
                    <g:message code="manage.extload.des01" />
                    <g:message code="manage.extload.des02" />
                </p>
                <p>
                     <g:message code="manage.extload.des03" />
                 </p>
            </div>
        </div>

    </g:form>
</div>

</body>
</html>