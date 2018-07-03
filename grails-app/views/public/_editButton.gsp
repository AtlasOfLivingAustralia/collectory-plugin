<cl:isLoggedIn>
    <cl:isAuth uuid="${instance.uid}">
        <g:set var="providerType" value="${instance.getClass().getSimpleName()}"/>
        <div style="float:right;">
            <a href="${g.createLink(controller: providerType, action: 'show', id: instance.uid)}"
                    class="btn btn-primary" title="<g:message code='public.show.edit.tooltip'/>">
                <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>&nbsp;&nbsp;<g:message
                    code="public.show.header.edit" default="Edit"/>
            </a>
        </div>
    </cl:isAuth>
</cl:isLoggedIn>