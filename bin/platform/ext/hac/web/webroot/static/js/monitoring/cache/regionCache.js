var tables = {};
var intervalId;

$(document).ready(function() {

    $("#accordion").accordion({
        heightStyle: "content",
      active: false,
      collapsible: true
    });

    $('#resetCache').click(function(e) {
        var token = $("meta[name='_csrf']").attr("content");
        var url = $('#resetCache').attr('data-url');
        $.ajax({
            url : url,
            type : 'POST',
            headers : {
                'Accept' : 'application/json',
                'X-CSRF-TOKEN' : token
            },
            success : function(data) {
                updateRegions(data.regions);
            },
            error: hac.global.err
        });

    });

    update(true);
    intervalId = setInterval(update, 2000, false);

});

function update(init) {
    if (document.hidden) {
        return;
    }
    var token = $("meta[name='_csrf']").attr("content");
    var url = $('#accordion').attr('data-updateDataUrl');
    $.ajax({
        url: url,
        type: 'GET',
        headers: {
            'Accept': 'application/json',
            'X-CSRF-TOKEN': token
        },
        success: function (data) {

            if (init) {
                initTables(data.regions);
            }

            updateRegions(data.regions);

        },
        error: hac.global.err
    });
}

function initTables(regions) {
    for (var pos in regions) {
        if (regions.hasOwnProperty(pos)) {
            var regionName = regions[pos].name;
            tables[regionName] = $(`#${regionName}_types`).dataTable({});
        }
    }
}

function updateRegions(regions)
{
    var pattern = /^([a-zA-Z0-9]+) \((.+)\)$/;
    for (var pos in regions)
    {
        if (regions.hasOwnProperty(pos)) {
            updateRegion(regions[pos], pattern);
        }
    }
}

function updateRegion(region, pattern) {
    const name = $("<div/>").text(region.name).html();
    tables[name].fnClearTable();

    $(`#${name}_maxEntries`).text(region.maxEntries);
    $(`#${name}_maxReachedSize`).text(region.maxReachedSize);
    $(`#${name}_factor`).text(region.factor);

    try {
        $(`#${name}_Hits`).text(region.cacheStatistics.hits);
        $(`#${name}_Misses`).text(region.cacheStatistics.misses);
        $(`#${name}_Invalidations`).text(region.cacheStatistics.invalidations);
        $(`#${name}_Evictions`).text(region.cacheStatistics.evictions);
        $(`#${name}_Fetches`).text(region.cacheStatistics.fetches);
        $(`#${name}_InstanceCount`).text(region.cacheStatistics.instanceCount);
    }
    catch (err) {
        //
    }

    for (var typePos in region.typesStatistics) {
        if (region.typesStatistics.hasOwnProperty(typePos)) {
            var type = region.typesStatistics[typePos];
            var ratio = type.hits + type.misses === 0 ? 0 : Math.round(100 * type.hits/(type.hits + type.misses));
            var instanceCount = Math.max(0, type.misses - type.evictions - type.invalidations);

            var matches = pattern.exec(type.typeName);

            tables[name].fnAddData([(matches != null) ? getHoverSpan(matches[1], matches[2]) : type.type, type.hits, type.misses, ratio, type.invalidations, type.evictions, type.fetches, instanceCount]);
        }
    }
}

function getHoverSpan(name, fullName)
{
    return '<span title="' + fullName + '">' + name + '</span>';
}
