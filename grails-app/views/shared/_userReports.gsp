<g:if test="${instance.getResources() && grailsApplication.config.loggerURL }">
    <div class="well">
        <!-- Resources -->
        <h2>User download reports</h2>
        <ul class='fancy'>
            <g:set var="downloadEntityUids" value="${instance.getResources().collect {it.uid}.join(',')}" />
            <li>
                <a href="${grailsApplication.config.loggerURL}/admin/userReport/download?fileName=user-report-${instance.uid}.csv&entityUids=${downloadEntityUids}&eventId=1002">Download user report for this data partner</a>
            </li>
            <li>
                <a href="${grailsApplication.config.loggerURL}/admin/userReport/downloadDetailed?fileName=user-report-detailed-${instance.uid}.csv&entityUids=${downloadEntityUids}&eventId=1002">Download detailed user report for this data partner</a>
            </li>
        </ul>
    </div>
</g:if>