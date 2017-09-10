<g:set var='external' value='${instance.externalIdentifiers}'/>
<g:if test="${external.size() > 0}">
    <section class="public-metadata" id="externalIdentifiers">
        <h4><g:message code="public.sdr.externalIdentifiers.title" /></h4>
        <ul>
            <g:each var="ext" in="${external}">
                <li><g:if test="${ext.uri}"><a href="${ext.uri}" class="external" target="_blank"><g:fieldValue bean="${ext}" field="label"/></a></g:if><g:else><g:fieldValue bean="${ext}" field="label"/></g:else></li>
            </g:each>
        </ul>
    </section>
</g:if>
