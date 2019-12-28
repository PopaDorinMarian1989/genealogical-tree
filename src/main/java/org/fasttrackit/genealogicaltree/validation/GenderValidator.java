package org.fasttrackit.genealogicaltree.validation;

import org.fasttrackit.genealogicaltree.core.FamilyGraph;
import org.fasttrackit.genealogicaltree.core.Person;
import org.fasttrackit.genealogicaltree.relationship.GenericRelation;
import org.fasttrackit.genealogicaltree.relationship.SpecificRelation;


public class GenderValidator implements Validator {
    private Validator nextValidator;

    @Override
    public void setNextValidatorInChain(Validator validator) {
        this.nextValidator = validator;
    }

    @Override
    public boolean validate(Person p1, GenericRelation genericRelation, Person p2, int relationLevel, FamilyGraph family) {
        boolean isValid;
        if (genericRelation == GenericRelation.SPOUSE) {
            isValid = (p1.isGenderMale() != p2.isGenderMale());
        } else {
            isValid = true;
        }
        return (nextValidator == null) ? isValid : isValid && nextValidator.validate(p1, genericRelation, p2, relationLevel,
                family);
    }

    @Override
    public boolean validate(Person p1, SpecificRelation specificRelation, Person p2, int relationLevel, FamilyGraph family) {
        boolean isValid = (specificRelation.isRelationMale() == p1.isGenderMale());

        switch (specificRelation) {
            case HUSBAND:
            case WIFE:
                isValid &= (p1.isGenderMale() != p2.isGenderMale());
        }
        return (nextValidator == null) ? isValid : isValid && nextValidator.validate(p1, specificRelation, p2,
                relationLevel, family);
    }
}

