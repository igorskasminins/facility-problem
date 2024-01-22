$(document).ready(function () {
    $.getJSON("/routes/list", function(plannings) {
        var listofroutes = $("#listofroutes");
        $.each(plannings, function(idx, value) {
            listofroutes.append($('<li><a href="route.html?id='+ value.solutionId + '">' +
                value.score +'</a><a href="route_leaflet.html?id='+ value.solutionId + '"> (map) </a>' +
                ' storages: ' + ', retails:' + value.retailList.length + '</li>'));
        });
    });
});