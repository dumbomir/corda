package net.corda.node.services.messaging

import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.RPCOps
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.utilities.NetworkHostAndPort
import net.corda.node.services.RPCUserService
import net.corda.nodeapi.internal.config.SSLConfiguration
import net.corda.nodeapi.internal.ArtemisMessagingComponent.Companion.NODE_USER
import net.corda.nodeapi.internal.crypto.X509Utilities
import net.corda.nodeapi.internal.crypto.getX509Certificate
import net.corda.nodeapi.internal.crypto.loadKeyStore
import org.apache.activemq.artemis.api.core.management.ActiveMQServerControl

class RPCMessagingClient(private val config: SSLConfiguration, serverAddress: NetworkHostAndPort) : SingletonSerializeAsToken() {
    private val artemis = ArtemisMessagingClient(config, serverAddress)
    private var rpcServer: RPCServer? = null

    fun start(rpcOps: RPCOps, userService: RPCUserService) = synchronized(this) {
        val locator = artemis.start().sessionFactory.serverLocator
        val myCert = loadKeyStore(config.sslKeystore, config.keyStorePassword).getX509Certificate(X509Utilities.CORDA_CLIENT_TLS)
        rpcServer = RPCServer(rpcOps, NODE_USER, NODE_USER, locator, userService, CordaX500Name.build(myCert.subjectX500Principal))
    }

    fun start2(serverControl: ActiveMQServerControl) = synchronized(this) {
        rpcServer!!.start(serverControl)
    }

    fun stop() = synchronized(this) {
        artemis.stop()
    }
}
