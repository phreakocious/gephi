package net.phreakocious.httpgraph.layout;

import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.layout.spi.LayoutBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author phreakocious
 */
//@ServiceProvider(service=LayoutBuilder.class)
public class HGForceAtlas2 extends ForceAtlas2Builder {

    @Override
    public ForceAtlas2 buildLayout() {
        
        ForceAtlas2 fal = new ForceAtlas2(this);        
        fal.resetPropertiesValues();
                
        return fal;
    }
}