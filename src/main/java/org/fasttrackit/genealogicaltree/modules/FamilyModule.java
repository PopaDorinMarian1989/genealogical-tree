package org.fasttrackit.genealogicaltree.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.fasttrackit.genealogicaltree.client.FamilyNetworkClient;
import org.fasttrackit.genealogicaltree.loader.FileLoaderService;
import org.fasttrackit.genealogicaltree.loader.LoaderService;
import org.fasttrackit.genealogicaltree.printer.ConsolePrintService;
import org.fasttrackit.genealogicaltree.printer.PrintService;
import org.fasttrackit.genealogicaltree.validation.AgeValidator;
import org.fasttrackit.genealogicaltree.validation.GenderValidator;
import org.fasttrackit.genealogicaltree.validation.RelationshipValidator;
import org.fasttrackit.genealogicaltree.validation.Validator;

import java.io.OutputStream;


public class FamilyModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(FamilyNetworkClient.class);
        bind(String.class).annotatedWith(Names.named("family-file")).toInstance("src//main//resources//MyFamily.txt");
        bind(LoaderService.class).to(FileLoaderService.class);
        bind(PrintService.class).to(ConsolePrintService.class);
        bind(OutputStream.class).toInstance(System.out);
    }

    @Provides
    public Validator provideValidator() {
        Validator genderValidator = new GenderValidator();
        Validator ageValidator = new AgeValidator();
        Validator relationShipValidator = new RelationshipValidator();

        genderValidator.setNextValidatorInChain(ageValidator);
        ageValidator.setNextValidatorInChain(relationShipValidator);

        return genderValidator;
    }
}
