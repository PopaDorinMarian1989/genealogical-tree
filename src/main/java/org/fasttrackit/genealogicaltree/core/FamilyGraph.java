package org.fasttrackit.genealogicaltree.core;


import lombok.RequiredArgsConstructor;
import org.fasttrackit.genealogicaltree.relationship.GenericRelation;
import org.fasttrackit.genealogicaltree.relationship.Relation;
import org.fasttrackit.genealogicaltree.relationship.SpecificRelation;
import org.fasttrackit.genealogicaltree.validation.Validator;
import org.springframework.lang.NonNull;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.fasttrackit.genealogicaltree.utils.FilterUtils.filterConnectionsByGenerationLevel;
import static org.fasttrackit.genealogicaltree.utils.RelationUtils.parseToGenericRelation;

@RequiredArgsConstructor
public class FamilyGraph {
    private final Map<String, Person> mPersonIdMap = new HashMap<>();
    private final Map<Person, Set<ConnectionEdge>> mRelationMap = new HashMap<>();

    private final Validator mValidator;
    private GenericRelation reverseRelation;
    private Boolean isRelationMale;
    private int i;
    private Collection<ConnectionEdge> allConnectionsInFamilyForPerson;
    private Person p1;
    private Object genericRelation;
    private SpecificRelation specificRelation;
    private Person p2;
    private int relationLevel;
    private boolean doValidate;

    public FamilyGraph(@NonNull Validator mValidator) {
        this.mValidator = mValidator;
    }


    public Set<ConnectionEdge> getAllNeighbourConnections(Person person) {
        return mRelationMap.get(person);
    }


    public void addPerson(Person person) {
        if (!mRelationMap.containsKey(person)) {
            mPersonIdMap.put(person.getId(), person);
            mRelationMap.put(person, new HashSet<>());
        }
    }


    public void addPerson(String id, String name, String age, String isMale) {
        this.addPerson(new Person(id, name, age, isMale));
    }


    public void connectPersons(String p1Id, String relation, String p2Id) {
        Person p1 = mPersonIdMap.get(p1Id);
        Person p2 = mPersonIdMap.get(p2Id);

        if (p1 == null) {
            throw new IllegalArgumentException("Person with Id: " + p1Id + " not found in family to connect");
        }
        if (p2 == null) {
            throw new IllegalArgumentException("Person with Id: " + p2Id + " not found in family to connect");
        }

        GenericRelation GenericRelation;
        GenericRelation = parseToGenericRelation(relation);
        connectPersons(p1, GenericRelation, p2, GenericRelation.getRelationLevel());
    }

    private void connectPersons(Person p1, GenericRelation genericRelation, Person p2, Object relationLevel) {

    }


    private void connectPersons(Person p1, GenericRelation GenericRelation, Person p2, int relationLevel, boolean
            doValidate) {
        addPerson(p1);
        addPerson(p2);
        if (doValidate && !mValidator.validate(p1, GenericRelation, p2, relationLevel, this)) {
            throw new IllegalArgumentException(new ConnectionEdge(p1, GenericRelation, p2) + " is NOT a valid Relation");
        }
        mRelationMap.get(p1).add(new ConnectionEdge(p1, GenericRelation, p2, relationLevel));
        mRelationMap.get(p2).add(new ConnectionEdge(p2, GenericRelation.getReverseRelation(), p1, -relationLevel));
    }


    public void connectPersons(Person p1, SpecificRelation SpecificRelation, Person p2, int relationLevel, boolean
            doValidate) {
        this.p1 = p1;
        specificRelation = SpecificRelation;
        this.p2 = p2;
        this.relationLevel = relationLevel;
        this.doValidate = doValidate;
        this.connectPersons(p1, SpecificRelation.getGenericRelation(), p2, relationLevel, doValidate);
    }

    private void connectPersons(Person p1, Object genericRelation, Person p2, int relationLevel, boolean doValidate) {

        this.p1 = p1;
        this.genericRelation = genericRelation;
        this.p2 = p2;
        this.relationLevel = relationLevel;
        this.doValidate = doValidate;
    }


    public void batchConnectPersons(Set<ConnectionEdge> connections) {
        connections.forEach(// No need of validation as the connections are already validated while initially connecting.
                this::accept);
    }


    public void removeDirectConnection(Person p1, Person p2) {
        if (!arePersonsDirectlyConnected(p1, p2)) {
            throw new IllegalArgumentException(p1 + " is NOT directly connected to " + p2);
        }
        for (Iterator<ConnectionEdge> iterator = getAllNeighbourConnections(p1).iterator(); iterator.hasNext(); ) {
            ConnectionEdge connection = iterator.next();
            if (connection.to() == p2) {
                iterator.remove();
                return;
            }
        }
    }


