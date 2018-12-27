package vadc.heartbeat.util

import io.grpc.ManagedChannelBuilder
import vadc.heartbeat.service.EventRequest
import vadc.heartbeat.service.EventServiceGrpc


fun main(args: Array<String>) {
    val channel = ManagedChannelBuilder.forAddress("localhost", 6565)
            .usePlaintext()
            .build()

    val stub = EventServiceGrpc.newBlockingStub(channel)

    val helloResponse = stub.submit(EventRequest.newBuilder().setBody("grpc body").build())

    channel.shutdown()
}