package org.fasttrackit.genealogicaltree.core;


import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.fasttrackit.genealogicaltree.relationship.GenericRelation;
import org.fasttrackit.genealogicaltree.relationship.SpecificRelation;

import static org.fasttrackit.genealogicaltree.utils.RelationUtils.parseToGenericRelation;


@Value
@AllArgsConstructor
public final class ConnectionEdge {
    @NonNull
    private Person from;
    @NonNull
    private GenericRelation relation;

    public Person getFrom() {
        return from;
    }

    public void setFrom(Person from) {
        this.from = from;
    }

    public GenericRelation getRelation() {
        return relation;
    }

    public void setRelation(GenericRelation relation) {
        this.relation = relation;
    }

    public Person getTo() {
        return to;
    }

    public void setTo(Person to) {
        this.to = to;
    }

    public int getRelationLevel() {
        return relationLevel;
    }

    public void setRelationLevel(int relationLevel) {
        this.relationLevel = relationLevel;
    }

    @NonNull
    private Person to;
    private int relationLevel;

    public ConnectionEdge(Person from, SpecificRelation relation, Person to) {
        this(from, relation.getGenericRelation(), to);
    }

    public ConnectionEdge(Person from, GenericRelation relation, Person to, int relationLevel) {
        this(from, relation, to, relation.getRelationLevel());
    }


    public ConnectionEdge(String fromPid, String relation, String toPid, int relationLevel, FamilyGraph family) {
        this(family.getPersonById(fromPid), parseToGenericRelation(relation), family.getPersonById(toPid),
                relationLevel);
    }

    public ConnectionEdge(Person from, Object genericRelation, Person to) {

    }

    public ConnectionEdge(Person from, GenericRelation relation, Person to, Object relationLevel) {

    }

    @Override
    public String toString() {
        int absoluteRelationLevel = Math.abs(this.relationLevel);
        StringBuilder grandRelationPrefix = generateGrandRelationPrefix(absoluteRelationLevel);
        return String.format("%s is %s of %s RelationLevel: %d",
                this.from, grandRelationPrefix.append(this.relation.getGenderSpecificRelation(this.from.isGenderMale())),
                this.to, this.relationLevel);
    }

    private StringBuilder generateGrandRelationPrefix(int relationLevel) {
        StringBuilder grandPrefix = new StringBuilder();

        while (relationLevel-- > 2) {
            grandPrefix.append("GREAT ");
        }
        return grandPrefix;
    }


    public Person to() {
        return null;
    }

    public Person from() {
        return null;
    }

    public GenericRelation relation() {
        return null;
    }


    public int relationLevel() {
        return 0;
    }
}

