<%@ page import="java.text.DecimalFormat; java.text.SimpleDateFormat" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title>${fieldValue(bean: instance, field: "name")} | Data sets | Atlas of Living Australia</title>
    </head>
    <body class="two-column-right">
      <div id="content">
        <div id="header" class="collectory">
        </div><!--close header-->
        <div id="column-one">

          <h1>${fieldValue(bean: instance, field: "name")}</h1>
          <g:render template="editButton"/>
          <div class="section">
            <p><g:message code="public.stdr.co.des01" args="[name, instance.dateCreated]" />.</p>
            <p><g:message code="public.stdr.co.des02" args="[instance.numberOfRecords]" />.</p>
            <cl:lastUpdated date="${instance.lastUpdated}"/>
          </div>
        </div><!--close column-one-->
        <div id="column-two">
          <div class="section sidebar">
              <!-- contacts -->
              <g:set var="contacts" value="${instance.getPublicContactsPrimaryFirst()}"/>
              <g:render template="contacts" bean="${contacts}"/>
              <div class="section">
              <p>${fieldValue(bean: instance, field: "firstName")} ${fieldValue(bean: instance, field: "lastName")}</p>
              <g:if test="${instance.email}"><cl:emailLink>${fieldValue(bean: instance, field: "email")}</cl:emailLink><br/></g:if>
            </div>
          </div>
        </div>
      </div>
    </body>
</html>
