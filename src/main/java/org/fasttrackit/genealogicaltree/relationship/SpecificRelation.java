package org.fasttrackit.genealogicaltree.relationship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@RequiredArgsConstructor
public enum SpecificRelation implements Relation {
    HUSBAND(true),
    WIFE(false),

    FATHER(true),
    MOTHER(false),

    SON(true),
    DAUGHTER(false),

    GRANDFATHER(true),
    GRANDMOTHER(false),

    GRANDSON(true),
    GRANDDAUGHTER(false),

    BROTHER(true),
    SISTER(false),

    UNCLE(true),
    AUNT(false),

    NEPHEW(true),
    NIECE(false),

    COUSIN(null);

    @Getter
    @Accessors(fluent = true)
    private final Boolean isRelationMale;

    @Getter
    @Setter
    private GenericRelation genericRelation;
    private boolean relationMale;

    SpecificRelation(boolean b) {

        isRelationMale = null;
    }

    SpecificRelation(Boolean aBoolean) {

        isRelationMale = null;
    }

    public GenericRelation getGenericRelation() {
        return genericRelation;
    }

    public void setGenericRelation(Object genericRelation) {
        this.genericRelation = (GenericRelation) genericRelation;
    }

    public boolean isRelationMale() {
        return relationMale;
    }

    public void setRelationMale(boolean relationMale) {
        this.relationMale = relationMale;
    }

    public Boolean getRelationMale() {
        return isRelationMale;
    }
}
