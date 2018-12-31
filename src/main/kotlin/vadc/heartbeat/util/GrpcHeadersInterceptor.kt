package vadc.heartbeat.util

import io.grpc.*
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import org.lognet.springboot.grpc.GRpcGlobalInterceptor
import org.springframework.beans.factory.annotation.Value

@GRpcGlobalInterceptor
class GrpcHeadersInterceptor: ServerInterceptor {

    @Value("\${hbf.api.key}")
    private lateinit var apiKey: String

    override fun <ReqT: Any?, RespT: Any?> interceptCall(call: ServerCall<ReqT, RespT>,
                                                           headers: Metadata,
                                                           next: ServerCallHandler<ReqT, RespT>)
            : ServerCall.Listener<ReqT> {

        val apiKeyHeader = headers.get(headerKey)
        if (apiKeyHeader != apiKey) {
            call.close(Status.UNAUTHENTICATED.withDescription("x-api-key header is incorrect"), Metadata())
        }
        return next.startCall(object: SimpleForwardingServerCall<ReqT, RespT>(call) { }, headers)
    }

    companion object {
        val headerKey: Metadata.Key<String> = Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER)
    }
}