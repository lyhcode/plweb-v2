Ext.onReady(function() {
	var menuPanel = new MenuPanel();
	// var mainPanel = new MainPanel();

	new Ext.Viewport({ // 注意这里是Viewport，NO ViewPort
		enableTabScroll : true,
		layout : "border",
		items : [ {
			title : "PLWeb RIA Platform",
			region : "north", // 顶部面板
			height : 80,
			html : '<h1>PLWeb System Administration Center</h1>'
		},
		menuPanel,
		{
			xtype : "tabpanel", // 选项卡
			region : "center",
			items : [ {
				title : "Dashboard",
				html : '<h1>Dashboard</h1>'
			} ]
		} ]
	});
	
    // add some default feeds
	menuPanel.addTreeItem('user', {
        url:	'http://feeds.feedburner.com/extblog',
        text:	'On-line users'
    }, false, true);
	
	menuPanel.addTreeItem('user', {
        url:	'ext',
        text:	'Latest registration'
    }, false, true);


});