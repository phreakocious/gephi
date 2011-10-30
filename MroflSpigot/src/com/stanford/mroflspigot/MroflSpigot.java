/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stanford.mroflspigot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.Issue;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.SpigotImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Sébastien Heymann
 */
public class MroflSpigot implements SpigotImporter, LongTask {

    private ContainerLoader container;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;
    private JSONParser parser;
    private DirectedGraph graph;
    private String nameRaw;
    private String personID;
    private int depth;
    private AttributeColumn colVisited = null;
    private AttributeColumn colRelationships = null;
    private AttributeColumn colNationality = null;
    private AttributeColumn colGender = null;
    private AttributeColumn colBirthDate = null;
    private AttributeColumn colDeathDate = null;

    @Override
    public boolean execute(ContainerLoader loader) {
        this.container = loader;
        this.report = new Report();
        this.parser = new JSONParser();
        this.graph = Lookup.getDefault().lookup(GraphController.class).getModel().getDirectedGraph();

        Progress.setDisplayName(progressTicket, "Get MROFL data...");
        Progress.start(progressTicket);

        try {
            if (personID == null) {
                this.personID = getPersonIDfromNameRaw(nameRaw); //e.g. "John Richards"
            }
            if (nameRaw == null) {
                nameRaw = personID;
            }
            colVisited = container.getAttributeModel().getNodeTable().addColumn("visited", AttributeType.BOOLEAN, AttributeOrigin.COMPUTED);
            colRelationships = container.getAttributeModel().getNodeTable().addColumn("relationships", AttributeType.INT, AttributeOrigin.DATA);
            getPeopleNet(personID, depth);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        Progress.finish(progressTicket);

        return !cancel;
    }

    private void getPeopleNet(String personID, int hops) throws Exception {
        String jsonResponse = query("_id", personID);
        if (!jsonResponse.isEmpty()) {
            //System.out.println(jsonResponse);
            Map jsonMap = (Map) parser.parse(jsonResponse);
            if (!jsonMap.get("status").toString().equals("ok")) {
                report.logIssue(new Issue("status error: " + jsonMap.get("status"), Issue.Level.SEVERE));
                return;
            }

            if (((JSONArray) jsonMap.get("result")).size() > 0) {
                JSONArray sources = (JSONArray) ((Map) ((JSONArray) jsonMap.get("result")).get(0)).get("sources");
                if (graph.getNode(personID) == null) {
                    addNode(personID, nameRaw);
                    container.getNode(personID).addAttributeValue(colVisited, Boolean.TRUE);
                } else {
                    graph.getNode(personID).getNodeData().getAttributes().setValue(colVisited.getIndex(), Boolean.TRUE);
                }
                if (container.nodeExists(personID) && graph.getNode(personID) == null) {
                    addNodeAttributes(personID, jsonResponse);
                }

                for (Object entry : sources) {
                    JSONArray relationships = (JSONArray) ((Map) entry).get("Relationships");
                    if (relationships != null) {

                        for (Object relation : relationships) {
                            if (((Map) relation).get("MPerson") != null) {
                                String relationID = ((Map) relation).get("MPerson").toString();

                                //add the relation node if not exists in the graph
                                if (graph.getNode(relationID) == null) {
                                    String label = ((Map) relation).get("Person").toString();
                                    addNode(relationID, label);
                                    addNodeAttributes(relationID, query("_id", relationID));
                                }

                                int weight = 0;
                                Object sentTo = ((Map) relation).get("SentTo");
                                if (sentTo != null) {
                                    weight = ((Number) sentTo).intValue();
                                    if (weight > 0) {
                                        addEdge(personID, relationID, weight);
                                    }
                                }
                                weight = 0;
                                Object receivedFrom = ((Map) relation).get("ReceivedFrom");
                                if (receivedFrom != null) {
                                    weight = ((Number) receivedFrom).intValue();
                                    if (weight > 0) {
                                        addEdge(relationID, personID, weight);
                                    }
                                }
                                if (hops > 1) {
                                    getPeopleNet(relationID, hops - 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String getPersonIDfromNameRaw(String nameRaw) throws Exception {
        String id = null;
        String jsonResponse = query("NameRaw", nameRaw);
        if (!jsonResponse.isEmpty()) {
            //System.out.println(jsonResponse);

            Map jsonMap = (Map) parser.parse(jsonResponse);
            if (!jsonMap.get("status").toString().equals("ok")) {
                report.logIssue(new Issue("status error: " + jsonMap.get("status"), Issue.Level.SEVERE));
                return null;
            }
            if (((JSONArray) jsonMap.get("result")).size() > 0) {
                id = ((Map) ((JSONArray) jsonMap.get("result")).get(0)).get("_id").toString();
            } else {
                report.logIssue(new Issue("no id found for " + nameRaw, Issue.Level.SEVERE));
            }
        }

        return id;
    }

    private String query(String key, String value) throws Exception {
        String q = URLEncoder.encode("\"" + key + "\":\"" + value + "\"", "UTF-8");
        URL url = new URL("http://mapping.stanford.edu/data/api.py?action=query&q={" + q + "}&collection=MPerson");
        //System.out.println(url);

        URLConnection conn = url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                conn.getInputStream()));

        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    private void addNode(String id, String label) {
        NodeDraft node;
        if (!container.nodeExists(id)) {
            node = container.factory().newNodeDraft();
            node.setId(id);
            node.setLabel(label);
            container.addNode(node);
        }
    }

    private void addEdge(String source, String target, float weight) {
        NodeDraft sourceNode;
        if (!container.nodeExists(source)) {
            sourceNode = container.factory().newNodeDraft();
            sourceNode.setId(source);
            container.addNode(sourceNode);
        } else {
            sourceNode = container.getNode(source);
        }
        NodeDraft targetNode;
        if (!container.nodeExists(target)) {
            targetNode = container.factory().newNodeDraft();
            targetNode.setId(target);
            container.addNode(targetNode);
        } else {
            targetNode = container.getNode(target);
        }
        EdgeDraft edge = container.getEdge(sourceNode, targetNode);
        if (edge == null) {
            edge = container.factory().newEdgeDraft();
            edge.setSource(sourceNode);
            edge.setTarget(targetNode);
            edge.setWeight(weight);
            container.addEdge(edge);
        } else {
            edge.setWeight(edge.getWeight() + weight);
        }
    }

    private void addNodeAttributes(String nodeID, String jsonResponse) throws Exception {
        if (!jsonResponse.isEmpty()) {
            //System.out.println(jsonResponse);
            Map jsonMap = (Map) parser.parse(jsonResponse);
            if (!jsonMap.get("status").toString().equals("ok")) {
                report.logIssue(new Issue("status error: " + jsonMap.get("status"), Issue.Level.SEVERE));
                return;
            }

            if (((JSONArray) jsonMap.get("result")).size() > 0) {
                JSONArray sources = (JSONArray) ((Map) ((JSONArray) jsonMap.get("result")).get(0)).get("sources");
                for (Object entry : sources) {
                    JSONArray relationships = (JSONArray) ((Map) entry).get("Relationships");
                    if (relationships != null) {
                        container.getNode(nodeID).addAttributeValue(colRelationships, relationships.size());
                    }
                }

                Object gender = ((Map) ((JSONArray) jsonMap.get("result")).get(0)).get("Gender");
                if (gender != null) {
                    if (!container.getAttributeModel().getNodeTable().hasColumn("gender")) {
                        colGender = container.getAttributeModel().getNodeTable().addColumn("gender", AttributeType.STRING, AttributeOrigin.DATA);
                    }
                    container.getNode(nodeID).addAttributeValue(colGender, gender);
                }

                Object nationality = ((Map) ((JSONArray) jsonMap.get("result")).get(0)).get("Nationality");
                if (nationality != null) {
                    if (!container.getAttributeModel().getNodeTable().hasColumn("nationality")) {
                        colNationality = container.getAttributeModel().getNodeTable().addColumn("nationality", AttributeType.STRING, AttributeOrigin.DATA);
                    }
                    container.getNode(nodeID).addAttributeValue(colNationality, nationality);
                }

                Map birthDate = (Map) ((Map) ((JSONArray) jsonMap.get("result")).get(0)).get("BirthDate");
                if (birthDate != null) {
                    if (!container.getAttributeModel().getNodeTable().hasColumn("birth date")) {
                        colBirthDate = container.getAttributeModel().getNodeTable().addColumn("birth date", AttributeType.INT, AttributeOrigin.DATA);
                    }
                    Long year = (Long) birthDate.get("year");
                    container.getNode(nodeID).addAttributeValue(colBirthDate, year);
                }

                Map deathDate = (Map) ((Map) ((JSONArray) jsonMap.get("result")).get(0)).get("DeathDate");
                if (deathDate != null) {
                    if (!container.getAttributeModel().getNodeTable().hasColumn("death date")) {
                        colDeathDate = container.getAttributeModel().getNodeTable().addColumn("death date", AttributeType.INT, AttributeOrigin.DATA);
                    }
                    Long year = (Long) deathDate.get("year");
                    container.getNode(nodeID).addAttributeValue(colDeathDate, year);
                }
            }
        }
    }

    public void setDepth(int d) {
        this.depth = d;
    }

    public void setNameRaw(String name) {
        this.nameRaw = name;
    }

    public void setPersonID(String id) {
        this.personID = id;
    }

    @Override
    public ContainerLoader getContainer() {
        return container;
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
