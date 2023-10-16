package com.roy.client.metadata;

import com.roy.client.MoneyStreamingResponse;
import com.roy.client.deadline.DeadlineInterceptor;
import com.roy.model.Balance;
import com.roy.model.BalanceCheckRequest;
import com.roy.model.BankServiceGrpc;
import com.roy.model.WithdrawRequest;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    void setup() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(ClientConstants.getClientToken()))
                //.intercept(new DeadlineInterceptor())
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(channel);
        this.bankServiceStub = BankServiceGrpc.newStub(channel);
    }

    @Test
    void balanceTest() {
        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(7)
                .build();
        for (int i = 0; i < 20; i++) {
            try {
                int random = ThreadLocalRandom.current().nextInt(1, 4);
                System.out.println("Random: " + random);
                Balance balance = this.blockingStub
                        //.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                        .withCallCredentials(new UserSessionToken("user-secret-" + random + ":prime"))
                        .getBalance(balanceCheckRequest);
                System.out.println("Received: " + balance.getAmount());
            } catch (StatusRuntimeException ex) {
                // go with default values
                ex.printStackTrace();
            }
        }
    }

    @Test
    void withdrawTest() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(7).setAmount(40).build();
        try {
            this.blockingStub
                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .withdraw(withdrawRequest)
                    .forEachRemaining(money -> System.out.println("Received " + money.getValue()));
        } catch (StatusRuntimeException ex) {
            // go with default values
        }

    }

    @Test
    void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(50).build();
        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
    }
}
