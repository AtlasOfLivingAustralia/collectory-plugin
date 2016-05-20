<g:form action="save" method="post" >
    <g:hiddenField name="returnTo" value="${returnTo}" />
    <g:hiddenField name="id" value="${providerMapInstance?.id}" />
    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name">
                    <label for="institution"><g:message code="providerMap.institution.label" default="Institution" /></label>
                </td>
                <td valign="top" class="value ${hasErrors(bean: providerMapInstance, field: 'providerGroup', 'errors')}">
                    <g:select id="institutionSelect" name="institution.id" class="input-xxlarge"
                              from="${au.org.ala.collectory.Institution.list([sort: 'name'])}"
                              optionKey="id"
                              value="${providerMapInstance?.institution?.id}"
                              noSelection="${['null':'---- select an institution -----']}"
                    />
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">
                    <label for="collection"><g:message code="providerMap.collection.label" default="Collection" /></label>
                </td>
                <td valign="top" class="value ${hasErrors(bean: providerMapInstance, field: 'providerGroup', 'errors')}">
                    <g:select id="collectionSelect" name="collection.id" class="input-xxlarge"
                              from="${au.org.ala.collectory.Collection.list([sort: 'name'])}"
                              optionKey="id"
                              value="${providerMapInstance?.collection?.id}"
                              noSelection="${['null':'---- select an collection -----']}"
                    />
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">
                    <label for="institutionCodes"><g:message code="providerMap.institutionCodes.label" default="Institution codes" /></label>
                </td>
                <td valign="top" class="value ${hasErrors(bean: providerMapInstance, field: 'institutionCodes', 'errors')}">
                    <g:select name="institutionCodes"
                              from="${au.org.ala.collectory.ProviderCode.list([sort:'code'])}"
                              multiple="yes" optionKey="id" size="5"
                              value="${providerMapInstance?.institutionCodes*.id}"
                    />
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">
                    <label for="collectionCodes"><g:message code="providerMap.collectionCodes.label" default="Collection codes" /></label>
                </td>
                <td valign="top" class="value ${hasErrors(bean: providerMapInstance, field: 'collectionCodes', 'errors')}">
                    <g:select name="collectionCodes"
                              from="${au.org.ala.collectory.ProviderCode.list([sort:'code'])}"
                              multiple="yes" optionKey="id" size="5"
                              value="${providerMapInstance?.collectionCodes*.id}" />
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">
                    <label for="warning"><g:message code="providerMap.warning.label" default="Warning" /></label>
                </td>
                <td valign="top" class="value ${hasErrors(bean: providerMapInstance, field: 'warning', 'errors')}">
                    <g:textField name="warning" value="${providerMapInstance?.warning}" />
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name" colspan="2">
                    <label for="exact" class="checkbox">
                        <g:message code="providerMap.exact.label" default="Exact matches only" />
                        <g:checkBox name="exact" value="${providerMapInstance?.exact}" />
                    </label>
                </td>
            </tr>

            <tr class="prop">
                <td valign="top"  class="name" colspan="2">
                    <label for="matchAnyCollectionCode" class="checkbox"><g:message code="providerMap.matchAnyCollectionCode.label" default="Match Any Collection Code" />
                    <g:checkBox name="matchAnyCollectionCode" value="${providerMapInstance?.matchAnyCollectionCode}" />
                    </label>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <br/>
    <div class="buttons">
        <g:if test="${!providerMapInstance.dateCreated}">
            <span class="button"><g:submitButton name="create" class="save btn" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
        </g:if>
        <g:else>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save btn" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
            <span class="button"><g:actionSubmit class="delete btn" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
        </div>
        </g:else>

    </div>
</g:form>
<r:script>

$('#institutionSelect').change(function() {

    console.log("Institution val: " + $('#institutionSelect').val());

    var selectedInst =  $('#institutionSelect').val();

    var collectionListURL = "${grailsApplication.config.grails.serverURL}/ws/institution/" + $('#institutionSelect').val();

    if(selectedInst == 'null'){
        collectionListURL = "${grailsApplication.config.grails.serverURL}/ws/collection/";

        $.get(collectionListURL, function( data ) {
            $('#collectionSelect').empty();
            $('#collectionSelect')
                .append($("<option></option>")
                .attr("value", 'null')
                .text('---- select a collection ----'));
            $.each( data, function( key, collection ) {
                console.log(collection);
                $('#collectionSelect')
                    .append($("<option></option>")
                    .attr("value", collection.id)
                    .text(collection.name));
            });
        });

    } else {

        $.get(collectionListURL, function( data ) {
            $('#collectionSelect').empty();
            $('#collectionSelect')
                .append($("<option></option>")
                .attr("value", 'null')
                .text('---- select a collection ----'));

            $.each( data.collections, function( key, collection ) {
                console.log(collection);
                $('#collectionSelect')
                    .append($("<option></option>")
                    .attr("value", collection.id)
                    .text(collection.name));
            });
        });
    }
});

</r:script>