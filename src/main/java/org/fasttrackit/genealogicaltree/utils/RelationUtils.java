package org.fasttrackit.genealogicaltree.utils;

import org.fasttrackit.genealogicaltree.relationship.GenericRelation;
import org.fasttrackit.genealogicaltree.relationship.Relation;
import org.fasttrackit.genealogicaltree.relationship.SpecificRelation;


public interface RelationUtils {
    static GenericRelation parseToGenericRelation(String relation) {
        relation = relation.toUpperCase();
        try {
            return GenericRelation.valueOf(relation);
        } catch (IllegalArgumentException e) {
            return SpecificRelation.valueOf(relation).getGenericRelation();
        }
    }

    static Relation parseToRelation(String relation) {
        relation = relation.toUpperCase();
        try {
            return GenericRelation.valueOf(relation);
        } catch (IllegalArgumentException e) {
            return SpecificRelation.valueOf(relation);
        }
    }
}
