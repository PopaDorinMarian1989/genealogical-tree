package org.fasttrackit.genealogicaltree.loader;

import org.fasttrackit.genealogicaltree.core.FamilyGraph;

import java.io.IOException;

public interface LoaderService {

    void loadFamily(FamilyGraph family) throws IOException;
}

