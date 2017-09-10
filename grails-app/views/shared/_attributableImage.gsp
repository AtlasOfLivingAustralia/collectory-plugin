<g:if test="${fieldValue(bean: command, field: 'imageRef.file')}">
    <div>

        <img alt="${fieldValue(bean: command, field: "imageRef.file")}"
             src="${resource(absolute: 'true', dir: 'data/' + directory, file: command.imageRef.file)}"/>
        <span style="padding-right:75px;">${command.imageRef.file}</span>

    </div>
</g:if>
<div class="form-group">
    <label for="imageFile">
        <g:if test="${fieldValue(bean: command, field: 'imageRef.file')}">
            <g:message code="shared.mes01"/> <br/><g:message code="shared.mes02"/>
        </g:if>
        <g:else>
            <g:message code="shared.mes03"/> <br/><g:message code="shared.mes04"/>
        </g:else>
    </label>
    <input class="form-control" type="file" name="imageFile" value="${command?.imageRef?.file}"/>
</div>

<div class="form-group">
    <label for="imageRef.caption"><g:message code="providerGroup.imageRef.filename.label" default="Caption"/></label>
    <g:textField class="form-control" name="imageRef.caption" maxlength="128" value="${command?.imageRef?.caption}"/>
</div>

<div class="form-group">
    <label for="imageRef.attribution"><g:message code="providerGroup.imageRef.attribution.label" default="Attribution"/></label>
    <g:textField class="form-control" name="imageRef.attribution" maxlength="128" value="${command?.imageRef?.attribution}"/>
</div>

<div class="form-group">
    <label for="imageRef.copyright"><g:message code="providerGroup.imageRef.copyright.label" default="Copyright"/></label>
    <g:textField class="form-control" name="imageRef.copyright" maxlength="128" value="${command?.imageRef?.copyright}"/>
</div>
