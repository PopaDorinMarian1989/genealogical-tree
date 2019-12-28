package org.fasttrackit.genealogicaltree.loader;

import com.google.inject.Inject;
import lombok.Cleanup;
import org.fasttrackit.genealogicaltree.core.FamilyGraph;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class FileLoaderService implements LoaderService {
    @Inject
    @Named("family-file")
    private String familyFile;

    @Override
    public void loadFamily(FamilyGraph family) throws IOException {
        @Cleanup BufferedReader reader = new BufferedReader(new FileReader(new File(familyFile)));
        load(family, reader, true);
        load(family, reader, false);
    }

    private void load(FamilyGraph family, BufferedReader reader, boolean isLoadingPersons) throws IOException {
        String line = reader.readLine();
        while ((line != null) && (line.length() > 0)) {
            String[] vals = line.split(",");
            trimVals(vals);
            if (isLoadingPersons) {
                family.addPerson(vals[0], vals[1], vals[2], vals[3]);
            } else {
                family.connectPersons(vals[0], vals[1], vals[2]);
            }
            line = reader.readLine();
        }
    }

    private void trimVals(String[] vals) {
        int len = vals.length;
        for (int i = 0; i < len; i++) {
            vals[i] = vals[i].trim();
        }
    }
}

