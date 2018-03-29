<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${instance.ENTITY_TYPE}" />
        <g:set var="entityNameLower" value="${cl.controller(type: instance.ENTITY_TYPE)}"/>
        <title>${instance.name} | <g:message code="default.show.label" args="[entityName]" /></title>
    </head>
<body>

<div class="btn-toolbar">
    <ul class="btn-group">
        <li class="btn btn-default"><cl:homeLink/></li>
        <li class="btn btn-default">
            <g:link class="returnAction" controller="dataProvider" action='manageAccess' id="${instance.id}">Return to managing access for ${instance.name}</g:link>
        </li>
    </ul>
</div>

<h1>Specify resources
    <g:link controller="dataProvider" action="show" id="${instance.id}"> ${instance.name}</g:link>
    for
    ${contact.email}
</h1>

<p>
  Specify the resources provided by ${instance.name} has sensitive access to.
</p>
<p>
  Note: If you opt to specify resources, the access to newly added resources will need to managed here.
</p>

<div class="well">
    <g:form controller="dataProvider" action="updateSpecifiedAccess" id="${instance.id}" elementId="specifyForm">
        <div class="form-check">
            <input class="form-check-input"
                   type="checkbox"
                   id="allResources"
                   name="allResources"
                    <g:if test="${allApproved}">
                        checked="checked"
                    </g:if>
                   value="true"
            >
            <label class="form-check-label" for="allResources">
                Allow access to all resources from ${instance.name}
            </label>
        </div>

        <hr/>

        <input type="hidden" name="userId" value="${contact.userId}"/>
        <g:each in="${instance.resources.sort {it.name} }" var="dataResource">
        <div class="form-check">
            <input class="form-check-input specifyResource"
                   type="checkbox"
                   <g:if test="${!approvedAccessUids  || approvedAccessUids.contains(dataResource.uid) }">
                   checked="checked"
                   </g:if>
                   id="${dataResource.uid}"
                   name="approvedUIDs"
                   value="${dataResource.uid}"
            >
            <label class="form-check-label" for="${dataResource.uid}">
                ${dataResource.name}
            </label>
        </div>
        </g:each>
        <button type="submit" class="btn btn-primary">Update</button>
        <button type="submit" class="btn btn-default">Cancel</button>
    </g:form>
</div>
<script type="text/javascript">

    var allApproved = ${allApproved};

    $( document ).ready(function() {
        $('#allResources').change(function () {
            if (this.checked) {
                $('.specifyResource').attr("disabled", true);
            } else {
                $('.specifyResource').removeAttr("disabled");
            }
        });

        if(allApproved){
            $('.specifyResource').attr("disabled", true);
        }
    });

</script>
</body>




</html>