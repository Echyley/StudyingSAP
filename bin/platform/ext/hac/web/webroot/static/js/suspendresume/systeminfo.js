$(function () {

    $('#suspend').click(function () {

        if (confirm("Are you sure you want to suspend system?")) {
            var getTokenUrl = $('#suspend').attr('data-suspendTokenUrl');
            $.ajax({
                url: getTokenUrl,
                type: 'GET',
                headers: {
                    'Accept': 'application/json'
                },
                success: function (data) {
                    const suspendToken = $("<div/>").text(data.suspendToken).html();
                    var form = $('<form/>', {action: $('#suspend').attr('data-suspendUrl'), method: "post"})
                        .append($("<input type='hidden'/>").attr({name: "suspendToken", value: suspendToken}))
                        .append($("<input type='hidden'/>").attr({
                            name: "forShutdown",
                            value: $('#forShutdown').is(':checked')
                        }));
                    $('body').append(form);
                    debug.log(form.prop('outerHTML'))
                    form.submit();
                },
                error: hac.global.err
            });
        }
    });

    function renderOperations(dtos) {
        var output = $('<ul>');

        dtos.forEach(function (node) {
            output.append(renderNode(node))
        });

        return output;
    }

    function renderNode(node) {

        const li = $('<li ' + (node.suspendable === false ? 'class="cantSuspend"' : '') + ">");

        const threadName = $("<span/>").text(`ID: ${node.threadId}${delim()}Name: ${node.threadName}`);
        li.append(threadName);

        if (node.category !== null) {
            li.append($("<span/>").text(`${delim()}Category: ${node.category}`));
        }

        if (node.statusInfo !== null) {
            li.append(delim());
            li.append(span("Status Info: "));
            li.append(span(node.statusInfo, "infoName", node.statusInfo));
        }

        if(node.childThreads.length !== 0   ){
            const children = $('<ul>');
            node.childThreads.forEach(function(child) {
                children.append(renderNode(child));
            });
            li.append(children);
        }

        return li;
    }

    function span(content, cssClass, title) {
        const clazz = cssClass === null ? "infoName" : cssClass;
        const output = $('<span/>', {
            class: clazz,
            title: title
        });
        output.text(content);

        return output;
    }

    function delim() {
        return ' :: '
    }

    function updateRunningOperationsView() {
        if (document.hidden) {
            return;
        }
        var url = $('#runningOperations').attr('data-updateRunningOperationsUrl')

        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                'Accept': 'application/json'
            },
            success: function (data) {
                $('#runningOperations').html('').append(renderOperations(data.runningOperations));
                $('#systemStatus').text(data.systemStatus);
            },
            error: hac.global.err
        });

    }

    setInterval(updateRunningOperationsView, 5000);

    updateRunningOperationsView();
})
