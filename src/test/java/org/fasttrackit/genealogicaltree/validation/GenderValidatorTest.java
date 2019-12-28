package org.fasttrackit.genealogicaltree.validation;

import org.fasttrackit.genealogicaltree.core.Person;
import org.fasttrackit.genealogicaltree.relationship.GenericRelation;
import org.fasttrackit.genealogicaltree.relationship.SpecificRelation;
import org.fasttrackit.genealogicaltree.software.SoftwareTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GenderValidatorTest extends SoftwareTest {

    @BeforeEach
    void init() {
        validator = new GenderValidator();
    }

    @Test
    void validate() {
        Person p1 = family.getPersonById("1");
        Person p2 = family.getPersonById("8");
        assertTrue(validator.validate(p1, GenericRelation.SPOUSE, p2, 0, null));
    }

    @Test
    void validate1() {
        Person p1 = family.getPersonById("2");
        Person p2 = family.getPersonById("8");
        assertFalse(validator.validate(p1, GenericRelation.SPOUSE, p2, 0, null));
    }

    @Test
    void validate2() {
        Person p1 = family.getPersonById("1");
        Person p2 = family.getPersonById("8");
        assertTrue(validator.validate(p1, SpecificRelation.WIFE, p2, 0, null));
    }

    @Test
    void validate3() {
        Person p1 = family.getPersonById("1");
        Person p2 = family.getPersonById("2");
        assertFalse(validator.validate(p1, SpecificRelation.BROTHER, p2, 0, null));
    }

    @Test
    void validate4() {
        Person p1 = family.getPersonById("1");
        Person p2 = family.getPersonById("2");
        assertTrue(validator.validate(p1, SpecificRelation.SISTER, p2, 0, null));
    }
}

