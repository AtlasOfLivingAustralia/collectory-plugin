<%@ page import="au.org.ala.collectory.Collection;" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="collection.base.label" args="[command.ENTITY_TYPE]" default="Edit collection metadata" /></title>
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
                        <div><h3><g:message code="collection.range.title" /></h3></div>
                        <div class="form-group">
                                <label for="geographicDescription"><g:message code="collection.range.label01" /><cl:helpText code="collection.geographicDescription"/></label>
                                <g:textField name="geographicDescription" class="form-control" value="${command?.geographicDescription}" />
                         </div>

                        <div class="form-group">
                              <label for="states"><g:message code="collection.range.label02" /><cl:helpText code="collection.states"/></label>
                                <g:textField name="states" class="form-control" value="${command?.states}" />
                        </div>

                        <div class="form-group">
                          <g:message code="collection.range.label03" />.
                        </div>

                        <div class="form-group">
                               <label for="eastCoordinate"><g:message code="collection.range.label04" /><cl:helpText code="collection.eastCoordinate"/></label>
                              <g:field type="number" class="form-control" name="eastCoordinate" min="-180.0" max="180.0" step="any" value="${cl.showDecimal(value: command.eastCoordinate)}" />
                        </div>

                        <div class="form-group">
                              <label for="westCoordinate"><g:message code="collection.range.label05" /><cl:helpText code="collection.westCoordinate"/></label>
                              <g:field type="number" class="form-control" name="westCoordinate" min="-180.0" max="180.0" step="any"  value="${cl.showDecimal(value: command.westCoordinate)}" />
                         </div>

                        <div class="form-group">
                              <label for="northCoordinate"><g:message code="collection.range.label06" /><cl:helpText code="collection.northCoordinate"/></label>
                                <g:field type="number" class="form-control" name="northCoordinate" min="-90.0" max="90.0" step="any" value="${cl.showDecimal(value: command.northCoordinate)}" />
                        </div>

                        <div class="form-group">
                              <label for="southCoordinate"><g:message code="collection.range.label07" /><cl:helpText code="collection.southCoordinate"/></label>
                              <g:field type="number" class="form-control" name="southCoordinate" min="-90.0" max="90.0" step="any" value="${cl.showDecimal(value: command.southCoordinate)}" />
                        </div>

                        <!-- taxonomic range -->
                        <div><h3><g:message code="collection.range.title02" /></h3></div>
                        <div class="form-group">
                              <label for="kingdomCoverage"><g:message code="collection.range.label08" /><cl:helpText code="collection.kingdomCoverage"/></label>
                                <cl:checkBoxList name="kingdomCoverage" from="${Collection.kingdoms}" value="${command?.kingdomCoverage}" />
                        </div>

                        <div class="form-group">
                              <label for="scientificNames"><g:message code="collection.range.label09" /><cl:helpText code="collection.scientificNames"/></label>
                               <g:textArea name="scientificNames" class="form-control" value="${command.listScientificNames().join(',')}"/>
                        </div>

                        <!-- stats -->
                        <tr><h3><g:message code="collection.range.title03" /></h3></div>
                        <div class="form-group">
                              <label for="numRecords"><g:message code="collection.numRecords.label" default="Number of specimens" /><cl:helpText code="collection.numRecords"/></label>
                                <g:field type="number" name="numRecords" class="form-control" value="${cl.showNumber(value: command.numRecords)}" />
                        </div>

                        <div class="form-group">
                              <label for="numRecordsDigitised"><g:message code="collection.numRecordsDigitised.label" default="Number of records digitised" /><cl:helpText code="collection.numRecordsDigitised"/></label>
                            
                            
                                <g:field type="number" name="numRecordsDigitised" class="form-control" value="${cl.showNumber(value: command.numRecordsDigitised)}" />
                        </div>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateRange" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