    public boolean arePersonsDirectlyConnected(Person p1, Person p2) {
        for (ConnectionEdge connection : getAllNeighbourConnections(p1)) {
            if (p2.equals(connection.to()))
                return true;
        }
        return false;
    }


    public Person getPersonById(String pId) {
        Person person = mPersonIdMap.get(pId);
        if (person == null) {
            throw new IllegalArgumentException("Person Id: " + pId + " NOT present in family");
        }
        return person;
    }


    public Collection<Person> getAllPersonsInFamily() {
        return mPersonIdMap.values();
    }


    public ConnectionEdge getConnection(Person p1, Person p2, boolean doBatchConnect) {
        ConnectionEdge connection = bfsTraverseFamilyGraph(p1, p2, null, doBatchConnect);

        assert connection.to() != null;
        return connection.to().equals(p2) ? connection : null;
    }


    public Collection<ConnectionEdge> getAllConnectionsInFamilyForPerson(Person person,
                                                                         boolean makeNewConnectionsFoundDuringSearch) {
        Set<ConnectionEdge> connectionsToPopulate = new HashSet<>();
        bfsTraverseFamilyGraph(person, null, connectionsToPopulate, makeNewConnectionsFoundDuringSearch);
        return connectionsToPopulate;
    }


    private ConnectionEdge bfsTraverseFamilyGraph(Person p1, Person p2, Set<ConnectionEdge> connectionsToPopulate,
                                                  boolean makeNewConnectionsFoundDuringSearch) {
        if (p1 == null || mPersonIdMap.get(p1.getId()) == null) {
            throw new IllegalArgumentException("Person " + p1 + " not found in family");
        }
        if (p2 != null && mPersonIdMap.get(p2.getId()) == null) {
            throw new IllegalArgumentException("Person " + p2 + " not found in family");
        }

        Queue<Person> queue = new LinkedList<>();
        Map<Person, Boolean> visited = new HashMap<>();
        Map<Person, ConnectionEdge> relationMap = new HashMap<>();
        ConnectionEdge previousConnection = null;
        Person neighbourRelative;
        GenericRelation currentRelation, nextRelation;

        boolean isGettingFamilyGraphForPerson = (p2 == null);
        if (isGettingFamilyGraphForPerson && connectionsToPopulate == null) {
            connectionsToPopulate = new HashSet<>();
        }

        queue.add(p1);
        visited.put(p1, true);
        loop:
        while (!queue.isEmpty()) {
            Person p = queue.poll();
            for (ConnectionEdge edge : getAllNeighbourConnections(p)) {
                if (visited.get(edge.to()) == null) {
                    neighbourRelative = edge.to();
                    previousConnection = relationMap.get(edge.from());
                    if (previousConnection == null) {
                        previousConnection = edge;
                    } else {
                        currentRelation = edge.relation();
                        nextRelation = currentRelation.getNextGenericRelation(previousConnection.relation());
                        previousConnection = new ConnectionEdge(p1,
                                nextRelation,
                                neighbourRelative,
                                previousConnection.relationLevel() + currentRelation.getRelationLevel());
                    }

                    if (isGettingFamilyGraphForPerson) {
                        connectionsToPopulate.add(previousConnection);
                    } else if (neighbourRelative.equals(p2)) {
                        break loop;
                    }
                    relationMap.put(neighbourRelative, previousConnection);
                    queue.add(neighbourRelative);
                    visited.put(neighbourRelative, true);
                }
            }
        }
        if (makeNewConnectionsFoundDuringSearch) {

            batchConnectPersons(connectionsToPopulate);
        }
        return previousConnection;
    }


    private Map<Person, ConnectionEdge> getConnectionPath(Person p1, Person p2) {
        if (p1 == null || mPersonIdMap.get(p1.getId()) == null) {
            throw new IllegalArgumentException("Person " + p1 + " not found in family");
        }
        if (p2 == null || mPersonIdMap.get(p2.getId()) == null) {
            throw new IllegalArgumentException("Person " + p2 + " not found in family");
        }
        HashMap<Person, ConnectionEdge> connectionPathMap = new HashMap<Person, ConnectionEdge>();
        LinkedList<Person> queue = new LinkedList<Person>();
        HashMap<Person, Boolean> visited = new HashMap<Person, Boolean>();

        queue.add(p1);
        visited.put(p1, true);
        while (!queue.isEmpty()) {
            Person p = queue.poll();
            for (ConnectionEdge edge : getAllNeighbourConnections(p)) {
                if (visited.get(edge.to()) == null) {
                    Person neighbourRelative = edge.to();
                    connectionPathMap.put(neighbourRelative, edge);
                    if (neighbourRelative.equals(p2)) {
                        break;
                    }
                    queue.add(neighbourRelative);
                    visited.put(p, true);
                }
            }
        }
        return connectionPathMap;
    }

