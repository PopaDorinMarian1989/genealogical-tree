package org.fasttrackit.genealogicaltree.printer;

import com.google.inject.Inject;
import lombok.Cleanup;
import lombok.var;
import org.fasttrackit.genealogicaltree.core.FamilyGraph;
import org.fasttrackit.genealogicaltree.core.Person;
import org.fasttrackit.genealogicaltree.relationship.GenericRelation;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;


public class ConsolePrintService implements PrintService {
    @Inject
    private OutputStream out;

    @Override
    public void printFamilyTree(String pId, FamilyGraph family) {
        printCollection(family.getAllConnectionsInFamilyForPerson(family.getPersonById(pId), false));
    }

    @Override
    public void printShortestRelationChain(String p1Id, String p2Id, FamilyGraph family) {
        Person p1 = family.getPersonById(p1Id);
        Person p2 = family.getPersonById(p2Id);
        printCollection(family.getShortestRelationChain(p1, p2));
    }

    @Override
    public void printAggregateRelation(String p1Id, String p2Id, FamilyGraph family) {
        @Cleanup PrintStream printOut = new PrintStream(out);
        Person p1 = family.getPersonById(p1Id);
        Person p2 = family.getPersonById(p2Id);
        printOut.println(family.getAggregateConnection(p1, p2));
    }

    @Override
    public void printAllMembersFromGenerationLevel(String pId, int generationLevel, FamilyGraph family) {
        printCollection(family.getAllMembersFromGenerationLevel(family.getPersonById(pId), generationLevel));
    }

    @Override
    public void printAllPersonsInFamily(FamilyGraph family) {
        printCollection(family.getAllPersonsInFamily());
    }

    @Override
    public void printFamilyInAscendingOrderOfAge(FamilyGraph family) {
        printCollection(family.getFamilyInOrderOfAge(true));
    }

    @Override
    public void printFamilyInDescendingOrderOfAge(FamilyGraph family) {
        printCollection(family.getFamilyInOrderOfAge(false));
    }

    @Override
    public void printAllMaleFamilyMembers(FamilyGraph family) {
        printCollection(family.getAllFamilyMembersOfGender(true));
    }

    @Override
    public void printAllFeMaleFamilyMembers(FamilyGraph family) {
        printCollection(family.getAllFamilyMembersOfGender(false));
    }

    @Override
    public void printPersonsByRelation(String pId, String relation, int relationLevel, FamilyGraph family) {
        var iRelation = parseToRelation(relation);
        printCollection(family.getAllPersonsByRelation(family.getPersonById(pId), (GenericRelation) iRelation, relationLevel));
    }

    private var parseToRelation(String relation) {
        return null;
    }

    @Override
    public void printPersonsRelatedWithRelation(String relation, int relationLevel, FamilyGraph family) {
        @Cleanup PrintStream printOut = new PrintStream(out);
        family.getAllPersonsInFamily().stream().filter(person -> family.isPersonRelatedWithRelation(person,
                parseToRelation(relation), relationLevel)).forEach(printOut::println);
    }

    private <T> void printCollection(Collection<T> collection) {
        @Cleanup PrintStream printOut = new PrintStream(out);
        collection.forEach(printOut::println);
    }
}

