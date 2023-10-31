	var nodestable;
	
	$(document).ready(function() {
		nodestable = $('#clusternodes').dataTable({
			"bPaginate" : false,
			"bFilter" : false,
			"bInfo" : false
		});		
		
		
		$('#ping').click(function() {

			hac.global.notify("Nodes will be pinged. Table will update...");
            var token = $("meta[name='_csrf']").attr("content");

            var url = $('#ping').attr('data-url');
			$.ajax({
				url:url,
				type:'POST',
				headers:{
                    'Accept':'application/json',
                    'X-CSRF-TOKEN' : token
                },
				success: function(data) {
					update(data);
				},
				error: hac.global.err
			});	
			
		});	
		
		updateNodes();
		
	});
	
	function update(data)
	{
		updateNodeTable(data.nodes);
		updateConfiguration(data);
		updateBroadcastMethods(data.broadcastMethods);	
	}
	
	
	function updateNodes()
	{
		debug.log('updating...');
		var url = $('#clusternodes').attr('data-updateNodesUrl');
		$.ajax({
			url:url,
			type:'GET',
			headers:{'Accept':'application/json'},
			success: function(data) {
				update(data)
				
			},
			error: hac.global.err
		});	
	}
	
	function updateNodeTable(data)
	{
		nodestable.fnClearTable();
		for (pos in data)
		{
			var node = data[pos];
			nodestable.fnAddData([node.nodeIP, node.nodeID, node.dynamicNodeID, node.methodName]);
		}
	}
	
	function updateConfiguration(data)
	{
		$('#clusteringEnabled').text('' + data.clusterEnabled);
		$('#clusterIslandId').text(data.clusterIslandId);
		$('#clusterNodeId').text(data.clusterNodeId);
		$('#dynamicClusterNodeId').text(data.dynamicClusterNodeId);
	}	
	
	function updateBroadcastMethods(methods)
	{
		var wrapper = $('#methodWrapper'); 
		wrapper.html('');
		
		for (pos in methods)
		{
            if (!methods.hasOwnProperty(pos)) {
                continue;
            }
			const method = methods[pos];
			
			const head = $("<h3/>").text(`${method.name} Cluster Settings`);
			wrapper.append(head);
			
			const settingsMap = method.settings;
			wrapper.append(dl(settingsMap));
			
			if (method.nodes) //only build nodes table in case there is data. only will be available for TCP clustering, not for UDP
			{
				const methodNodes = method.nodes;
				wrapper.append('<h4>Nodes</h4>');

                const tableId = $("<div/>").text(`${method.name}_table`).html();

                const table = $("<table/>", {id: tableId}).append(
                    $("<thead><tr><th>clusterNodeId</th><th>clusterNodeState</th><th>serverAddress</th><th>lastUp</th></tr></thead><tbody></tbody></table>")
                );

                wrapper.append(table);
				
				const tcpTable = $(`#${tableId}_table`).dataTable({bInfo:false, bPaginate:false, bFilter: false, bSort: false});
				
				for (pos in methodNodes)
				{
                    if(!methodNodes.hasOwnProperty(pos)) {
                        continue;
                    }
                    const nodeMap = methodNodes[pos];
                    tcpTable.fnAddData([nodeMap.nodeID, nodeMap.nodeID, nodeMap.serverAddress, nodeMap.lastUp]);
				}
			}
			
		}
	}
	
	
	function dl(map)
	{
		var dl = '';
		dl += '<dl>';
		
		for (key in map)
		{
			dl += dlItem(key, map[key]);
		}
		
		dl+= '</dl>';
		
		return dl;		
	}
	
	function dlItem(key, value)
	{
		var item = '';
		item += '<dt>'+key+'</dt>';
		item +='<dd>'+value+'</dd>';
		return item;
	}