    public List<ConnectionEdge> getShortestRelationChain(Person p1, Person p2) {
        List<ConnectionEdge> connections = new ArrayList<>();
        getAggregateRelationWithRelationChain(p1, p2, getConnectionPath(p1, p2), connections);
        return connections;
    }

    public ConnectionEdge getAggregateConnection(Person p1, Person p2) {
        return getAggregateRelationWithRelationChain(p1, p2, getConnectionPath(p1, p2), null);
    }


    private ConnectionEdge getAggregateRelationWithRelationChain(Person p1,
                                                                 Person p2,
                                                                 Map<Person, ConnectionEdge> connectionPath,
                                                                 List<ConnectionEdge> connections) {
        ConnectionEdge nextEdge, aggregateConnection = null;
        GenericRelation nextRelation, aggregateRelation = null;
        Person nextPerson = p2;

        while (!nextPerson.equals(p1)) {
            nextEdge = connectionPath.get(nextPerson);
            nextPerson = nextEdge.from();
            nextRelation = nextEdge.relation();
            if (aggregateRelation == null) {
                aggregateRelation = nextRelation;
                aggregateConnection = nextEdge;
            } else {
                aggregateRelation = aggregateRelation.getNextGenericRelation(nextRelation);
                aggregateConnection = new ConnectionEdge(nextPerson,
                        aggregateRelation,
                        p2,
                        requireNonNull(nextRelation).getRelationLevel() + aggregateConnection.relationLevel());
            }
            if (connections != null) {
                connections.add(nextEdge);
            }
        }
        if (connections != null) {
            Collections.reverse(connections);
        }
        return aggregateConnection;
    }

    public Collection<ConnectionEdge> getAllMembersFromGenerationLevel(Person person, int generationLevel) {
        return filterConnectionsByGenerationLevel(person, -generationLevel, getAllConnectionsInFamilyForPerson(person,
                false));
    }

    public Collection<Person> getFamilyInOrderOfAge(boolean isOrderAscending) {
        Comparator<Person> ascendingAgeComparator = (p1, p2) -> {
            if (p1.getAge() == p2.getAge()) return 0;
            return (p1.getAge() > p2.getAge()) ? 1 : -1;
        };
        ArrayList sortedPersons = new ArrayList(mPersonIdMap.values());
        if (isOrderAscending) {
            sortedPersons.sort(ascendingAgeComparator);
        } else {
            sortedPersons.sort(Collections.reverseOrder(ascendingAgeComparator));
        }
        return sortedPersons;
    }

    public Collection<Person> getAllFamilyMembersOfGender(Boolean isMale) {
        return filterPersonsByGender(isMale, new ArrayList<>(mPersonIdMap.values()));
    }

    private Collection<Person> filterPersonsByGender(Boolean isMale, ArrayList<Person> people) {
        return null;
    }


    public Collection<Person> getAllPersonsByRelation(Person person, Relation relation, int relationLevel) {
        if (relation instanceof GenericRelation) {
            return this.getAllPersonsByRelation(person, (GenericRelation) relation, relationLevel);
        } else {
            return this.getAllPersonsByRelation(person, (SpecificRelation) relation, relationLevel);
        }
    }

    private Collection<Person> getAllPersonsByRelation(Person person, GenericRelation genericRelation, int relationLevel) {
        return this.getAllPersonsByRelation(person, genericRelation, null, relationLevel);
    }


    public Collection<Person> getAllPersonsByRelation(Person person, SpecificRelation specificRelation, int
            relationLevel) {
        return this.getAllPersonsByRelation(person, specificRelation.getGenericRelation(), specificRelation
                .isRelationMale(), relationLevel);
    }

    private Collection<Person> getAllPersonsByRelation(Person person, GenericRelation genericRelation,
                                                       Boolean isRelationMale, int relationLevel) {
        GenericRelation reverseRelation = genericRelation.getReverseRelation();
        Collection<Person> list = new ArrayList<>();
        for (ConnectionEdge connectionEdge : getAllConnectionsInFamilyForPerson(person, false)) {
            Person to = connectionEdge.to();
            boolean add = list.add(to);

        }
        return list;
    }

