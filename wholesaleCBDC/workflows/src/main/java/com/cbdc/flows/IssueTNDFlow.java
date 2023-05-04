package com.cbdc.flows;
import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.contracts.utilities.AmountUtilities;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.Collections;

@StartableByRPC
@InitiatingFlow
public class IssueTNDFlow extends FlowLogic<SignedTransaction> {
    //private final Party bank;
    private final long amount;

    public IssueTNDFlow(long amount) {
       // this.bank = bank;
        this.amount = amount;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        final TokenType tndTokenType = new TokenType("TND",3);
       if (!getOurIdentity().getName().equals(CentralBankConstants.TND_B1) && !getOurIdentity().getName().equals(CentralBankConstants.TND_B2)) {
           throw new FlowException("We are not concerned by issuing TND CBDC");
        }
        final IssuedTokenType CbTnd = new IssuedTokenType(getOurIdentity(), tndTokenType);

        // Create a 100 gbp token that can be split and merged.
        final Amount<IssuedTokenType> amountOfUsd = AmountUtilities.amount(amount, CbTnd);
        final FungibleToken tndToken = new FungibleToken(amountOfUsd, getServiceHub().getMyInfo().getLegalIdentities().get(0), null);

        // Issue the token to alice.
        return subFlow(new IssueTokens(
                Collections.singletonList(tndToken), // Output instances
                Collections.emptyList())); // Observers
    }
}

