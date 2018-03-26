<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
        <g:set var="entityNameLower" value="${command.ENTITY_TYPE.toLowerCase()}"/>
        <title><g:message code="${entityNameLower}.base.label" default="Edit ${entityNameLower} metadata" /></title>
    </head>
    <body>
        <div class="nav">
          <h1>Edit ${cf.contact?.buildName()} for ${command.name}</h1>
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
            <g:form method="post" name="baseForm" action="base">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <g:hiddenField name="contactForId" value="${cf.id}" />
                <g:hiddenField name="returnTo" value="${returnTo}" />
                <div class="dialog">
                    <div class="form-group">
                        <label for="role" class="col-sm-2 control-label"><g:message code="shared.cr.table0201" /></label>
                        <div class="col-sm-10">
                            <g:textField name="role" value="${cf?.role}" class="form-control" />
                        </div>
                    </div>
                    <div class="checkbox">
                        <label>
                            <g:checkBox  name="administrator" value="${cf?.administrator}"/>
                            <g:message code="shared.cr.table0301" /><br/>
                            <g:message code="shared.cr.table0302" /> ${entityNameLower}
                        </label>
                    </div>

                    <div class="form-group">
                        <div class="checkbox">
                            <label>
                                <g:checkBox name="primaryContact" value="${cf?.primaryContact}"/>
                                <g:message code="shared.cr.table0501" /><br/>
                                <span class="smaller">
                                    <g:message code="shared.cr.table0502" /> ${entityNameLower}
                                </span>
                            </label>
                        </div>
                    </div>
                    <div class="buttons">
                        <input class="btn btn-primary" type="submit" name="_action_updateContactRole" value="Update" class="save"/>
                        <input class="btn btn-default" type="submit" name="_action_cancel" value="Cancel" class="cancel"/>
                    </div>
            </g:form>
        </div>
    </body>
</html>
