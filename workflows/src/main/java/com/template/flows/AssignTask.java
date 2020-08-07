package com.template.flows;


import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import com.template.contracts.TodoListContract;
import com.template.states.TodoListState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * This flow is used to put a bid on an asset put on auction.
 */
public class AssignTask {

    //private BidFlow(){}

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction>{

        private final Party assignedTo;
        private final String linearId;

        /**
         * Constructor to initialise flow parameters received from rpc.
         *

         */
        public Initiator(Party assignedTo, String linearId) {
            this.assignedTo = assignedTo;
            this.linearId = linearId;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final QueryCriteria q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(UUID.fromString(linearId)));

            final Vault.Page<TodoListState> taskStatePage = getServiceHub().getVaultService().queryBy(TodoListState.class, q);
            final List<StateAndRef<TodoListState>> states = taskStatePage.getStates();


            TodoListState input = states.get(0).getState().getData();
            System.out.println("LinearId:" + input.getLinearId());
            //Create the output state
            TodoListState output =input.assign(assignedTo);


            TransactionBuilder builder = new TransactionBuilder(states.get(0).getState().getNotary())
                    .addInputState(states.get(0))
                    .addOutputState(output)
                    .addCommand(new TodoListContract.Commands.CreateTodoList(),
                            getOurIdentity().getOwningKey(), assignedTo.getOwningKey());

            // Verify the transaction
            builder.verify(getServiceHub());

            // Sign the transaction
            SignedTransaction selfSignedTransaction = getServiceHub().signInitialTransaction(builder);

            // Call finality Flow to notarise and commit the transaction in ledger

            FlowSession assignToSession = initiateFlow(assignedTo);
            final SignedTransaction collectSignedTx = subFlow(new CollectSignaturesFlow(selfSignedTransaction, ImmutableSet.of(assignToSession),
                    CollectSignaturesFlow.Companion.tracker()));
            return subFlow(new FinalityFlow(collectSignedTx, ImmutableSet.of(assignToSession)));
        }
    }

}