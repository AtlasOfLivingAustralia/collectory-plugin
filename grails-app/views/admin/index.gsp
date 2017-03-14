<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <title><g:message code="admin.index.title" /></title>
	<meta name="layout" content="${grailsApplication.config.skin.layout}" />

    </head>
    
    <body>
      <div class="floating-content">
    
          <div style="float:right;">
            <g:link class="mainLink" controller="public" action="map"><g:message code="admin.index.link01" /></g:link>
          </div>
          <div id="welcome">
              <h3><g:message code="admin.index.title01" /></h3> <p><g:message code="admin.index.title02" />.</p>
          </div>

          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>

          <cl:isNotLoggedIn>
            <div class="homeCell">
              <h4 class="inline"><g:message code="admin.notlogin.title" /></h4>
                <span class="buttons" style="float: right;">
                  <a href="${grailsApplication.config.security.cas.loginUrl}?service=${grailsApplication.config.grails.serverURL}/admin">&nbsp;<g:message code="admin.notlogin.link" />&nbsp;</a>
                </span>
              <p><g:message code="admin.notlogin.des" /></p>
            </div>
          </cl:isNotLoggedIn>

          <div class="homeCell">
            <g:link class="mainLink" controller="contact" action="showProfile"><g:message code="admin.index.link02" /></g:link>
            <p class="mainText"><g:message code="admin.index.des02" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="collection" action="list"><g:message code="admin.index.link03" /></g:link>
            <p class="mainText"><g:message code="admin.index.des03" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="collection" action="myList" id="68"><g:message code="admin.index.link04" /></g:link>
            <p class="mainText"><g:message code="admin.index.des04" />.</p>
          </div>

          <div class="homeCell">
            <span class="mainLink"><g:message code="admin.index.link05" /></span>
            <p class="mainText"><g:message code="admin.index.des05" /></p>
            <g:form action="search">
              <g:textField class="mainText" name="term"/><g:submitButton style="margin-left:20px;" name="search" value="Search"/>
            </g:form>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="collection" action="create"><g:message code="admin.index.link06" /></g:link>
            <p class="mainText"><g:message code="admin.index.des06" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="institution" action="list"><g:message code="admin.index.link07" /></g:link>
            <p class="mainText"><g:message code="admin.index.des07" />.</p>
          </div>

        <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN || grailsApplication.config.security.cas.bypass.toBoolean()}">
          <br/><br/><p><g:message code="admin.index.des08" />.</p>

          <div class="homeCell">
            <g:link class="mainLink" controller="dataProvider" action="list"><g:message code="admin.index.link09" /></g:link>
            <p class="mainText"><g:message code="admin.index.des09" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="dataResource" action="list"><g:message code="admin.index.link10" /></g:link>
            <p class="mainText"><g:message code="admin.index.des10" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="reports" action="list"><g:message code="admin.index.link11" /></g:link>
            <p class="mainText"><g:message code="admin.index.des11" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="contact" action="list"><g:message code="admin.index.link12" /></g:link>
            <p class="mainText"><g:message code="admin.index.des12" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="licence" action="list"><g:message code="admin.index.licence"  default="View all licences" /></g:link>
            <p class="mainText"><g:message code="admin.index.licence.desc" default="View all licences, and add new licences" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="providerCode" action="list"><g:message code="admin.index.link14" /></g:link>
            <p class="mainText"><g:message code="admin.index.des14" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="providerMap" action="list"><g:message code="admin.index.link15" /></g:link>
            <p class="mainText"><g:message code="admin.index.des15" />.</p>
          </div>

          <div class="homeCell">
            <g:link class="mainLink" controller="admin" action="export"><g:message code="admin.index.link16" /></g:link>
            <p class="mainText"><g:message code="admin.index.des16" /></p>
          </div>
        </cl:ifGranted>
      </div>
    </body>
</html>