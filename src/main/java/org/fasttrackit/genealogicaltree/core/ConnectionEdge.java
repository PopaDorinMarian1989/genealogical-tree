package org.fasttrackit.genealogicaltree.core;


import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.var;
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
    @NonNull
    private Person to;
    private int relationLevel;

    public ConnectionEdge(Person from, SpecificRelation relation, Person to) {
        this(from, relation.getGenericRelation(), to);
    }

    public ConnectionEdge(Person from, GenericRelation relation, Person to, int relationLevel) {
        this(from, relation, to, relation.getRelationLevel());
    }

    /**
     * This Constructor mainly defined to ease Unit testing
     *
     * @param fromPid
     * @param relation
     * @param toPid
     * @param relationLevel
     * @param family
     */
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
        StringBuilder grandPrefix;
        grandPrefix = new grandPrefix();
        while (relationLevel-- > 2) {
            grandPrefix.append("GREAT ");
        }
        return grandPrefix;
    }


    public Person from() {
        return null;
    }

    public Person to() {
        return null;
    }

    public GenericRelation relation() {
        return null;
    }

    public byte relationLevel() {
        return 0;
    }
}

