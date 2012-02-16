/*!
 * Ext JS Library 3.2.1
 * Copyright(c) 2006-2010 Ext JS, Inc.
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
MenuPanel = function() {
    MenuPanel.superclass.constructor.call(this, {
        id:				'feed-tree',
        region:			'west',
        title:			'Menu',
        split:			true,
        width:			225,
        minSize: 		200,
        maxSize: 		400,
        collapsible:	true,
        margins:		'0 0 5 5',
        cmargins:		'0 5 5 5',
        rootVisible:	false,
        lines:			false,
        autoScroll:		true,
        root:			new Ext.tree.TreeNode('Menu Items'),
        collapseFirst:	false
    });
    
    this.management = this.root.appendChild(
        new Ext.tree.TreeNode({
            text:		'Management',
            cls:		'feeds-node',
            expanded:	true
        })
    );

    this.treeUser = this.management.appendChild(
        new Ext.tree.TreeNode({
            text:		'Users',
            cls:		'feeds-node',
            expanded:	true
        })
    );
    
    this.treeResource = this.management.appendChild(
        new Ext.tree.TreeNode({
            text:		'Resources',
            cls:		'feeds-node',
            expanded:	true
        })
    );

    this.treeClass = this.management.appendChild(
        new Ext.tree.TreeNode({
            text:		'Classes',
            cls:		'feeds-node',
            expanded:	true
        })
    );
    
    this.getSelectionModel().on({
        'beforeselect' : function(sm, node){
             return node.isLeaf();
        },
        'selectionchange' : function(sm, node){
            if(node){
                this.fireEvent('feedselect', node.attributes);
            }
            //this.getTopToolbar().items.get('delete').setDisabled(!node);
        },
        scope:this
    });

    this.addEvents({feedselect:true});

};

Ext.extend(MenuPanel, Ext.tree.TreePanel, {

    selectTreeItem: function(url){
        this.getNodeById(url).select();
    },
    
    addTreeItem : function(tree, attrs, inactive, preventAnim){
        var exists = this.getNodeById(attrs.url);
        if(exists){
            if(!inactive){
                exists.select();
                exists.ui.highlight();
            }
            return;
        }
        Ext.apply(attrs, {
            iconCls: 'feed-icon',
            leaf:true,
            cls:'feed',
            id: attrs.url
        });
        var node = new Ext.tree.TreeNode(attrs);
        var target = null;
        if (tree == 'user') {
        	this.treeUser.appendChild(node);
        }
        if(!inactive){
            if(!preventAnim){
                Ext.fly(node.ui.elNode).slideIn('l', {
                    callback: node.select, scope: node, duration: .4
                });
            }else{
                node.select();
            }
        }
        return node;
    },

    // prevent the default context menu when you miss the node
    afterRender : function(){
        MenuPanel.superclass.afterRender.call(this);
        this.el.on('contextmenu', function(e){
            e.preventDefault();
        });
    }
});

//Ext.reg('appfeedpanel', MenuPanel); 