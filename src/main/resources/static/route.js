function getScorePopoverContent(constraint_list) {
    var popover_content = "";
    constraint_list.forEach((constraint) => {
        if (getHardScore(constraint.score) == 0) {
            popover_content = popover_content + constraint.name + " : " + constraint.score + "<br>";
        } else {
            popover_content = popover_content + "<b>" + constraint.name + " : " + constraint.score + "</b><br>";
        }
    })
    return popover_content;
}

function getEntityPopoverContent(entityId, indictmentMap) {
    var popover_content = "";
    const indictment = indictmentMap[entityId];
    if (indictment != null) {
        popover_content = popover_content + "Total score: <b>" + indictment.score + "</b> (" + indictment.matchCount + ")<br>";
        indictment.constraintMatches.forEach((match) => {
            if (getHardScore(match.score) == 0) {
                popover_content = popover_content + match.constraintName + " : " + match.score + "<br>";
            } else {
                popover_content = popover_content + "<b>" + match.constraintName + " : " + match.score + "</b><br>";
            }
        })
    }

    return popover_content;
}

function getHardScore(score) {
    return score.slice(0,score.indexOf("hard"))
}

function getSoftScore(score) {
    return score.slice(score.indexOf("hard/"),score.indexOf("soft"))
}

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    $.getJSON("/routes/score?id=" + solutionId, function(analysis) {
        var badge = "badge bg-danger";
        if (getHardScore(analysis.score)==0) { badge = "badge bg-success"; }
        $("#score_a").attr({"title":"Score Brakedown","data-bs-content":"" + getScorePopoverContent(analysis.constraints) + "","data-bs-html":"true"});
        $("#score_text").text(analysis.score);
        $("#score_text").attr({"class":badge});
    });

    $.getJSON("/routes/solution?id=" + solutionId, function(solution) {
        $.getJSON("/routes/indictments?id=" + solutionId, function(indictments) {
            renderRoutes(solution, indictments);
            $(function () {
                $('[data-toggle="popover"]').popover()
            })
        })
    });

});

function renderRoutes(solution, indictments) {
    var indictmentMap = {};
    indictments.forEach((indictment) => {
        indictmentMap[indictment.indictedObjectID] = indictment;
    })

    const storage_div = $("#storage_container");
    solution.storageList.forEach((storage) => {

        var v_badge = "badge bg-danger";
        if (indictmentMap[storage.name]==null || getHardScore(indictmentMap[storage.name].score)==0) { v_badge = "badge bg-success"; }
        storage_div.append($('<a data-toggle="popover" data-bs-html="true" data-bs-content="' +
            'capacity=' + storage.capacity +
            '<hr>' +
            getEntityPopoverContent(storage.name, indictmentMap) +
            '" data-bs-original-title="'+ storage.name + ' (' + storage.capacity + ')' +'"><span class="'+ v_badge +'">'+
            storage.name + ' (' + storage.capacity + ')' +'</span></a>'));
        var retail_nr = 1;
        storage.retails.forEach((retail) => {
            var retail_badge = "badge bg-danger";
            if (indictmentMap[retail.name] == null || getHardScore(indictmentMap[retail.name].score)==0) { retail_badge = "badge bg-success"; }
            storage_div.append($('<a data-toggle="popover" data-bs-html="true" data-bs-content="'+
                'capacity=' + retail.capacity + '<br>' +
                'timeToServe=' + retail.timeToServe +
                '<hr>' +
                getEntityPopoverContent(retail.name, indictmentMap) +
                '" data-bs-original-title="'+
                '#' + retail_nr + ' ' + retail.name+'"><span class="'+retail_badge+'">'+
                '#' + retail_nr + ' ' + retail.name + ' (' + retail.capacity + ')' +'</span></a>'));

            retail_nr = retail_nr + 1;
        })
        storage_div.append($('<br>'));
    })
}

function formatTime(timeInSeconds) {
    if (timeInSeconds != null) {
        const HH = Math.floor(timeInSeconds / 3600);
        const MM = Math.floor((timeInSeconds % 3600) / 60);
        const SS = Math.floor(timeInSeconds % 60);
        return HH + ":" + MM + ":" + SS;
    } else return "null";
}