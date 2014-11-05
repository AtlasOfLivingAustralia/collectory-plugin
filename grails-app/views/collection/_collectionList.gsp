<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<div class="list">

    <g:if test="${collectionInstanceList}">
    <table class="table table-striped table-bordered">
      <colgroup><col width="40%"/><col width="10%"/><col width="50%"/></colgroup>
        <thead>
            <tr>
                <g:sortableColumn property="name" title="${message(code: 'collection.name.label', default: 'Name')}" params="${params}"/>
                <g:sortableColumn property="acronym" title="${message(code: 'collection.acronym.label', default: 'Acronym')}" params="${params}"/>
                <g:sortableColumn params="[sort: 'institution.name']" property="institution" title="${message(code: 'collection.institution.label', default: 'Institution')}" />
            </tr>
        </thead>
        <tbody>
        <g:each in="${collectionInstanceList}" status="i" var="collectionInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link controller="collection" action="show" id="${collectionInstance.uid}">${fieldValue(bean: collectionInstance, field: "name")}</g:link></td>
            <td>${fieldValue(bean: collectionInstance, field: "acronym")}</td>
            <td>${collectionInstance.institution?.name}</td>
          </tr>
        </g:each>
        </tbody>
    </table>
    </g:if>
    <g:else>
        <p class="lead"><g:message code="ale.show.cl.des" />.</p>
    </g:else>
</div>
