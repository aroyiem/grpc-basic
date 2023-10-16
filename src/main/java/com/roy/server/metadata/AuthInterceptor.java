package com.roy.server.metadata;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import java.util.Objects;

/*
    user-secret-3:prime
    user-secret-2:regular

 */
public class AuthInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String clientToken = metadata.get(ServerConstants.USER_TOKEN);
        if (this.validate(clientToken)) {
            UserRole userRole = this.extractUserRole(clientToken);
            Context context = Context.current().withValue(ServerConstants.CTX_USER_ROLE, userRole);

            return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
            //return serverCallHandler.startCall(serverCall, metadata);
        } else {
            Status status = Status.UNAUTHENTICATED.withDescription("Invalid token/expired token");
            serverCall.close(status, metadata);
        }
        return new ServerCall.Listener<>() {

        };
    }

    private boolean validate(String token) {
        return Objects.nonNull(token) &&
                (token.startsWith("user-secret-3") || token.startsWith("user-secret-2"));
    }

    private UserRole extractUserRole(String jwt) {
        return jwt.endsWith("prime") ? UserRole.PRIME : UserRole.STANDARD;
    }
}
