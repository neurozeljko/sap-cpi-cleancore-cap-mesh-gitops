# ADR-014: Clean Core API Mesh with Node.js CAP, OAuth 2.0 RFC 8693 Token Exchange & GitOps CI/CD

## Status
Accepted

## Context
Transitioning to S/4HANA Public Cloud strictly prohibits classic ABAP in-stack enhancements and Z-tables. Business logic must reside outside the ERP core while interacting seamlessly with standard APIs under strict SOX and GDPR compliance, preserving actual user attribution without shared technical users.

## Decision
1. Host side-by-side extensibility services in Node.js CAP on SAP BTP.
2. Utilize SAP Integration Suite as the API Mesh Gateway.
3. Enforce OAuth 2.0 RFC 8693 Token Exchange & Principal Propagation to maintain end-user identity in S/4HANA audit logs.
4. Automate static analysis and multi-tier transport via Project Piper GitOps CI/CD.
5. Stream OpenTelemetry performance and exception metrics to SAP Cloud ALM.

## Consequences
- 100% upgrade safety for continuous S/4HANA biannual updates.
- Total elimination of shared technical RFC users.
- Automated quality gate enforcement prior to production deployments.
