/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stanford.mroflspigot;

import org.gephi.io.importer.spi.SpigotImporter;
import org.gephi.io.importer.spi.SpigotImporterBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Sébastien Heymann
 */
@ServiceProvider(service = SpigotImporterBuilder.class)
public class MroflSpigotBuilder implements SpigotImporterBuilder {

    @Override
    public SpigotImporter buildImporter() {
        return new MroflSpigot();
    }

    @Override
    public String getName() {
        return("Republic of Letters Spigot");
    }
    
}