    /**
     * @param reverseRelation
     * @param isRelationMale
     * @param i
     * @param allConnectionsInFamilyForPerson
     */
    @org.jetbrains.annotations.Contract(pure = true)
    private void filterConnectionsBySpecificRelation(GenericRelation reverseRelation, Boolean isRelationMale, int i, Collection<ConnectionEdge> allConnectionsInFamilyForPerson) {


        this.reverseRelation = reverseRelation;
        this.isRelationMale = isRelationMale;
        this.i = i;
        this.allConnectionsInFamilyForPerson = allConnectionsInFamilyForPerson;
    }


    public boolean isPersonRelatedWithRelation(Person person, Relation relation, int relationLevel) {
        if (relation instanceof GenericRelation) {
            return this.isPersonRelatedWithRelation(person, (GenericRelation) relation, relationLevel);
        } else {
            return this.isPersonRelatedWithRelation(person, (SpecificRelation) relation, relationLevel);
        }
    }

    private boolean isPersonRelatedWithRelation(Person person, SpecificRelation specificRelation, int relationLevel) {
        return this.isPersonRelatedWithRelation(person, specificRelation.getGenericRelation(),
                specificRelation.isRelationMale(), relationLevel, getAllNeighbourConnections(person))
                || this.isPersonRelatedWithRelation(person, specificRelation.getGenericRelation(),
                specificRelation.isRelationMale(), relationLevel, getAllConnectionsInFamilyForPerson(person, false));
    }

    public boolean isPersonRelatedWithRelation(Person person,
                                               int relationLevel) {
        return this.isPersonRelatedWithRelation(person, (GenericRelation) genericRelation, null, relationLevel,
                getAllNeighbourConnections(person))
                || this.isPersonRelatedWithRelation(person, (GenericRelation) genericRelation, null, relationLevel,
                getAllConnectionsInFamilyForPerson(person, false));
    }

    private boolean isPersonRelatedWithRelation(Person person, GenericRelation genericRelation,
                                                Boolean isRelationMale, int relationLevel, Collection<ConnectionEdge> allConnections) {
        if (isRelationMale != null && person.isGenderMale() != isRelationMale) {
            return false;
        }
        for (ConnectionEdge connection : allConnections) {
            assert connection.relation() != null;
            if (connection.relation().equals(genericRelation))
                if (connection.relationLevel() == relationLevel) {
                    return true;
                }
        }
        return false;
    }

    private void accept(ConnectionEdge connection) {
        Person from = connection.from();
        Person to = connection.to();
        if (!arePersonsDirectlyConnected(from, to)) {
            connectPersons(from, connection.relation(), to, connection.relationLevel(), false);
        }
    }

    private void connectPersons(Person from, GenericRelation relation, Person to, Object relationLevel, boolean b) {

    }

    public Collection<ConnectionEdge> getAllConnectionsInFamilyForPerson() {
        return allConnectionsInFamilyForPerson;
    }

    public void setAllConnectionsInFamilyForPerson(Collection<ConnectionEdge> allConnectionsInFamilyForPerson) {
        this.allConnectionsInFamilyForPerson = allConnectionsInFamilyForPerson;
    }

    public SpecificRelation getSpecificRelation() {
        return specificRelation;
    }

    public void setSpecificRelation(SpecificRelation specificRelation) {
        this.specificRelation = specificRelation;
    }

    public Object getGenericRelation() {
        return genericRelation;
    }

    public void setGenericRelation(Object genericRelation) {
        this.genericRelation = genericRelation;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public Boolean getRelationMale() {
        return isRelationMale;
    }

    public void setRelationMale(Boolean relationMale) {
        isRelationMale = relationMale;
    }

    public GenericRelation getReverseRelation() {
        return reverseRelation;
    }

    public void setReverseRelation(GenericRelation reverseRelation) {
        this.reverseRelation = reverseRelation;
    }

    public Person getP1() {
        return p1;
    }

    public void setP1(Person p1) {
        this.p1 = p1;
    }

    public Person getP2() {
        return p2;
    }

    public void setP2(Person p2) {
        this.p2 = p2;
    }

    public int getRelationLevel() {
        return relationLevel;
    }

    public void setRelationLevel(int relationLevel) {
        this.relationLevel = relationLevel;
    }

    public boolean isDoValidate() {
        return doValidate;
    }

    public void setDoValidate(boolean doValidate) {
        this.doValidate = doValidate;
    }
}


