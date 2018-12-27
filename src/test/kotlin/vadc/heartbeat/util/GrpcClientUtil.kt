package vadc.heartbeat.util

import io.grpc.ManagedChannelBuilder
import org.junit.Ignore
import org.junit.Test
import vadc.heartbeat.service.EventRequest
import vadc.heartbeat.service.EventServiceGrpc

class GrpcClientUtil {

    @Test
    @Ignore
    fun runClient() {
        val channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build()

        val stub = EventServiceGrpc.newBlockingStub(channel)

        val helloResponse = stub.submit(EventRequest.newBuilder().setBody("grpc body").build())

        channel.shutdown()
    }
}