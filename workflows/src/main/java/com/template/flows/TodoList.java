package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TodoListContract;
import com.template.states.TodoListState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class TodoList extends FlowLogic<SignedTransaction> {
    private final ProgressTracker progressTracker = new ProgressTracker();

    @Nullable
    private String taskDesc="";

    public TodoList(String taskDesc) {
        this.taskDesc = taskDesc;
    }
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // Initiator flow logic goes here.
        /* Choose the notary for the transaction */

        ServiceHub serviceHub = getServiceHub();
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        /* Get a reference of own identity */
        Party me = getOurIdentity();

        /* Construct the output state */
        TodoListState todoListState = TodoListState.create(me,me, taskDesc);
        System.out.println("Linear ID"  + todoListState.getLinearId());
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(todoListState)
                .addCommand(new TodoListContract.Commands.CreateTodoList(),
                        getOurIdentity().getOwningKey()); // Required Signers
        // Verify the transaction
        transactionBuilder.verify(getServiceHub());

        SignedTransaction stx = serviceHub.signInitialTransaction(transactionBuilder);
        return subFlow(new FinalityFlow(stx, Arrays.asList()));

    }
}
