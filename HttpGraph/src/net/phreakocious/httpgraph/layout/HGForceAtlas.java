package net.phreakocious.httpgraph.layout;

import org.gephi.layout.plugin.forceAtlas.ForceAtlas;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.spi.LayoutBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author phreakocious
 */
@ServiceProvider(service=LayoutBuilder.class)
public class HGForceAtlas extends ForceAtlas {

    @Override
    public ForceAtlasLayout buildLayout() {
        
        ForceAtlasLayout fal = new ForceAtlasLayout(this);
        fal.resetPropertiesValues();
        fal.setRepulsionStrength((double) 600);
        
        return fal;
    }
}
