/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stanford.mroflspigot.ui;

import com.stanford.mroflspigot.MroflSpigot;
import javax.swing.Icon;
import org.gephi.datalab.spi.ContextMenuItemManipulator;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.nodes.NodesManipulator;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.api.LongTaskErrorHandler;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.gephi.visualization.spi.GraphContextMenuItem;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Sébastien Heymann
 */
@ServiceProvider(service = GraphContextMenuItem.class)
public class ExpandNode implements GraphContextMenuItem, NodesManipulator {

    private Node node;

    @Override
    public void execute() {
        if (node != null) {
            final LongTaskExecutor longTaskExecutor = new LongTaskExecutor(true, "Spigot");
            final LongTaskErrorHandler errorHandler = new LongTaskErrorHandler() {

                @Override
                public void fatalError(Throwable t) {
                    if (t instanceof OutOfMemoryError) {
                        return;
                    }
                    t.printStackTrace();
                    String message = t.getCause().getMessage();
                    if (message == null || message.isEmpty()) {
                        message = t.getMessage();
                    }
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE);
                    DialogDisplayer.getDefault().notify(msg);
                    //Logger.getLogger("").log(Level.WARNING, "", t.getCause());
                }
            };
            
            /*LongTaskListener longTaskListener = new LongTaskListener() {
            
            @Override
            public void taskFinished(LongTask task) {
            }
            };
            longTaskExecutor.setLongTaskListener(longTaskListener);*/
            
            Task task = new Task(node);
            longTaskExecutor.execute(task, task, "Get MROFL data...", errorHandler);

        }
    }

    private static class Task implements LongTask, Runnable {

        private Node node;
        private ProgressTicket progressTicket;

        public Task(Node node) {
            this.node = node;
        }

        @Override
        public void run() {
            MroflSpigot importer = new MroflSpigot();
            importer.setDepth(1);
            importer.setPersonID(node.getNodeData().getId());
            importer.setProgressTicket(progressTicket);

            try {
                final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
                final Workspace workspace = pc.getCurrentWorkspace();

                //Create container
                final Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();

                //Fill in the container
                importer.execute(container.getLoader());

                //Append container to graph structure
                ImportController importController = Lookup.getDefault().lookup(ImportController.class);
                importController.process(container, new AppendProcessor(), workspace);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean cancel() {
            return true;
        }

        @Override
        public void setProgressTicket(ProgressTicket progressTicket) {
            this.progressTicket = progressTicket;
        }
    }

    @Override
    public void setup(Node[] nodes, Node clickedNode) {
        setup(null, nodes);
    }

    @Override
    public void setup(HierarchicalGraph graph, Node[] nodes) {
        if (nodes.length == 1) {
            node = nodes[0];
        } else {
            node = null;
        }
    }

    @Override
    public ContextMenuItemManipulator[] getSubItems() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return node != null;
    }

    @Override
    public Integer getMnemonicKey() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ExpandNode.class, "GraphContextMenu_ExpandNode");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ExpandNode.class, "GraphContextMenu_ExpandNode.description");
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public ManipulatorUI getUI() {
        return null;
    }

    @Override
    public int getType() {
        return 400;
    }

    @Override
    public int getPosition() {
        return 300;
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
