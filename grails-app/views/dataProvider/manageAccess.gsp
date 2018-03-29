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
            <g:link class="returnAction" controller="dataProvider" action='show' id="${instance.id}">Return to ${instance.name}</g:link>
        </li>
    </ul>
</div>
<h1>Manage approved list for <g:link controller="dataProvider" action="show" id="${instance.id}"> ${instance.name}</g:link></h1>

<div class=" well">
    <div class="form-group">
        <label for="q" >Find registered user by name or email address</label>
        <input type="text" name="q" class="form-control" id="q" placeholder="">
    </div>
    <button class="btn btn-primary" type="button" id="searchForUser">Search</button>
    <button class="btn btn-info" type="button" id="viewApproved">View approved users</button>
</div>

<div class="list">
    <table id="searchResults" class="table table-striped table-bordered hide">
        <thead>
            <tr>
                <th>${message(code: 'contact.id.label', default: 'Id')}</th>
                <th>${message(code: 'contact.firstName.label', default: 'First Name')}</th>
                <th>${message(code: 'contact.lastName.label', default: 'Last Name')}</th>
                <th>${message(code: 'contact.email.label', default: 'Email')}</th>
                <th>${message(code: 'contact.access.label', default: 'Access')}</th>
            </tr>
        </thead>
        <tbody>

        </tbody>
    </table>
</div>

<!-- results template for search results -->
<div class="hide">
    <table>
        <tr class="resultTemplate" >
            <td class="userId"></td>
            <td class="firstName"></td>
            <td class="lastName"></td>
            <td class="email"></td>
            <td class="actions">
                <button class="addUser btn btn-primary hide">Grant access</button>
                <button class="removeUser btn btn-danger hide">Revoke access</button>
                <button class="specifyResources btn btn-primary hide">Specify resources</button>
            </td>
        </tr>
    </table>
</div>

<script>

    var queryBaseUrl = "${g.createLink(controller: 'dataProvider', action: 'findUser', id: instance.id)}?q=";
    var findApprovedUsers = "${g.createLink(controller: 'dataProvider', action: 'findApprovedUsers', id: instance.id)}";
    var addUserUrl = "${g.createLink(controller: 'dataProvider', action: 'addUserToApprovedList', id: instance.id)}";
    var removeUserUrl = "${g.createLink(controller: 'dataProvider', action: 'removeUserToApprovedList', id: instance.id)}";
    var specifyResourcesUrl = "${g.createLink(controller: 'dataProvider', action: 'specifyAccess', id: instance.id)}";

    $('#searchForUser').click(function() {
        var queryUrl = queryBaseUrl + $('#q').val();
        $.getJSON(queryUrl, function(data){
            renderResults(data.results, false);
        });
    });

    $('#viewApproved').click(function() {
        listApproved();
    });

    function listApproved(){
        $.getJSON(findApprovedUsers, function(data){
            renderResults(data, true);
        });
    }

    function renderResults(results, allApproved){

        $("#searchResults").removeClass("hide");
        $('#searchResults tbody').html('');

        $.each(results, function(index, returnedResult){
            var template = $('.resultTemplate').clone();
            template.removeClass("resultTemplate");
            template.attr("id", returnedResult.userId);
            template.find('.userId').html(returnedResult.userId);
            template.find('.email').html(returnedResult.email);
            template.find('.firstName').html(returnedResult.firstName);
            template.find('.lastName').html(returnedResult.lastName);
            template.find('.addUser').attr('id', 'add-' + returnedResult.userId);
            template.find('.removeUser').attr('id', 'def-' + returnedResult.userId);
            template.find('.specifyResources').attr('id', 'spc-' + returnedResult.userId);

            if(allApproved || returnedResult.hasAccess){
                template.find('.removeUser').removeClass('hide');
                template.find('.specifyResources').removeClass('hide');
            } else {
                template.find('.addUser').removeClass('hide');
            }

            template.removeClass('hide');
            template.appendTo('#searchResults');
        });


        $('.addUser').click(function(event) {

            var userId = event.target.id.substring(4);

            var data = {
                userId : userId,
                email : $('#' + userId).find(".email").html(),
                firstName: $('#' + userId).find(".firstName").html(),
                lastName: $('#' + userId).find(".lastName").html()
            }
            $.post( addUserUrl, data)
                .done(function() {
                    $('#' + userId).find('.removeUser').removeClass('hide');
                    $('#' + userId).find('.specifyResources').removeClass('hide');
                    $('#' + userId).find('.addUser').addClass('hide');
                })
                .fail(function() {
                    alert( "There was a problem changing access." );
                });
        });

        $('.removeUser').click(function(event) {

            var userId = event.target.id.substring(4);

            var data = {
                userId : userId,
                email : $('#' + userId).find(".email").html(),
                firstName: $('#' + userId).find(".firstName").html(),
                lastName: $('#' + userId).find(".lastName").html()
            }
            $.post( removeUserUrl, data)
                .done(function() {
                    $('#' + userId).find('.removeUser').addClass('hide');
                    $('#' + userId).find('.specifyResources').addClass('hide');
                    $('#' + userId).find('.addUser').removeClass('hide');
                })
                .fail(function() {
                    alert( "There was a problem changing access." );
                });
        });

        $('.specifyResources').click(function(event) {
            var userId = event.target.id.substring(4);
            window.location.href = specifyResourcesUrl + "?userId=" + userId;
        });
    }

    listApproved();

</script>
</body>


</html>