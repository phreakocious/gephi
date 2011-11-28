package net.phreakocious.httpgraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.EdgeDraft.EdgeType;
import org.gephi.io.importer.api.EdgeDraftGetter;
import org.gephi.io.importer.api.NodeDraftGetter;
import org.gephi.io.processor.plugin.AbstractProcessor;
import org.gephi.io.processor.spi.Processor;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author phreakocious
 */
//@ServiceProvider(service = Processor.class)
public class HGAppendProcessor extends AbstractProcessor implements Processor {

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(HGAppendProcessor.class, "HGAppendProcessor.displayName");
    }

    @Override
    public void process() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        //Workspace
        if (workspace == null) {
            workspace = pc.getCurrentWorkspace();
            if (workspace == null) {
                //Append mode but no workspace
                workspace = pc.newWorkspace(pc.getCurrentProject());
                pc.openWorkspace(workspace);
            }
        }
        if (container.getSource() != null) {
            pc.setSource(workspace, container.getSource());
        }

        //Architecture
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        HierarchicalGraph graph = null;
        switch (container.getEdgeDefault()) {
            case DIRECTED:
                graph = graphModel.getHierarchicalDirectedGraph();
                break;
            case UNDIRECTED:
                graph = graphModel.getHierarchicalUndirectedGraph();
                break;
            case MIXED:
                graph = graphModel.getHierarchicalMixedGraph();
                break;
            default:
                graph = graphModel.getHierarchicalMixedGraph();
                break;
        }
        GraphFactory factory = graphModel.factory();

        //Attributes - Creates columns for properties
        attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        attributeModel.mergeModel(container.getAttributeModel());

        //Dynamic
        if (container.getTimeFormat() != null) {
            DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
            dynamicController.setTimeFormat(container.getTimeFormat());
        }

        //Index existing graph
        Map<String, Node> map = new HashMap<String, Node>();
        for (Node n : graph.getNodes()) {
            String id = n.getNodeData().getId();
            if (id != null && !id.equalsIgnoreCase(String.valueOf(n.getId()))) {
                map.put(id, n);
            }
            if (n.getNodeData().getLabel() != null && !n.getNodeData().getLabel().isEmpty()) {
                map.put(n.getNodeData().getLabel(), n);
            }
        }

        int nodeCount = 0;
        //Create all nodes
        for (NodeDraftGetter draftNode : container.getNodes()) {
            Node n;
            String id = draftNode.getId();
            String label = draftNode.getLabel();
            if (!draftNode.isAutoId() && id != null && map.get(id) != null) {
                n = map.get(id);

                // If a node is being re-added, make sure that it doesn't move (tb)                
                draftNode.setX(n.getNodeData().x());
                draftNode.setY(n.getNodeData().y());
                draftNode.setZ(n.getNodeData().z());

            } else if (label != null && map.get(label) != null) {
                n = map.get(label);
            } else {
                n = factory.newNode(draftNode.isAutoId() ? null : id);

                // If a node's "parent" node exists, place the new one 
                // it in the same spot for aesthetics (tb)
                                
                Object parent = draftNode.getAttributeRow().getValue("domain");
                if (parent != null) {

                    Node p = map.get(parent.toString());

                    if (p != null) {
                        draftNode.setX(p.getNodeData().x());
                        draftNode.setY(p.getNodeData().y());
                        draftNode.setZ(p.getNodeData().z());
                    }
                } else {
                    Random rnd = new Random();
                    draftNode.setX(rnd.nextFloat());
                    draftNode.setY(rnd.nextFloat());
                    draftNode.setZ(0f);
                }
                
                nodeCount++;
            }
            flushToNode(draftNode, n);
            draftNode.setNode(n);
        }

        //Push nodes in data structure
        for (NodeDraftGetter draftNode : container.getNodes()) {
            Node n = draftNode.getNode();
            NodeDraftGetter[] parents = draftNode.getParents();
            if (parents != null) {
                for (int i = 0; i < parents.length; i++) {
                    Node parent = parents[i].getNode();
                    graph.addNode(n, parent);
                }
            } else {
                graph.addNode(n);
            }
        }

        //Create all edges and push to data structure
        int edgeCount = 0;
        for (EdgeDraftGetter edge : container.getEdges()) {
            Node source = edge.getSource().getNode();
            Node target = edge.getTarget().getNode();
            if (graph.getEdge(source, target) == null) {
                Edge e = null;
                switch (container.getEdgeDefault()) {
                    case DIRECTED:
                        e = factory.newEdge(edge.isAutoId() ? null : edge.getId(), source, target, edge.getWeight(), true);
                        break;
                    case UNDIRECTED:
                        e = factory.newEdge(edge.isAutoId() ? null : edge.getId(), source, target, edge.getWeight(), false);
                        break;
                    case MIXED:
                        e = factory.newEdge(edge.isAutoId() ? null : edge.getId(), source, target, edge.getWeight(), edge.getType().equals(EdgeType.UNDIRECTED) ? false : true);
                        break;
                }

                flushToEdge(edge, e);
                edgeCount++;
                graph.addEdge(e);
            }
        }

        System.out.println("# New Nodes appended: " + nodeCount + "\n# New Edges appended: " + edgeCount);
        workspace = null;
    }
}