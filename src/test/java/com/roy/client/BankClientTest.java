package com.roy.client;

import com.google.common.util.concurrent.Uninterruptibles;
import com.roy.model.Balance;
import com.roy.model.BalanceCheckRequest;
import com.roy.model.BankServiceGrpc;
import com.roy.model.WithdrawRequest;
import com.roy.server.BankService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    void setup() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(channel);
        this.bankServiceStub = BankServiceGrpc.newStub(channel);
    }

    @Test
    void balanceTest() {
        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(5)
                .build();
        Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
        System.out.println("Received: " + balance.getAmount());
    }

    @Test
    void withdrawTest() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(7).setAmount(40).build();
        this.blockingStub.withdraw(withdrawRequest)
                .forEachRemaining(money -> System.out.println("Received " + money.getValue()));
    }

    @Test
    void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(50).build();
        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
    }
}
