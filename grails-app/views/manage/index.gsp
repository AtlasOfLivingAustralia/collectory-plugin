<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <title><g:message code="manage.index.title" /></title>
	    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    </head>
    
    <body>
      <div class="floating-content manage">
    
        <div style="float:right;">
            <g:link class="mainLink" controller="public" action="map"><g:message code="manage.index.link" /></g:link>
        </div>
        <h1><g:message code="manage.index.title01" /></h1>
        <p><g:message code="manage.index.des01" />.</p>

        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>

        <div style="width:400px;">
            <h2 style="margin-top:30px;"><g:message code="manage.index.title02" /></h2>
            <span id="login-button">
                <a href="https://auth.ala.org.au/cas/login?service=${grailsApplication.config.security.cas.serverName}${request.forwardURI}">&nbsp;<g:message code="manage.index.login" />&nbsp;</a>
            </span>

            <p><g:message code="manage.index.des02" /></p>
        </div>
        <div class="well">
            <h3><g:message code="manage.index.title03" /></h3>
            <h4><g:message code="manage.index.title04" />?</h4>
            <p><g:message code="manage.index.des03" />:</p>
            <ol>
                <li><g:message code="manage.index.li01" /></li>
                <li><g:message code="manage.index.li02" /></li>
                <li><g:message code="manage.index.li03" />.</li>
            </ol>
            <h4><g:message code="manage.index.title05" />!</h4>
            <p><g:message code="manage.index.des04" /> <a href="https://auth.ala.org.au/emmet/selfRegister.html"><g:message code="manage.index.des05" /></a>.<p>
            <p><g:message code="manage.index.des06" />.</p>
            <h4><g:message code="manage.index.title06" />?</h4>
            <p><g:message code="manage.index.des07" /> <span class="link" onclick="return sendEmail('support(SPAM_MAIL@ALA.ORG.AU)ala.org.au')"><g:message code="manage.index.des08" /></span>
            <g:message code="manage.index.des09" /> ROLE_EDITOR.</p>
            <h4><g:message code="manage.index.title07" />?</h4>
            <p><g:message code="manage.index.des10" />
            <span class="link" onclick="return sendEmail('support(SPAM_MAIL@ALA.ORG.AU)ala.org.au')"><g:message code="manage.index.des08" /></span>
            <g:message code="manage.index.des11" />.</p>
        </div>

    </body>
</html>