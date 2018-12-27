package vadc.heartbeat.service

import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Autowired

@GRpcService
class GrpcEventServiceImpl: EventServiceGrpc.EventServiceImplBase() {

    @Autowired
    private lateinit var eventInProcessor: EventInProcessor

    override fun submit(request: EventRequest, responseObserver: StreamObserver<EventResponse>) {
        val processorResponse = eventInProcessor.submit(request.body)
        val grpcResponse = EventResponse.newBuilder()
                .setId(processorResponse.id)
                .setPayload(processorResponse.payload)
                .setState(processorResponse.state.name)
        responseObserver.onNext(grpcResponse.build())
        responseObserver.onCompleted()
    }
}