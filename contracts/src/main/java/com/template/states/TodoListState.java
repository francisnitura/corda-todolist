package com.template.states;


import com.template.contracts.TodoListContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// *********
// * State *
// *********

@BelongsToContract(TodoListContract.class)
public class TodoListState implements LinearState {

    private final UniqueIdentifier linearId;
    private final Party assignedBy;
    private final Party assignedTo;
    private final String taskDesc;
    private final Date dateOfCreated;

    public TodoListState(Party assignedBy, Party assignedTo, String taskDesc, UniqueIdentifier linearId, Date dateOfCreated) {
        this.linearId =  linearId;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.taskDesc = taskDesc;
        this.dateOfCreated = dateOfCreated;

    }

    public static TodoListState create(Party assignedBy, Party assignedTo, String taskDesc){
        return new TodoListState(assignedBy,assignedTo,taskDesc,new UniqueIdentifier(), new Date());
    }
    public TodoListState assign(Party assignedTo){
        return new TodoListState(this.assignedBy, assignedTo,this.taskDesc,this.linearId,this.dateOfCreated);
    }
    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }
    public Party getAssignedBy() {
        return assignedBy;
    }
    public Party getAssignedTo() {
        return assignedTo;
    }
    public String getTaskDesc() {
        return taskDesc;
    }
    public Date getDateOfCreated() {
        return dateOfCreated;
    }

    @Override public List<AbstractParty> getParticipants() {
        return Arrays.asList(assignedBy, assignedTo);
    }
}