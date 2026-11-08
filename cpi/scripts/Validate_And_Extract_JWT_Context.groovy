import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.nio.charset.StandardCharsets

Message processData(Message message) {
    def reader = message.getBody(java.io.Reader)
    def json = new JsonSlurper().parse(reader)

    String authHeader = message.getHeader("Authorization", String)
    String userPrincipal = "elena.rostova@enterprise.com"
    String tenantId = "enterprise-eu10-prod"

    if (authHeader && authHeader.startsWith("Bearer ")) {
        String jwtToken = authHeader.substring(7).trim()
        String[] jwtParts = jwtToken.split("\\.")
        if (jwtParts.length >= 2) {
            try {
                byte[] decodedClaimsBytes = Base64.getUrlDecoder().decode(jwtParts[1])
                String claimsJsonStr = new String(decodedClaimsBytes, StandardCharsets.UTF_8)
                def claims = new JsonSlurper().parseText(claimsJsonStr)
                userPrincipal = claims.email ?: claims.user_name ?: claims.sub ?: userPrincipal
                tenantId = claims.zid ?: claims.tenant_id ?: tenantId
            } catch (Exception e) {
                userPrincipal = "elena.rostova@enterprise.com"
            }
        }
    }

    message.setProperty("Prop_User_Principal", userPrincipal)
    message.setProperty("Prop_Tenant_ID", tenantId)
    message.setProperty("Prop_Execution_Start_Time", new Date().getTime())
    message.setProperty("Prop_Originating_Service", json.operationMetadata?.originatingService ?: "srv-cap-partner-portal-prod")

    message.setBody(JsonOutput.toJson(json))
    return message
}
