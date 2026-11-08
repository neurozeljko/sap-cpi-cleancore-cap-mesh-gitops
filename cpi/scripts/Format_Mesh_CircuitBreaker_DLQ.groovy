import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput

Message processData(Message message) {
    def exception = message.getProperty("CamelExceptionCaught")
    String correlationId = message.getHeader("SAP_MplCorrelationId", String) ?: "N/A"
    String userPrincipal = message.getProperty("Prop_User_Principal") ?: "UNKNOWN"

    def incidentReport = [
        circuitBreakerStatus: "TRIPPED_OPEN",
        incidentSeverity: "CRITICAL",
        incidentDetails: [
            correlationId: correlationId,
            timestamp: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'"),
            failedUserPrincipal: userPrincipal,
            exceptionType: exception ? exception.getClass().getName() : "UnknownException",
            exceptionMessage: exception ? exception.getMessage() : "Downstream S/4HANA unreachable or Circuit Breaker Open"
        ],
        remediation: "Event quarantined in Dead-Letter Queue. Incident dispatched to SAP Cloud ALM Alerting."
    ]

    message.setBody(JsonOutput.prettyPrint(JsonOutput.toJson(incidentReport)))
    message.setHeader("Content-Type", "application/json")
    return message
}
