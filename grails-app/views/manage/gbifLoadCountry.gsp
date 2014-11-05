<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.ala.skin}" />
    <title><g:message code="manage.gbiflc.title" /></title>
</head>
<body>
<h1><g:message code="manage.gbiflc.title01" /></h1>
<div id="baseForm">
    <g:form action="loadAllGbifForCountry" controller="manage">
            <div class="span6">
                    <table>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="country"><g:message code="manage.gbiflc.label01" />:</label></td>
                            <td valign="top" class="value">
                                <g:select name="country" from="${pubMap.entrySet()}" optionKey="key" optionValue="value"/>
                            </td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="gbifUsername"><g:message code="manage.gbiflc.label02" />:</label></td>
                            <td valign="top" class="value"><g:field type="text" name="gbifUsername" required="true" value="" /></td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="gbifPassword"><g:message code="manage.gbiflc.label03" />:</label> </td>
                            <td valign="top" class="value"><g:field type="password" name="gbifPassword" required="true" value="" /></td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><label for="maxResources"><g:message code="manage.gbiflc.label04" />:</label></td>
                            <td valign="top" class="value"><g:field type="number" name="maxResources"  value="1"/></td>
                        </tr>
                    </table>

                <span class="button"><input type="submit" name="performGBIFLoad" value="Load" class="save btn"></span>
            </div>

            <div class="well pull-right span5">
                <p>
                    <g:message code="manage.gbiflc.des01" />.<br/>
                    <g:message code="manage.gbiflc.des02" />.
                    <br/>
                    <g:message code="manage.gbiflc.des03" />
                    <a href="http://www.gbif.org/user/register"><g:message code="manage.gbiflc.link01" /></a>.
                </p>
                <p>
                    <b><g:message code="manage.gbiflc.des04" /></b>: <g:message code="manage.gbiflc.des05" />.<br/>
                    <g:message code="manage.gbiflc.des06" />.
                </p>
            </div>
        </div>

    </g:form>
</div>

</body>
</html>