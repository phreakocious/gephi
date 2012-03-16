package net.phreakocious.httpgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phreakocious
 */
public class SnarfData {

    private static final Logger log = Logger.getLogger(SnarfData.class.getName());
    private static HashMap<String, String> attribtypes = new HashMap<String, String>();
    private String clientaddr, domain, host, uri;
    private int bytes = 0;
    private HashMap<String, SDNode> nodes = new HashMap<String, SDNode>();
    private ArrayList<SDEdge> edges = new ArrayList<SDEdge>();

    public SnarfData(String xsrcaddr, String xuri, String xmethod, String xhost, String xreferer) {
        setClient(xsrcaddr);
        setHost(xhost);
        setUri(xuri);
        setReferer(xreferer);
        String method = xmethod;
        //explodeUri(nodes.get("uri"));
        //explodeUri(nodes.get("referer"));

        for (String n : new String[]{"referer", "rhost", "rdomain", "host", "domain", "client"}) {
            nodes.get(n).attributes.put("content-type", "");
        }

        addEdge("rdomain", "rhost");
        addEdge("rhost", "referer");
        addEdge("referer", "uri");
        addEdge("client", "uri");
        addEdge("host", "uri");
        addEdge("domain", "host");
    }

    public class SDNode {

        final String type, id, domain, label;
        final float size;
        boolean labelvisible;
        HashMap<String, Object> attributes;

        public SDNode(String t, String d, String l, String i, boolean lv, float sz) {
            type = t;
            label = l;
            domain = d;
            id = i;
            labelvisible = lv;
            size = sz;
            attributes = new HashMap<String, Object>();
            setAttrib("type", type);
            setAttrib("domain", domain);
        }

        public final void setAttrib(String name, Object value) {
            String attribclass = value.getClass().getName();
            String result;

            result = attribtypes.put(name, attribclass);

            if (result != null && !result.equals(attribclass)) {
                log.warning(String.format("Redefined attribute type: %s is now %s !!", result, attribclass));
            }
            attributes.put(name, value);
        }
    }

    public class SDEdge {

        SDNode src, dst;

        public SDEdge(SDNode source, SDNode destination) {
            src = source;
            dst = destination;
        }
    }

    private void setClient(String rawaddr) {
        clientaddr = rawaddr.replaceFirst("[^\\d]+(\\d+\\.\\d+\\.\\d+\\.\\d+).*", "$1");
        nodes.put("client", new SDNode("client", "local", clientaddr, clientaddr, net.phreakocious.httpgraph.HttpGraph.INSTANCE.isClientLabelVisible(), 8f));
    }

    private void setUri(String rawuri) {
        if (rawuri == null || rawuri.isEmpty()) {
            log.severe("Snarfailure!");
            return;
        }
        uri = parseUri(rawuri);
        nodes.put("uri", new SDNode("resource", domain, uri, uri, net.phreakocious.httpgraph.HttpGraph.INSTANCE.isResourceLabelVisible(), 3f));
    }

    private void explodeUri(SDNode node) {

        String uripath = node.label;

        ArrayList<String> npath = new ArrayList<String>();
        npath.addAll(Arrays.asList(uripath.split("/")));

        String nhost = npath.remove(0);
        SDNode prevnode = getNodeByID(nhost);

        if (npath.size() >= 1) {
            npath.remove(npath.size() - 1);
        }

        String path = "/";

        for (String s : npath) {
            path = path + s + "/";
            SDNode newnode = new SDNode("uripath", domain, path, host + path, true, 1f);
            nodes.put(host + path, newnode);
            log.finer(String.format("edge: %s -> %s", prevnode.id, newnode.id));
            edges.add(new SDEdge(prevnode, newnode));
            prevnode = newnode;
        }

        edges.add(new SDEdge(prevnode, node));
    }

    private void setReferer(String referer) {
        String rdomain, rhost;
        if (referer == null || referer.isEmpty()) {
            referer = clientaddr;
            rhost = clientaddr;
            rdomain = "local";
        } else {
            referer = parseUri(referer);
            rhost = referer.replaceFirst(":.*$", "");
            rdomain = parseDomain(rhost);
        }

        nodes.put("referer", new SDNode("resource", rdomain, referer, referer, net.phreakocious.httpgraph.HttpGraph.INSTANCE.isResourceLabelVisible(), 3f));
        nodes.put("rhost", new SDNode("host", rdomain, rhost, rhost, net.phreakocious.httpgraph.HttpGraph.INSTANCE.isHostLabelVisible(), 4f));
        nodes.put("rdomain", new SDNode("domain", rdomain, rdomain, rdomain, net.phreakocious.httpgraph.HttpGraph.INSTANCE.isDomainLabelVisible(), 6f));

    }

