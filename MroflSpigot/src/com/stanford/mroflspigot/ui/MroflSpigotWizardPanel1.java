/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stanford.mroflspigot.ui;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Sébastien Heymann
 */
public class MroflSpigotWizardPanel1 implements WizardDescriptor.Panel {

    private Component component;
    
    @Override
    public Component getComponent() {
        if (component == null) {
            component = new MroflSpigotWizardSwing1();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(Object data) {
    }

    @Override
    public void storeSettings(Object data) {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }
    
}
