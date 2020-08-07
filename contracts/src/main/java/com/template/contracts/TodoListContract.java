package com.template.contracts;

import com.template.states.TodoListState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

// ************
// * Contract *
// ************
public class TodoListContract implements Contract {
    // This is used to identify our contract when building a transaction.
    //public static final String ID = "com.template.contracts.TemplateContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        TodoListState outputState = (TodoListState) tx.getOutput(0);
        if(outputState.getTaskDesc().length() < 1 )
            throw new IllegalArgumentException("Task Description must be blank");
        if(outputState.getTaskDesc().length() > 40 )
            throw new IllegalArgumentException("Task Description must not be more than 40 characters");
    }

    public void additionalCreateChecks(@NotNull LedgerTransaction tx) {
        // Write contract validation logic to be performed while creation of token
        TodoListState outputState = (TodoListState) tx.getOutput(0);
        if(outputState.getTaskDesc().length() < 1 )
            throw new IllegalArgumentException("Task Description must be blank");
        if(outputState.getTaskDesc().length() > 40 )
            throw new IllegalArgumentException("Task Description must not be more than 40 characters");
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class CreateTodoList implements Commands {}
    }
}