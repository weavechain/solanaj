package org.p2p.solanaj.serum;

import org.p2p.solanaj.core.*;
import org.p2p.solanaj.programs.SerumProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;

import java.util.List;
import java.util.logging.Logger;

public class SerumManager {

    private static final Logger LOGGER = Logger.getLogger(SerumManager.class.getName());
    private final RpcClient client = new RpcClient("https://solana-api.projectserum.com");

    /**
     * Places order at the specified {@link Market} with the given {@link Order}
     * @param account Solana account to pay for the order
     * @param market Market to trade on
     * @param order Buy or sell order with quantity and price
     * @return true if the order succeeded
     */
    public String placeOrder(Account account, Account openOrders, Market market, Order order) {
        /*
          Placing orders: A user funds an intermediary account (their OpenOrders account) from their SPL token
          account (wallet) and adds an order placement request to the Request Queue
          See: https://github.com/project-serum/serum-ts/blob/master/packages/serum/src/market.ts#L637
         */

        final Transaction transaction = new Transaction();

        // Create payer account
        final Account payerAccount = new Account();

        // Call Create Account with Token Program as the assign owner... or whatnot
        final Transaction createAccountTransaction = new Transaction();

        // 0.1 SOL
        long lamports = 110000000L;
        long space = 165L;

        transaction.addInstruction(
                SystemProgram.createAccount(
                        account.getPublicKey(),
                        payerAccount.getPublicKey(),
                        lamports,
                        space,
                        TokenProgram.PROGRAM_ID
                )
        );
        transaction.addInstruction(
                TokenProgram.initializeAccount(
                        payerAccount.getPublicKey(),
                        new PublicKey("So11111111111111111111111111111111111111112"),
                        account.getPublicKey()
                )
        );
        transaction.addInstruction(
                SerumProgram.placeOrder(
                        client,
                        account,
                        payerAccount,
                        openOrders,
                        market,
                        order
                )
        );

        final List<Account> signers = List.of(account, payerAccount);

        String result = null;
        try {
            //result = client.getApi().sendTransaction(transaction, account);
            result = client.getApi().sendTransaction(transaction, signers, null);
            LOGGER.info("Result = " + result);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Cranks a given market with the ConsumeEvents instruction.
     *
     * @param market market to run crank against
     * @return transaction id of ConsumeEvents call
     */
    public String consumeEvents(Market market, Account payerAccount, List<PublicKey> openOrdersAccounts) {
        // Get all open orders accounts
        final Transaction transaction = new Transaction();

        transaction.addInstruction(
                SerumProgram.consumeEvents(
                        openOrdersAccounts,
                        payerAccount,
                        market
                )
        );

        final List<Account> signers = List.of(payerAccount);
        String result = null;
        try {
            result = client.getApi().sendTransaction(transaction, signers, null);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return result;
    }



}
