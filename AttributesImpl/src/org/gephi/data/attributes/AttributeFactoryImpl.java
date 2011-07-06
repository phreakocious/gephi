/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gephi.data.attributes;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeRowFactory;
import org.gephi.data.attributes.api.AttributeValueFactory;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.attributes.store.AttributeStore;
import org.gephi.data.attributes.store.AttributeStoreController;
import org.gephi.graph.api.EdgeData;
import org.gephi.graph.api.NodeData;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class AttributeFactoryImpl implements AttributeValueFactory, AttributeRowFactory {

    private AbstractAttributeModel model;
    private AttributeStoreController controller = Lookup.getDefault().lookup(AttributeStoreController.class);
    
    public AttributeFactoryImpl(AbstractAttributeModel model) {
        this.model = model;
    }

    public AttributeValue newValue(AttributeColumn column, Object value) {
        if (value == null) {
            return new AttributeValueImpl((AttributeColumnImpl) column, null);
        }

        if (value.getClass() != column.getType().getType() && value.getClass() == String.class) {
            value = column.getType().parse((String) value);
        }
        Object managedValue = model.getManagedValue(value, column.getType());
        return new AttributeValueImpl((AttributeColumnImpl) column, managedValue);
    }

    public AttributeRow newNodeRow(NodeData nodeData) {
        AttributeStore nodeStore = controller.getNodeStore(model);
        
        if (nodeStore == null)
            return new AttributeRowImpl(model.getNodeTable(), nodeData);
        else 
            return new AttributeRowProxyImpl(nodeStore, model.getNodeTable(), nodeData);
    }

    public AttributeRow newEdgeRow(EdgeData edgeData) {
        AttributeStore edgeStore = controller.getEdgeStore(model);
        
        if (edgeStore == null)
            return new AttributeRowImpl(model.getEdgeTable(), edgeData);
        else
            return new AttributeRowProxyImpl(edgeStore, model.getEdgeTable(), edgeData);
    }

    public AttributeRow newRowForTable(String tableName, Object object) {
        AttributeTableImpl attTable = model.getTable(tableName);
        if (attTable != null) {
            return new AttributeRowImpl(attTable, object);
        }
        return null;
    }

    public void setModel(AbstractAttributeModel model) {
        this.model = model;
    }
}
