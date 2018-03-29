<g:if test="${( instance.entityType() == 'DataResource' || instance.getResources()) && grailsApplication.config.loggerURL }">
    <div class="well">

        <!-- Resources -->
        <h2>User download reports</h2>

        <g:set var="downloadEntityUids" value="${instance.uid}" />

        <g:if test="${instance.entityType() == 'DataProvider' || instance.entityType() == 'Institution'}">
            <g:set var="downloadEntityUids" value="${instance.getResources().collect {it.uid}.join(',')}" />
        </g:if>

        <p>
            <a class="btn btn-default" href="${grailsApplication.config.loggerURL}/admin/userReport/download?fileName=user-report-${instance.uid}.csv&entityUids=${downloadEntityUids}&eventId=1002">
                <i class="glyphicon glyphicon-cloud-download"></i>
                Download user report for this data partner</a>
        </p>
        <p>
            <a class="btn btn-default" href="${grailsApplication.config.loggerURL}/admin/userReport/downloadDetailed?fileName=user-report-detailed-${instance.uid}.csv&entityUids=${downloadEntityUids}&eventId=1002">
                <i class="glyphicon glyphicon-cloud-download"></i>
                Download detailed user report for this data partner</a>
        </p>
    </div>
</g:if>