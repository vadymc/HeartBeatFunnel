package vadc.heartbeat.util

import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
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

        val header = Metadata()
        header.put(headerKey, "header")
        val stubWithHeader = MetadataUtils.attachHeaders(stub, header)
        val eventRequest = EventRequest.newBuilder()
                .setBody("grpc body")
                .build()
        val helloResponse = stubWithHeader.submit(eventRequest)

        channel.shutdown()
    }

    companion object {
        val headerKey: Metadata.Key<String> = Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER)
    }
}