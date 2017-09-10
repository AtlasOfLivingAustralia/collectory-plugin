<%@ page import="au.org.ala.collectory.Collection; au.org.ala.collectory.DataHub; au.org.ala.collectory.DataResource; au.org.ala.collectory.DataProvider; au.org.ala.collectory.Institution" %>
<div class="show-section  well">
  <g:if test="${instance instanceof Collection}">
    <g:set var="dir" value="data/collection"/>
  </g:if>
  <g:elseif test="${instance instanceof Institution}">
    <g:set var="dir" value="data/institution"/>
  </g:elseif>
  <g:elseif test="${instance instanceof DataProvider}">
    <g:set var="dir" value="data/dataProvider"/>
  </g:elseif>
  <g:elseif test="${instance instanceof DataResource}">
    <g:set var="dir" value="data/dataResource"/>
  </g:elseif>
  <g:elseif test="${instance instanceof DataHub}">
    <g:set var="dir" value="data/dataHub"/>
  </g:elseif>

  <h2>${title?:'Not title provided'}</h2>

  <g:if test="${fieldValue(bean: image, field: 'file')}">

    <div class="media">
      <a class="pull-left" href="#">
        <img class="showImage img-thumbnail" alt="${fieldValue(bean: image, field: "file")}"
            src="${resource(absolute: "true", dir: dir, file: image.file)}"/>
      </a>
      <div class="media-body">
        <span class="category"><g:message code="shared.images.span01" />:</span> ${fieldValue(bean: image, field: "file")} </br/>
        <span class="category"><g:message code="shared.images.span02" />:</span> ${fieldValue(bean: image, field: "caption")} <br/>
        <span class="category"><g:message code="shared.images.span03" />:</span> ${fieldValue(bean: image, field: "attribution")}<br/>
        <span class="category"><g:message code="shared.images.span04" />:</span> ${fieldValue(bean: image, field: "copyright")}<br/>
      </div>
    </div>

  </g:if>

  <div style="clear:both;"></div>
  <cl:editButton uid="${instance.uid}" page="/shared/images" target="${target}"/>
</div>
