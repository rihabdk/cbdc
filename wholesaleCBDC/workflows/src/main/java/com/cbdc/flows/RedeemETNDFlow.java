package com.cbdc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.contracts.utilities.AmountUtilities;
import com.r3.corda.lib.tokens.money.FiatCurrency;
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemFungibleTokens;
import com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;

@InitiatingFlow
@StartableByRPC
public class RedeemETNDFlow extends FlowLogic<SignedTransaction> {

    private final long amount ;

    public RedeemETNDFlow(long amount) {
        this.amount = amount;
    }


    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        final TokenType tndTokenType = FiatCurrency.Companion.getInstance("TND");
        final Party tndMint = getServiceHub().getNetworkMapCache().getPeerByLegalName( CentralBankConstants.TND_MINT);
        if (tndMint == null) throw new FlowException("Not found");

        // Describe how to find those TND held by Me.
        final QueryCriteria heldByMe = QueryUtilities.heldTokenAmountCriteria(tndTokenType, getOurIdentity());
        final Amount<TokenType> tndAmount = AmountUtilities.amount(amount, tndTokenType);

        // Do the redeem
        return subFlow(new RedeemFungibleTokens(
                tndAmount, // How much to redeem
                tndMint, // issuer
                Collections.emptyList(), // Observers
                heldByMe, // Criteria to find the inputs
                getOurIdentity())); // change holder
    }

}
