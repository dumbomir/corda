package net.corda.node.services.keys

import net.corda.core.CordaOID
import net.corda.core.identity.Role
import net.corda.testing.node.MockServices
import net.corda.testing.singleIdentityAndCert
import org.bouncycastle.asn1.DERSequence
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KMSUtilsTests {
    @Test
    fun `should generate certificates with the correct role`() {
        val mockServices = MockServices()
        val wellKnownIdentity = mockServices.myInfo.singleIdentityAndCert()
        val confidentialIdentity = mockServices.keyManagementService.freshKeyAndCert(wellKnownIdentity, false)
        val cert = confidentialIdentity.certificate
        val extensionData = cert.getExtensionValue(CordaOID.X509_EXTENSION_CORDA_ROLE)
        assertNotNull(extensionData)
        val extension = DERSequence.getInstance(extensionData)
        assertEquals(1, extension.size())
        val role = Role.parse(extension)
        assertEquals(Role.CONFIDENTIAL_IDENTITY, role)
    }
}