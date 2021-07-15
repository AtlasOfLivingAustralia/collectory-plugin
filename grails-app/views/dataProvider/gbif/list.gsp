<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${entityType}" />
        <title>${message(code: 'dataProvider.gbif.import.longLabel')}</title>
        <style>
            .highlightedRow {
                background-color: #f5e79e !important;
            }
        </style>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1>${message(code: 'dataProvider.gbif.import.longLabel')}</h1>
            <g:if test="${flash.message}">
                <div class="alert alert-warning">${flash.message}</div>
            </g:if>
            <div class="list">
                <div class="row">
                    <div class="well pull-left col-md-6">
                        <p>
                            ${message(code: 'dataProvider.gbif.import.desc1')}
                        </p>
                        <p>
                            ${message(code: 'dataProvider.gbif.import.desc2')}<br/>
                            ${message(code: 'dataProvider.gbif.import.desc3')}<br/>
                            - ${message(code: 'dataProvider.gbif.import.desc4')}<br/>
                            - ${message(code: 'dataProvider.gbif.import.desc5')}
                        </p>
                    </div>
                    <div class="col-md-6">
                        <g:form action="searchForOrganizations" controller="dataProvider">
                            <div class="form-group">
                                <label for="country">${message(code: 'dataProvider.gbif.import.country')}</label>
                                <g:select name="country" class="form-control" from="${countryMap.entrySet()}" optionKey="key" optionValue="value" value="${country}"
                                          onchange="submit()"
                                          noSelection="${['NO_VALUE':message(code:'dataProvider.gbif.import.country.noselection')]}"/>
                            </div>
                        </g:form>
                    </div>
                    <g:if test="${organizations}">
                        <div class="col-md-6">
                            <g:form action="importAllFromOrganizations" controller="dataProvider">
                                <g:hiddenField name="country" value="${country}"/>
                                <g:submitButton name="importAll" value="${message(code: 'dataProvider.gbif.import.action.importAll')}" onclick="return confirm('${message(code: 'dataProvider.gbif.import.action.import.confirmation')}');" class="pull-right"/>
                            </g:form>
                        </div>
                    </g:if>
                </div>
                <g:if test="${organizations}">
                    <table class="table table-bordered table-striped">
                        <thead>
                        <tr>
                            <td>${message(code: 'dataProvider.gbif.import.header.gbifuid')}</td>
                            <td>${message(code: 'dataProvider.gbif.import.header.title')}</td>
                            <td>${message(code: 'dataProvider.gbif.import.header.uid')}</td>
                            <td>${message(code: 'dataProvider.gbif.import.header.action')}</td>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${organizations}" status="i" var="organization">
                            <g:form action="importFromOrganization" controller="dataProvider">
                                <g:hiddenField name="organizationKey" value="${organization.key}"/>
                                <g:hiddenField name="country" value="${country}"/>
                                <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${organization.lastCreated ? 'highlightedRow' : ''}">
                                    <td><a href="https://www.gbif.org/publisher/${organization.key}" target="_blank">${organization.key}</a></td>
                                    <td>${organization.title}</td>
                                    <td><g:link action="show" id="${organization.uid}">${organization.uid}</g:link></td>
                                    <td><g:if test="${organization.statusAvailable}"><g:submitButton name="import" value="${message(code: 'dataProvider.gbif.import.action.import')}" /></g:if></td>
                                </tr>
                            </g:form>
                        </g:each>
                        </tbody>
                    </table>
                </g:if>
            </div>
        </div>
    </body>
</html>
