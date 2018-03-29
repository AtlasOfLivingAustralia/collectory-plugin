<%@ page import="au.org.ala.collectory.Contact; au.org.ala.collectory.ContactFor" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
  <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
  <g:set var="entityNameLower" value="${command.ENTITY_TYPE.toLowerCase()}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>
<body>

    <div class="btn-toolbar">
        <ul class="btn-group">
            <li class="btn btn-default"><cl:homeLink/></li>
            <li class="btn btn-default">
                    <g:link class="returnAction" controller="${command.urlForm()}" action='show' id="${command.id}">Return to ${command.name}</g:link>
            </li>
        </ul>
    </div>

  <div class="nav">
    <h1><g:message code="shared.title.editing" />: ${command.name} - <g:message code="shared.scontact.title01" /></h1>
  </div>
  <div class="body">
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="currentContacts row">
      <g:each var="cf" in="${command.getContacts()}" status="contactStatus">
        <div class="col-md-3" style="padding-left:0px;">
            <div class="panel panel-default">
                <div class="panel-heading"><span class="contactName">${cf.contact.buildName()}</span></div>
                <div class="panel-body">
                    <!-- details -->
                    <div>
                        <ul class="detailList">
                          <cl:valueOrOtherwise value="${cf.contact.email}"><li>${cf.contact.email}</li></cl:valueOrOtherwise>
                          <cl:valueOrOtherwise value="${cf.contact.phone}"><li>Ph: ${cf.contact.phone}</li></cl:valueOrOtherwise>
                          <cl:valueOrOtherwise value="${cf.contact.mobile}"><li>Mob: ${cf.contact.mobile}</li></cl:valueOrOtherwise>
                          <cl:valueOrOtherwise value="${cf.contact.fax}"><li>Fax: ${cf.contact.fax}</li></cl:valueOrOtherwise>
                        </ul>
                    </div>

                    <ul class="detailList">
                      <li><cl:valueOrOtherwise value="${cf.role}" otherwise="No role defined"><g:message code="shared.scontact.li01" /> ${cf.role}</cl:valueOrOtherwise></li>
                      <li><cl:valueOrOtherwise value="${cf.administrator}" otherwise="Not allowed to edit"><img src="${resource(dir:'images/ala', file:'olive-tick.png')}"/><g:message code="shared.scontact.li02" /></cl:valueOrOtherwise></li>
                      <cl:valueOrOtherwise value="${cf.primaryContact}"><li><img src="${resource(dir:'images/ala', file:'olive-tick.png')}"/><g:message code="shared.scontact.li04" /></li></cl:valueOrOtherwise>
                    </ul>

                    <span class="contactButton buttonRight">
                      <g:link class="edit-small btn btn-default" action='editRole' id="${cf.id}"
                              params='[returnTo: "/${command.urlForm()}/edit/${command.id}?page=/shared/showContacts"]'>
                        <g:message code="shared.scontact.link.edit" /> ${entityNameLower}
                      </g:link>
                    </span>
                    <span class="contactButton buttonRight">
                      <g:link class="edit-small btn btn-default" controller="contact" action='edit' id="${cf.contact.id}"
                              params='[returnTo: "/${command.urlForm()}/edit/${command.id}?page=/shared/showContacts"]'>
                        ${message(code: 'default.button.editContact.label', default: "Edit the contact's details")}
                      </g:link>
                    </span>
                    <span class="contactButton">
                      <g:link class="removeSmallAction btn btn-danger" action='removeContact' id="${command.id}" onclick="return confirm('Remove ${cf.contact?.buildName()} as a contact for this ${entityNameLower}?');"
                              params='[idToRemove: "${cf.id}"]'><g:message code="shared.scontact.link.remove" /> ${entityNameLower}</g:link>
                    </span>
                </div>
            </div>
        </div>
      </g:each>
    </div>
    <div>

    <div class="addContacts row">
      <!-- add existing contact -->
      <h2><g:message code="shared.scontact.title02" /></h2>
      <div class="show-section">
        <g:form action="addContact" id="${command.id}">
          <table class="shy">
            <colgroup><col width="68%"><col width="32%"></colgroup>
            <tr><td colspan="2"><g:message code="shared.scontact01.cell0101" /></td></tr>
            <tr><td>
              <g:select name="addContact" from="${Contact.listOrderByLastName()}" optionKey="id" noSelection="${['null':'Select one to add']}" />
            </td><td>
              <input type="submit" onclick="return anySelected('addContact','You must select a contact to add.');" class="addAction btn btn-default" value="Add existing contact"/>
            </td></tr>
          </table>
        </g:form>
        OR:<br/>
        <table class="shy">
          <colgroup><col width="68%"><col width="32%"></colgroup>
          <tr><td><g:message code="shared.scontact02.cell0101" /> ${entityNameLower}:</td>
          <td>
          <span class="button">
            <g:link class="addAction btn btn-default" controller="contact" action='create' params='[returnTo:"/${command.urlForm()}/addNewContact/${command.id}"]' id="${command.id}">${message(code: 'default.button.addContact.label', default: 'Add new contact')}</g:link>
          </span>
          </td></tr>
        </table>
      </div>
    </div>

  </div>
</body>
</html>
