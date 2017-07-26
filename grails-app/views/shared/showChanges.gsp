<%@ page import="au.org.ala.collectory.ContactFor; au.org.ala.collectory.ProviderGroup" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
  <g:set var="entityName" value="${instance.ENTITY_TYPE}"/>
  <g:set var="entityNameLower" value="${instance.ENTITY_TYPE.toLowerCase()}"/>
  <title><g:message code="shared.sc.title" args="[entityNameLower]" /></title>
</head>
<div class="nav">
  <h1><g:message code="shared.sc.title01" /> ${fieldValue(bean: instance, field: "name")}</h1>
</div>
<div class="body">
  <g:if test="${message}">
    <div class="message">${message}</div>
  </g:if>
  <g:hasErrors bean="${instance}">
    <div class="errors">
      <g:renderErrors bean="${instance}" as="list"/>
    </div>
  </g:hasErrors>
    <div class="dialog">
      <p><g:message code="shared.sc.des01" />.</p>
      <g:each in="${changes}" var="ch">
        <div>
          <g:if test="${ch.eventName == 'UPDATE'}">
            <p class="relatedFollows"><g:message code="shared.sc.des02" args="[ch.lastUpdated, ch.actor, ch.propertyName]" /></p>
            <table class="textChanges table table-striped table-bordered">
              <tr>
                <td><g:message code="shared.sc.table01.cell0101" />:</td><td><cl:cleanString class="changeTo" value="${ch.newValue}" field="${ch.propertyName}"/></td>
              </tr><tr>
                <td><g:message code="shared.sc.table01.cell0102" />:</td><td><cl:cleanString class="changeFrom" value="${ch.oldValue}" field="${ch.propertyName}"/></td>
              </tr>
            </table>
          </g:if>
          <g:elseif test="${ch.eventName == 'INSERT' && cl.shortClassName(className:ch.className) == 'ContactFor'}">
            <g:set var="cf" value="${ContactFor.get(ch.persistedObjectId)}"/>
            <p class="relatedFollows"><g:message code="shared.sc.des03" args="[ch.lastUpdated, ch.actor]" /></p>
            <table class="textChanges table table-striped table-bordered">
              <tr>
                <td><g:message code="shared.sc.table02.cell0101" />:${ch.persistedObjectId}</td><td>${cf ? cf.contact?.buildName() : 'name missing - may have been deleted'}</td>
              </tr>
            </table>
          </g:elseif>
          <g:elseif test="${ch.eventName == 'DELETE' && cl.shortClassName(className:ch.className) == 'ContactFor'}">
            <p class="relatedFollows"><g:message code="shared.sc.des04" args="[ch.lastUpdated, ch.actor]" /></p>
            <table class="textChanges table table-striped table-bordered">
              <tr>
                <td><g:message code="shared.sc.table03.cell0101" />:${ch.persistedObjectId}</td><td><g:message code="shared.sc.table03.cell0102" /></td>
              </tr>
            </table>
          </g:elseif>
          <g:elseif test="${ch.eventName == 'INSERT' && ch.uri == instance.uid}">
            <p class="relatedFollows"><g:message code="shared.sc.des05" args="[ch.lastUpdated, ch.actor]" /> ${entityNameLower}.</p>
            <table class="textChanges table table-striped table-bordered">
              <tr>
                <td colspan="2">${instance.name}</td>
              </tr>
            </table>
          </g:elseif>
        </div>
      </g:each>
    </div>
    <div class="buttons">
      <g:form>
        <g:hiddenField name="id" value="${instance.id}"/>
        <span class="button"><g:link class="returnAction btn btn-default" controller="${instance.urlForm()}" action='show' id="${instance.uid}">Return to ${instance.name}</g:link></span>
      </g:form>
    </div>
</div>

</body>
</html>
