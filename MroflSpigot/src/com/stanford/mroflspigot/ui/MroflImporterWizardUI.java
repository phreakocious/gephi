/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stanford.mroflspigot.ui;

import com.stanford.mroflspigot.MroflSpigot;
import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.ImporterWizardUI;
import org.gephi.io.importer.spi.SpigotImporter;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Sébastien Heymann
 */
@ServiceProvider(service = ImporterWizardUI.class)
public class MroflImporterWizardUI implements ImporterWizardUI {

    private Panel[] panels = null;
    
    @Override
    public String getDisplayName() {
        return "Network of people by relationships";
    }

    @Override
    public String getCategory() {
        return "Republic of Letters";
    }

    @Override
    public String getDescription() {
        return "Retreive networks from the database Mapping Republic of Letters.";
    }

    @Override
    public Panel[] getPanels() {
        if (panels == null) {
            panels = new Panel[1];
            panels[0] = new MroflSpigotWizardPanel1();
        }
        return panels;
    }

    @Override
    public void setup(Panel panel) {
        //Before opening the wizard
    }

    @Override
    public void unsetup(SpigotImporter importer, Panel panel) {
        //When the wizard has been closed
        ((MroflSpigotWizardSwing1) ((Panel) panels[0]).getComponent()).unsetup((MroflSpigot)importer);
 
        panels = null;
    }

    @Override
    public boolean isUIForImporter(Importer importer) {
        return importer instanceof MroflSpigot;
    }
    
}
