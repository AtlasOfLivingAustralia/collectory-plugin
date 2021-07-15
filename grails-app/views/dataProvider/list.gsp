<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${entityType}" />
        <g:set var="entityNameLower" value="${cl.controller(type: entityType)}"/>
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="searchForOrganizations"> ${message(code: 'dataProvider.gbif.import.label')}</g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="alert alert-warning">${flash.message}</div>
            </g:if>

            <div class="list">
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <g:sortableColumn property="name" title="${message(code: 'dataProvider.name.label', default: 'Name')}" />

                            <g:sortableColumn property="uid" title="${message(code: 'providerGroup.uid.label', default: 'UID')}" />

                            <th style="text-align:center;">${message(code: 'dataProvider.resources.label', default: 'No. resources')}</th>

                            <g:if test="${grailsApplication.config.gbifRegistrationEnabled == 'true'}">
                                <th style="text-align:center;">${message(code: 'dataProvider.gbif.label', default: 'GBIF')}</th>
                            </g:if>

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${instanceList}" status="i" var="instance">
                      <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link controller="dataProvider" action="show" id="${instance.uid}">${fieldValue(bean: instance, field: "name")}</g:link></td>

                        <td>${fieldValue(bean: instance, field: "uid")}</td>

                        <td style="text-align:center;">${instance.resources.size()}</td>

                        <g:if test="${grailsApplication.config.gbifRegistrationEnabled == 'true'}">

                            <td class="text-nowrap">
                                <g:if test="${fieldValue(bean: instance, field: "gbifRegistryKey")}">
                                    <g:link class="btn btn-default" controller="dataProvider" action="updateGBIF" id="${instance.uid}"
                                            onclick="return confirm('${message(code: 'default.button.update.provider.confirm.message', default: 'Are you sure you want to update this provider?')}');">
                                        ${message(code: 'dataProvider.gbif.update', default: 'Update')}
                                    </g:link> |
                                    <a href="https://gbif.org/publisher/${instance.gbifRegistryKey}">
                                        ${message(code: 'dataProvider.gbif.show', default: 'Show')}
                                    </a>
                                </g:if>
                                <g:else>
                                    <g:link class="btn btn-default" controller="dataProvider" action="registerGBIF" id="${instance.uid}"
                                            onclick="return confirm('${message(code: 'default.button.register.provider.confirm.message', default: 'Are you sure you want to register this provider?')}');">
                                        ${message(code: 'dataProvider.gbif.register', default: 'Register')}
                                    </g:link>
                                </g:else>
                            </td>

                        </g:if>

                      </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>

            <div class="nav">
                <tb:paginate controller="dataProvider" action="list" total="${instanceTotal}" />
            </div>
        </div>
    </body>
</html>
