package net.phreakocious.httpgraph;

import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import javax.swing.JPanel;


/**
 *
 * @author phreakocious
 */
public class HttpGraphPanel extends JPanel {

    public HttpGraphPanel() {
        initComponents();
        chainProxy.setEnabled(false);
        chainProxyPort.setEnabled(false);
        startLayout.setEnabled(true);
    }

    @SuppressWarnings("unchecked")
    public static ValidationPanel createValidationPanel(HttpGraphPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new HttpGraphPanel();
        }
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();

        group.add(innerPanel.portField, Validators.REQUIRE_NON_EMPTY_STRING,
                Validators.REQUIRE_VALID_INTEGER,
                Validators.numberRange(1, 65535));

        group.add(innerPanel.chainProxy, Validators.HOST_NAME_OR_IP_ADDRESS);

        group.add(innerPanel.chainProxyPort, Validators.REQUIRE_VALID_INTEGER,
                Validators.numberRange(1, 65535));

        return validationPanel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        portLabel = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        chainProxy = new javax.swing.JTextField();
        chainProxyEnabled = new javax.swing.JCheckBox();
        chainProxyPort = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        startLayout = new javax.swing.JCheckBox();
        clientLabels = new javax.swing.JCheckBox();
        hostLabels = new javax.swing.JCheckBox();
        resourceLabels = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        domainLabels = new javax.swing.JCheckBox();

        portLabel.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.portLabel.text")); // NOI18N

        portField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        portField.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.portField.text")); // NOI18N

        chainProxy.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.chainProxy.text")); // NOI18N
        chainProxy.setToolTipText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.chainProxy.toolTipText")); // NOI18N

        chainProxyEnabled.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.chainProxyEnabled.text")); // NOI18N
        chainProxyEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chainProxyEnabledActionPerformed(evt);
            }
        });

        chainProxyPort.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.chainProxyPort.text")); // NOI18N
        chainProxyPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chainProxyPortActionPerformed(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.jLabel1.text")); // NOI18N

        startLayout.setSelected(true);
        startLayout.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.startLayout.text")); // NOI18N
        startLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startLayoutActionPerformed(evt);
            }
        });

        clientLabels.setSelected(true);
        clientLabels.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.clientLabels.text")); // NOI18N
        clientLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientLabelsActionPerformed(evt);
            }
        });

        hostLabels.setSelected(true);
        hostLabels.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.hostLabels.text")); // NOI18N
        hostLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hostLabelsActionPerformed(evt);
            }
        });

        resourceLabels.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.resourceLabels.text")); // NOI18N
        resourceLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourceLabelsActionPerformed(evt);
            }
        });

        jLabel2.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.jLabel2.text")); // NOI18N

        domainLabels.setSelected(true);
        domainLabels.setText(org.openide.util.NbBundle.getMessage(HttpGraphPanel.class, "HttpGraphPanel.domainLabels.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chainProxyEnabled)
                            .addComponent(portLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chainProxy, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addComponent(chainProxyPort, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(clientLabels)
                                .addGap(18, 18, 18)
                                .addComponent(domainLabels)
                                .addGap(18, 18, 18)
                                .addComponent(hostLabels)
                                .addGap(18, 18, 18)
                                .addComponent(resourceLabels)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(35, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startLayout, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chainProxy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(chainProxyPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chainProxyEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startLayout)
                .addGap(13, 13, 13)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientLabels)
                    .addComponent(hostLabels)
                    .addComponent(domainLabels)
                    .addComponent(resourceLabels))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chainProxyEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chainProxyEnabledActionPerformed
        if (chainProxyEnabled.isSelected()) {
            chainProxy.setEnabled(true);
            chainProxyPort.setEnabled(true);
        }  else {
            chainProxy.setEnabled(false);
            chainProxyPort.setEnabled(false);
        }
    }//GEN-LAST:event_chainProxyEnabledActionPerformed

    private void chainProxyPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chainProxyPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chainProxyPortActionPerformed

    private void startLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startLayoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startLayoutActionPerformed

    private void clientLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientLabelsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clientLabelsActionPerformed

    private void hostLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hostLabelsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hostLabelsActionPerformed

    private void resourceLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourceLabelsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resourceLabelsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextField chainProxy;
    protected javax.swing.JCheckBox chainProxyEnabled;
    protected javax.swing.JTextField chainProxyPort;
    protected javax.swing.JCheckBox clientLabels;
    protected javax.swing.JCheckBox domainLabels;
    protected javax.swing.JCheckBox hostLabels;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    protected javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    protected javax.swing.JCheckBox resourceLabels;
    public javax.swing.JCheckBox startLayout;
    // End of variables declaration//GEN-END:variables
}
