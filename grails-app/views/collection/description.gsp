<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="collection.base.label" default="Edit collection metadata" /></title>
    </head>
    <body id="body-wrapper">
        <div class="nav">
          <g:if test="${mode == 'create'}">
            <h1><g:message code="collection.des.title" /></h1>
          </g:if>
          <g:else>
            <h1><g:message code="collection.title.editing" />: ${command.name}</h1>
          </g:else>
        </div>
        <tr id="baseForm" >
            <g:if test="${message}">
            <div class="message">${message}</div>
            </g:if>
            <g:hasErrors bean="${command}">
            <div class="errors">
                <g:renderErrors bean="${command}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" name="baseForm" action="base">
                <g:hiddenField name="id" value="${command?.id}"/>
                <g:hiddenField name="version" value="${command.version}"/>
                <!-- public description -->
                <div class="form-group">
                    <label for="pubDescription"><g:message code="providerGroup.pubDescription.label"
                                                           default="Public Description"/><cl:helpText
                            code="collection.pubDescription"/></label>
                    <g:textArea name="pubDescription" class="form-control"
                                rows="${cl.textAreaHeight(text: command.pubDescription)}"
                                value="${command.pubDescription}"/>
                </div>

                <!-- tech description -->
                <div class="form-group">
                    <label for="techDescription"><g:message code="providerGroup.techDescription.label"
                                                            default="Technical Description"/><cl:helpText
                            code="collection.techDescription"/></label>
                    <g:textArea name="techDescription" class="form-control"
                                rows="${cl.textAreaHeight(text: command.techDescription)}"
                                value="${command?.techDescription}"/>
                </div>
                <!-- focus -->
                <div class="form-group">
                    <label for="focus"><g:message code="providerGroup.focus.label" default="Focus"/><cl:helpText
                            code="collection.focus"/></label>
                    <g:textArea name="focus" class="form-control" rows="${cl.textAreaHeight(text: command.focus)}"
                                value="${command?.focus}"/>
                </div>
                <!-- type -->
                <div class="form-group">
                    <label for="collectionType"><g:message code="collection.collectionType.label"
                                                           default="Collection Type"/><cl:helpText
                            code="collection.collectionType"/></label>
                    <cl:checkboxSelect name="collectionType" from="${command.collectionTypes}"
                                       value="${command.listCollectionTypes()}" multiple="yes"
                                       valueMessagePrefix="collection.collectionType" noSelection="['': '']"/>
                </div>

                <!-- growth status -->
                <div class="form-group">
                    <label for="active"><g:message code="providerGroup.sources.active.label"
                                                   default="Status"/><cl:helpText code="collection.active"/></label>
                    <g:select name="active" from="${command.constraints.active.inList}" value="${command?.active}"
                              valueMessagePrefix="infoSource.active" noSelection="['': '']"/>
                </div>

                <!-- start date -->
                <div class="form-group">
                    <label for="startDate"><g:message code="collection.des.startdate"/><cl:helpText
                            code="collection.startDate"/></label>
                    <g:textField name="startDate" maxlength="45" value="${command?.startDate}"/>

                </div>

                <!-- end date -->
                <div class="form-group">

                    <label for="endDate"><g:message code="collection.des.enddate"/><cl:helpText
                            code="collection.endDate"/></label>
                    <g:textField name="endDate" maxlength="45" value="${command?.endDate}"/>
                </div>

                <!-- keywords -->
                <div class="form-group">
                    <label for="keywords"><g:message code="collection.keywords.label" default="Keywords"/><cl:helpText
                            code="collection.keywords"/></label>
                    <g:textField name="keywords" value="${command?.listKeywords().join(',')}"/>
                </div>

                <!-- sub-collections -->
                <div class="form-group">
                    <label for="subCollections"><g:message code="scope.subCollections.label"
                                                           default="Sub-collections"/><cl:helpText
                            code="scope.subCollections"/></label>
                    <p><g:message code="collection.des.des01"/>.</p>
                    <table><colgroup><col width="50%"/><col width="50%"/></colgroup>
                        <tr><g:message code="collection.des.de02"/><g:message code="collection.des.des03"/></tr>
                        <g:set var="subcollections" value="${command.listSubCollections()}"/>
                        <g:each var="sub" in="${subcollections}" status="i">
                            <tr>
                                <g:textField name="name_${i}" value="${sub.name.encodeAsHTML()}"/>
                                <g:textField name="description_${i}" value="${sub.description.encodeAsHTML()}"/>
                            </tr>
                        </g:each>
                        <g:set var="j" value="${subcollections.size()}"/>
                        <g:each var="i" in="${[j, j + 1, j + 2]}">
                            <tr>
                                <g:textField name="name_${i}" value=""/>
                                <g:textField name="description_${i}" value=""/>
                            </tr>
                        </g:each>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" class="save btn btn-success"
                                                name="_action_updateDescription"
                                                value="${message(code: "collection.button.update")}"></span>
                    <span class="button"><input type="submit" class="cancel btn btn-default" name="_action_cancel"
                                                value="${message(code: "collection.button.cancel")}"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
