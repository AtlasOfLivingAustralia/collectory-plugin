<g:if test="${it?.size() > 0}">
  <section class="public-metadata">
    <h4><g:message code="public.show.contacts.contact" default="Contact"/></h4>
    <g:each in="${it}" var="cf">
      <div class="contact">
        <p>
            <span class="contactName">${cf?.contact?.buildName()}</span><br/>
            <g:if test="${cf?.role}">${cf?.role}<br/></g:if>
            <g:if test="${cf?.contact?.phone}"><g:message code="public.show.contacts.phone" default="phone"/> : ${cf?.contact?.phone}<br/></g:if>
            <g:if test="${cf?.contact?.fax}"><g:message code="public.show.contacts.phone" default="phone"/>: ${cf?.contact?.fax}<br/></g:if>
            <cl:emailLink email="${cf?.contact?.email}"><g:message code="public.show.contacts.email" default="email this contact"/> </cl:emailLink>
        </p>
      </div>
    </g:each>
  </section>
</g:if>
