<%@ page import="au.org.ala.collectory.Collection;" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <title><g:message code="collection.base.label" default="Edit collection metadata" /></title>
    </head>
    <body>
        <div class="nav">
          <g:if test="${mode == 'create'}">
            <h1><g:message code="collection.range.title01" /></h1>
          </g:if>
          <g:else>
            <h1><g:message code="collection.title.editing" />: ${command.name}</h1>
          </g:else>
        </div>
        <div id="baseForm" class="body">
            <g:if test="${message}">
            <div class="message">${message}</div>
            </g:if>
            <g:hasErrors bean="${command}">
            <div class="errors">
                <g:renderErrors bean="${command}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" name="baseForm" action="range">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <div class="dialog">
                    <table>
                        <tbody>

                        <!-- geographic range -->
                        <tr><td colspan="3"><h3><g:message code="collection.range.title" /></h3></td></tr>
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="geographicDescription"><g:message code="collection.range.label01" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'geographicDescription', 'errors')}">
                                <g:textField name="geographicDescription" class="input-xxlarge" value="${command?.geographicDescription}" />
                                <cl:helpText code="collection.geographicDescription"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="states"><g:message code="collection.range.label02" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'states', 'errors')}">
                                <g:textField name="states" value="${command?.states}" />
                                <cl:helpText code="collection.states"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <tr class="prop">
                          <td colspan="2"><g:message code="collection.range.label03" />.</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="eastCoordinate"><g:message code="collection.range.label04" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'eastCoordinate', 'errors')}">
                              <g:textField name="eastCoordinate" value="${cl.showDecimal(value: command.eastCoordinate)}" />
                              <cl:helpText code="collection.eastCoordinate"/>
                          </td>
                          <cl:helpTD/>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="westCoordinate"><g:message code="collection.range.label05" /></label>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'westCoordinate', 'errors')}">
                              <g:textField name="westCoordinate" value="${cl.showDecimal(value: command.westCoordinate)}" />
                              <cl:helpText code="collection.westCoordinate"/>
                          </td>
                          <cl:helpTD/>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="northCoordinate"><g:message code="collection.range.label06" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'northCoordinate', 'errors')}">
                                <g:textField name="northCoordinate" value="${cl.showDecimal(value: command.northCoordinate)}" />
                                <cl:helpText code="collection.northCoordinate"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="southCoordinate"><g:message code="collection.range.label07" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'southCoordinate', 'errors')}">
                              <g:textField name="southCoordinate" value="${cl.showDecimal(value: command.southCoordinate)}" />
                              <cl:helpText code="collection.southCoordinate"/>
                          </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- taxonomic range -->
                        <tr><td colspan="3"><h3><g:message code="collection.range.title02" /></h3></td></tr>
                        <tr class="prop">
                            <td valign="top" class="checkbox">
                              <label for="kingdomCoverage"><g:message code="collection.range.label08" /></label>
                            </td>
                            <td valign="top" class="checkbox">
                                <cl:checkBoxList name="kingdomCoverage" from="${Collection.kingdoms}" value="${command?.kingdomCoverage}" />
                                <cl:helpText code="collection.kingdomCoverage"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="scientificNames"><g:message code="collection.range.label09" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'scientificNames', 'errors')}">
                                <!--richui:autoComplete name="scientificNames" controller="collection" action="scinames" title="sci name"/-->
                              <g:textArea name="scientificNames" value="${command.listScientificNames().join(',')}"/>
                              <cl:helpText code="collection.scientificNames"/>
                          </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- stats -->
                        <tr><td colspan="3"><h3><g:message code="collection.range.title03" /></h3></td></tr>
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="numRecords"><g:message code="collection.numRecords.label" default="Number of specimens" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'numRecords', 'errors')}">
                                <g:textField name="numRecords" value="${cl.showNumber(value: command.numRecords)}" />
                                <cl:helpText code="collection.numRecords"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="numRecordsDigitised"><g:message code="collection.numRecordsDigitised.label" default="Number of records digitised" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'numRecordsDigitised', 'errors')}">
                                <g:textField name="numRecordsDigitised" value="${cl.showNumber(value: command.numRecordsDigitised)}" />
                                <cl:helpText code="collection.numRecordsDigitised"/>
                              </td>
                              <cl:helpTD/>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateRange" value="${message(code:"collection.button.update")}" class="save btn"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
