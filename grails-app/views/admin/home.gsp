<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <title><g:message code="admin.home.title" /></title>
	<meta name="layout" content="${grailsApplication.config.skin.layout}" />

    </head>
    
    <body>

        <div id="nav">
            <ul id="nav-primary" class="left">
                  <li class="nav-item active">
                          <g:link controller="admin" action="home" class="active" accesskey="2"><g:message code="admin.home.li.home" /></g:link>
                  </li>
            </ul>
            <ul id="nav-secondary" class="right">
                  <li class="nav-item">
                          <a href="${grailsApplication.config.ala.baseURL}" accesskey="3"><g:message code="admin.home.li.ala" /></a>
                  </li>
                  <li class="nav-item">
                          <a href="${grailsApplication.config.grails.serverURL}" accesskey="4"><g:message code="admin.home.li.collections" /></a>
                  </li>
                  <li class="nav-item">
                      <cl:isLoggedIn>
                          <a href="https://auth.ala.org.au/cas/logout?url=${grailsApplication.config.grails.serverURL}/admin/home"><g:message code="admin.logout" /></a>
                      </cl:isLoggedIn>
                      <cl:isNotLoggedIn>
                          <a href="https://auth.ala.org.au/cas/login?service=${grailsApplication.config.security.cas.serverName}/${grailsApplication.config.security.cas.context}/admin"><g:message code="admin.login" /></a>
                      </cl:isNotLoggedIn>
                  </li>
            </ul>
        </div>

        <div class="login-info">
            <cl:isLoggedIn>
                <span id="logged-in"><g:message code="admin.loggedin" /> <cl:loggedInUsername/></span>
            </cl:isLoggedIn>
        </div>


      <div class="floating-content">

          <!--div style="float:right;">
            <g:link class="mainLink" controller="public" action="map">View public site</g:link>
          </div-->
          <div id="welcome">
              <img width="130" height="109" src="${resource(dir:'images/admin',file:'swift-moth.gif')}"/>
              <div>
                  <span style="font-size:12px;"><g:message code="admin.home.welcome.span01" /></span>
                    <h1><g:message code="admin.home.welcome.title01" /></h1>
                    <p><g:message code="admin.home.welcome.des01" />.</p>
              </div>
          </div>

          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>

          <cl:isNotLoggedIn>
            <div class="homeCell">
              <h4 class="inline"><g:message code="admin.home.welcome.title02" /></h4>
                <span style="" class="buttons" style="float: right;">
                  <a href="${grailsApplication.config.security.cas.loginUrl}?service=${grailsApplication.config.grails.serverURL}/admin">&nbsp;<g:message code="admin.home.welcome.login" />&nbsp;</a>
                </span>
              <p><g:message code="admin.home.welcome.des02" /></p>
            </div>
          </cl:isNotLoggedIn>
          <div style="clear:both;"></div>

          <div id="buttons">
          <div class='link-group'>
              <h2><g:message code="admin.home.title01" /></h2>
              <g:link class="mainLink" controller="collection" action="list"><g:message code="admin.home.link.vac" /></g:link>
              <g:link class="mainLink" controller="collection" action="myList" id="68"><g:message code="admin.home.link.vmc" /></g:link>
              <g:link class="mainLink" controller="collection" action="create-notyet"><g:message code="admin.home.link.aac" /></g:link>
              <span><g:message code="admin.home.span01" /></span>
              <g:form action="search">
                <g:textField class="mainText" name="term"/><span class="search-button-wrapper">
                  <g:actionSubmitImage width="24" src="${resource(dir:'images/admin',file:'search.png')}" action="search" value="Search"/></span>
              </g:form>
          </div>

          <div class='link-group'>
              <h2><g:message code="admin.home.title02" /></h2>
              <g:link class="mainLink" controller="institution" action="list"><g:message code="admin.home.link.vai" /></g:link>
              <g:link class="mainLink" controller="institution" action="myList" id="68"><g:message code="admin.home.link.vmi" /></g:link>
              <g:link class="mainLink" controller="institution" action="create-notyet"><g:message code="admin.home.link.aai" /></g:link>
              <span><g:message code="admin.home.span02" /></span>
              <g:form action="search">
                <g:textField class="mainText" name="term"/><span class="search-button-wrapper">
                  <g:actionSubmitImage src="${resource(dir:'images/admin',file:'search.png')}" action="search" value="Search"/></span>
              </g:form>
          </div>


          <div class='link-group'>
              <h2><g:message code="admin.home.title03" /></h2>
              <g:link class="mainLink" controller="dataResource" action="list"><g:message code="admin.home.link.vadr" /></g:link>
              <g:link class="mainLink" controller="dataProvider" action="list"><g:message code="admin.home.link.vadp" /></g:link>
              <g:link class="mainLink" controller="institution" action="create-notyet"><g:message code="admin.home.link.aadr" /></g:link>
              <span><g:message code="admin.home.span03" /></span>
              <g:form action="search">
                <g:textField class="mainText" name="term"/><span class="search-button-wrapper">
                  <g:actionSubmitImage src="${resource(dir:'images/admin',file:'search.png')}" action="search" value="Search"/></span>
              </g:form>
          </div>

          <div class='link-group'>
              <h2><g:message code="admin.home.title04" /></h2>
              <g:link class="mainLink" controller="contact" action="showProfile"><g:message code="admin.home.link.emp" /></g:link>
              <p class="mainText"><g:message code="admin.home.des01" />.</p>
          </div>

          <g:if test="${request.isUserInRole(ProviderGroup.ROLE_ADMIN)}">

          <div class='link-group admin'>
              <h2><g:message code="admin.home.title05" /></h2>
              <g:link class="mainLink" controller="reports" action="home"><g:message code="admin.home.link.vr" /></g:link>
              <g:link class="mainLink" controller="contact" action="list"><g:message code="admin.home.link.mc" /></g:link>
              <g:link class="mainLink" controller="admin" action="export"><g:message code="admin.home.link.eadaj" /></g:link>
          </div>

          <div class='link-group admin'>
              <h2><g:message code="admin.home.title06" /></h2>
              <g:link class="mainLink" controller="providerCode" action="list"><g:message code="admin.home.link.mpc" /></g:link>
              <p class="mainText"><g:message code="admin.home.link.elovc" />.</p>
              <g:link class="mainLink" controller="providerMap" action="list"><g:message code="admin.home.link.mpm" /></g:link>
              <p class="mainText"><g:message code="admin.home.link.acicc" />.</p>
          </div>
          </g:if>
        </div>

        <div style="clear: both;"></div>

      </div>
      <script type="text/javascript">
          $('.link-group').hover(
                  function() {
                      $(this).addClass('link-group-highlight');
                  },
                  function() {
                      $(this).removeClass('link-group-highlight');
                  }
          );
      </script>
    </body>
</html>
