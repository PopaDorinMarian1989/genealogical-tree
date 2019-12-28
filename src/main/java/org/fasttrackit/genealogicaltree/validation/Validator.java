package org.fasttrackit.genealogicaltree.validation;

import org.fasttrackit.genealogicaltree.core.FamilyGraph;
import org.fasttrackit.genealogicaltree.core.Person;
import org.fasttrackit.genealogicaltree.relationship.GenericRelation;
import org.fasttrackit.genealogicaltree.relationship.SpecificRelation;


public interface Validator {
    void setNextValidatorInChain(Validator validator);

    boolean validate(Person p1, GenericRelation genericRelation, Person p2, int relationLevel, FamilyGraph family);

    boolean validate(Person p1, SpecificRelation specificRelation, Person p2, int relationLevel, FamilyGraph family);
}
