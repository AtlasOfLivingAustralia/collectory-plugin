<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="collection.base.label" default="Edit collection metadata" /></title>
    </head>
    <body>

        <div class="nav">
          <g:if test="${mode == 'create'}">
            <h1><g:message code="collection.des.title" /></h1>
          </g:if>
          <g:else>
            <h1><g:message code="collection.title.editing" />: ${command.name}</h1>
          </g:else>
        </div>
            <g:if test="${message}">
                <div class="message alert alert-warning">${message}</div>
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
                    <label for="startDate"><g:message code="manage.show.temp.startdate"/><cl:helpText
                            code="collection.startDate"/></label>
                    <g:textField name="startDate" maxlength="45" value="${command?.startDate}"/>

                </div>

                <!-- end date -->
                <div class="form-group">

                    <label for="endDate"><g:message code="manage.show.temp.enddate"/><cl:helpText
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
                <div id="subcollections-editor" class="well">
                    <h2>
                        <g:message code="scope.subCollections.label" default="Sub-collections"/>
                        <cl:helpText code="scope.subCollections"/>
                    </h2>

                    <ul id="subcollections" style="list-style-type: none; padding-left:0px;" >
                        <g:set var="subcollections" value="${command.listSubCollections()}"/>
                        <g:each var="sub" in="${subcollections}" status="i">
                            <li id="subcollection_${i}">
                                <label for="name_${i}">Name</label>
                                <g:textField name="name_${i}" class="subcollection_name form-control" value="${sub.name.encodeAsHTML()}"/>

                                <label for="description_${i}">Description</label>
                                <g:textArea name="description_${i}" class="subcollection_description form-control" value="${sub.description.encodeAsHTML()}"/>
                                <br/>
                                <button class="btn btn-default deleteSubcollection">Delete</button>
                                <hr/>
                            </li>
                        </g:each>
                    </ul>

                    <button id="addSubcollection" class="btn btn-default">Add new subcollection</button>

                    <!-- template -->
                    <li id="subcollection_template" class="hide">
                        <label for="name_">Name</label>
                        <g:textField  name="name_" class="subcollection_name form-control" value=""/>
                        <label for="description_">Description</label>
                        <g:textArea name="description_" class="subcollection_description form-control" value=""/>
                        <br/>
                        <button class="btn btn-default deleteSubcollection">Delete</button>
                        <hr/>
                    </li>

                </div>

                <div class="buttons">
                    <input type="submit" class="save btn btn-success"
                                                name="_action_updateDescription"
                                                value="${message(code: "collection.button.update")}">
                    <input type="submit" class="cancel btn btn-default" name="_action_cancel"
                                                value="${message(code: "collection.button.cancel")}">
                </div>
            </g:form>
        </div>

    <script>

        $('.deleteSubcollection').click(function(event){
            event.preventDefault();
            $(event.target).parent().remove();
        });

        $('#addSubcollection').click(function(event){

            event.preventDefault();
            var $subcollection = $('#subcollection_template').clone();
            $subcollection.removeClass('hide');
            var currentSubcollectionSize = $('#subcollections li').length;

            $subcollection.attr('id','subcollection_'+ currentSubcollectionSize);
            $subcollection.find('.subcollection_name').attr('name', 'name_'+ currentSubcollectionSize);
            $subcollection.find('.subcollection_description').attr('name', 'description_' + currentSubcollectionSize);
            $subcollection.find('.deleteSubcollection').click(function(event){
                event.preventDefault();
                $(event.target).parent().remove();
            });

            $subcollection.appendTo('#subcollections');
        });

    </script>


    </body>
</html>
