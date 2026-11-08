import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput

Message processData(Message message) {
    long currentTime = new Date().getTime()
    long startTime = (message.getProperty("Prop_Execution_Start_Time") as Long) ?: currentTime
    long duration = currentTime - startTime

    String correlationId = message.getHeader("SAP_MplCorrelationId", String) ?: UUID.randomUUID().toString()
    String userPrincipal = message.getProperty("Prop_User_Principal") ?: "UNKNOWN"
    String originatingService = message.getProperty("Prop_Originating_Service") ?: "CAP_SERVICE"

    def almTelemetry = [
        telemetryHeader: [
            cloudAlmTenant: "alm-prod-eu10",
            serviceType: "SAP_INTEGRATION_SUITE",
            serviceName: "IF_CleanCore_CAP_APIMesh_to_S4HANA",
            timestamp: new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        ],
        metrics: [
            correlationId: correlationId,
            originatingClient: originatingService,
            userPrincipal: userPrincipal,
            executionDurationMs: duration,
            p95Exceeded: (duration > 300),
            operationalStatus: (duration > 1500) ? "DEGRADED" : "OPTIMAL"
        ]
    ]

    message.setProperty("Prop_Telemetry_Duration_Ms", duration)
    message.setProperty("Prop_Telemetry_Log", JsonOutput.toJson(almTelemetry))
    return message
}
