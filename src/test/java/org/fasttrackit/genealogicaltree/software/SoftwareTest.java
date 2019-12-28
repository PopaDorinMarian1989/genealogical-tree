package org.fasttrackit.genealogicaltree.software;

import com.google.inject.Guice;
import com.google.inject.Inject;
import org.fasttrackit.genealogicaltree.core.FamilyGraph;
import org.fasttrackit.genealogicaltree.loader.LoaderService;
import org.fasttrackit.genealogicaltree.modules.FamilyModule;
import org.fasttrackit.genealogicaltree.validation.Validator;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class SoftwareTest {
    @Inject
    protected static Validator validator;
    @Inject
    protected static FamilyGraph family;
    @Inject
    protected static LoaderService loader;
    @Inject
    protected static ByteArrayOutputStream outContent;

    @BeforeEach
    void setUpAll() throws IOException {
        Guice.createInjector(new FamilyModule() {
            @Override
            protected void configure() {
                super.configure();
                requestStaticInjection(SoftwareTest.class);
            }
        });
        loader.loadFamily(family);
        System.setOut(new PrintStream(outContent));
    }
}
