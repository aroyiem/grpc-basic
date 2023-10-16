package com.roy.client.metadata;

import com.roy.model.WithdrawalError;
import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;

public class ClientConstants {

    private static final Metadata METADATA = new Metadata();
    public static final Metadata.Key<String> USER_TOKEN = Metadata.Key.of("user-token",
            Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<WithdrawalError> WITHDRAWAL_ERROR_KEY =
            ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());

    static {
        METADATA.put(Metadata.Key.of("client-token", Metadata.ASCII_STRING_MARSHALLER), "bank-client-secret");
    }

    public static Metadata getClientToken() {
        return METADATA;
    }
}
