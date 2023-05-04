package com.cbdc.flows;

import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.*;
//import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService;
import com.r3.corda.lib.accounts.workflows.services.KeyManagementBackedAccountService;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import net.corda.core.flows.FlowLogic;;
import net.corda.core.flows.StartableByRPC;

import java.util.UUID;

@StartableByRPC
@StartableByService
@InitiatingFlow
public class CreateNewAccount extends FlowLogic<String>{

    private String cin;
//    private String cin;


    public CreateNewAccount(String cin)
    {
        this.cin = cin;
    }


    @Override
    public String call() throws FlowException {
        StateAndRef<AccountInfo> newAccount = null;
        try {
            newAccount = getServiceHub().cordaService(KeyManagementBackedAccountService.class).createAccount(cin).get();
        } catch (Exception e) {
            //System.out.println("aaaaaaaaaaaaaaaaaa");
            //System.out.println(e);
            e.printStackTrace();
        }
        AccountInfo acct = newAccount.getState().getData();
        return "" + acct.getName() + " team's account was created. UUID is : " + acct.getIdentifier();
    }
}