    private String parseUri(String uri) {
        uri = uri.replaceFirst("^https?://", "");
        uri = uri.replaceFirst("\\?.*", "");
        return uri;
    }

    private void setHost(String rawhost) {
        host = rawhost;
        host = host.replaceFirst(":.*$", "");
        domain = parseDomain(host);

        nodes.put("host", new SDNode("host", domain, host, host, net.phreakocious.httpgraph.HttpGraph.INSTANCE.isHostLabelVisible(), 4f));
        nodes.put("domain", new SDNode("domain", domain, domain, domain, net.phreakocious.httpgraph.HttpGraph.INSTANCE.isDomainLabelVisible(), 6f));
    }

    private String parseDomain(String domain) {
        domain = domain.replaceFirst("/.*", "");

        if (domain.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            //
        } else if (domain.matches(".*\\.[^.]{2}\\.[^.]{2}$")) {
            domain = domain.replaceFirst(".*\\.([^.]+\\.[^.]{2}\\.[^.]{2})$", "$1");

        } else {
            domain = domain.replaceFirst(".*\\.([^.]+\\.[^.]+)$", "$1");
        }

        // Eliminates naming conflicts between host and
        // domain for those with no high level qualifier ;)

        domain = domain.concat(".");

        if (domain.equals(".")) {
            domain = "local";
        }
        return domain;
    }

    public void setBytes(int thebytes) {
        bytes = thebytes;
    }

    public SDNode getNode(String nodetype) {
        return (nodes.get(nodetype));
    }

    public SDNode getNodeByID(String id) {
        for (SDNode n : nodes.values()) {
            if (n.id.equals(id)) {
                return n;
            }
        }
        return null;
    }

    public SDNode[] getNodes() {
        return nodes.values().toArray(new SDNode[nodes.size()]);
    }

    public HashMap<String, Object> getAttributes(SDNode node) {
        return node.attributes;
    }

    public HashMap<String, String> getNodeAttributeList() {
        return attribtypes;
    }

    public SDNode[] getEdgeNodes(SDEdge e) {
        SDEdge edge = e;
        return new SDNode[]{edge.src, edge.dst};
    }

    private boolean addEdge(String n1, String n2) {
        SDNode src = nodes.get(n1);
        SDNode dst = nodes.get(n2);

        if (src.label == null || dst.label == null) {
            log.warning(String.format("something is null!  src.label = %s  dst.label = %s", src.label, dst.label));
            return false;
        }
        if (src.label.equals(dst.label)) {
            // log.info(String.format("labels are equal!  label = %s  src.type = %s  dst.type = %s", src.label, src.type, dst.type));
            return false;
        }
        edges.add(new SDEdge(src, dst));
        return true;
    }

    public SDEdge[] getEdges() {
        return edges.toArray(new SDEdge[edges.size()]);
    }

    public void nullCheck() {
        for (SDNode n : getNodes()) {
            for (String attrib : n.attributes.keySet()) {
                String value = n.attributes.get(attrib).toString();
                if (value == null) {
                    log.warning(String.format("node %s attrib %s is null!", n.label, attrib));
                }
            }
        }
    }

    public void graphUpdate() {
        // dump();
        net.phreakocious.httpgraph.HttpGraph.INSTANCE.graphupdate(this);
    }

    private void dump() {
        log.log(Level.INFO, "vars:  srcaddr {0}", clientaddr);
        log.log(Level.INFO, "vars:  bytes {0}", bytes);
        log.log(Level.INFO, "vars:  uri {0}", nodes.get("uri").label);
        log.log(Level.INFO, "vars:  host {0}", nodes.get("host").label);
        log.log(Level.INFO, "vars:  domain {0}", nodes.get("domain").label);
        log.log(Level.INFO, "vars:  referer {0}", nodes.get("referer").label);
        log.log(Level.INFO, "vars:  rhost {0}", nodes.get("rhost").label);
        log.log(Level.INFO, "vars:  rdomain {0}", nodes.get("rdomain").label);
    }

}