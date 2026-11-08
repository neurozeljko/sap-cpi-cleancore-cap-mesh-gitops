import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

Message processData(Message message) {
    def reader = message.getBody(java.io.Reader)
    def json = new JsonSlurper().parse(reader)

    def bpData = json.businessPartnerData ?: [:]

    def s4Payload = [
        BusinessPartnerCategory: bpData.businessPartnerCategory ?: "2",
        OrganizationName1: bpData.organizationName ?: "Acme Industrial Technologies SE",
        SearchTerm1: bpData.searchTerm1 ?: "ACME",
        LegalForm: bpData.legalForm ?: "01",
        to_BusinessPartnerRole: bpData.roles ? bpData.roles.collect { [BusinessPartnerRole: it.businessPartnerRole] } : [[BusinessPartnerRole: "FLCU01"]],
        to_BusinessPartnerAddress: bpData.addresses ? bpData.addresses.collect {
            [
                Country: it.country ?: "DE",
                CityName: it.city ?: "Munich",
                PostalCode: it.postalCode ?: "80331",
                StreetName: it.streetName ?: "Maximilianstraße",
                HouseNumber: it.houseNumber ?: "42"
            ]
        } : [[Country: "DE", CityName: "Munich", PostalCode: "80331", StreetName: "Maximilianstraße", HouseNumber: "42"]]
    ]

    String userPrincipal = message.getProperty("Prop_User_Principal") ?: "elena.rostova@enterprise.com"
    message.setHeader("X-Forwarded-User", userPrincipal)
    message.setHeader("Content-Type", "application/json")
    message.setProperty("Prop_S4HANA_Payload_Status", "READY_FOR_DISPATCH")

    message.setBody(JsonOutput.prettyPrint(JsonOutput.toJson(s4Payload)))
    return message
}
