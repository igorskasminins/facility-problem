var map = L.map('map').setView([56.9947, 24.0309], 11);
var color_idx = 0;
const colors = ["#f44336","#e81e63","#9c27b0","#673ab7","#3f51b5","#2196f3","#03a9f4","#00bcd4","#009688",
    "#4caf50","#8bc34a","#cddc39","#ffeb3b","#ffc107","#ff9800","#ff5722"];
;
const pickupIcon = L.divIcon({
    html: '<i class="fas fa-building"></i>'
});

const storageIcon = L.divIcon({
    html: '<i class="fas fa-warehouse"></i>'
});

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    $.getJSON("/routes/score?id=" + solutionId, function(analysis) {
        var badge = "badge bg-danger";
        if (getHardScore(analysis.score)==0) { badge = "badge bg-success"; }
        $("#score_a").attr({"title":"Score Brakedown","data-bs-content":"" + getScorePopoverContent(analysis.constraints) + "","data-bs-html":"true"});
        $("#score_text").text(analysis.score);
        $("#score_text").attr({"class":badge});

        $(function () {
            $('[data-toggle="popover"]').popover()
        })
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
    $("#solutionTitle").text("Version 04/Dec/2023 solutionId: " + solution.solutionId);

    var indictmentMap = {};
    indictments.forEach((indictment) => {
        indictmentMap[indictment.indictedObjectID] = indictment;
    })

    solution.storageList.forEach((storage) => {
        let storage_location = [storage.location.lat, storage.location.lon];
        let nr = 1;
        const vcolor = getColor();
        const vmarker = L.marker(storage_location).addTo(map);
        vmarker.setIcon(storageIcon);
        storage.retails.forEach((retail) => {
            const location = [retail.location.lat, retail.location.lon];
            const marker = L.marker(location).addTo(map);
            marker.setIcon(getRetailIcon(retail.retailType, indictmentMap[retail.name]));
            marker.bindPopup("<b>#"+nr+"</b><br>id="+retail.name+"<br>"+retail.retailType+"<br>"+retail.capacity + "<br>arrival="
                + formatTime(retail.arrivalTime) + "<br>undeliverd=" + retail.capacityUndelivered +                                                                                                   '<br>picked=' + retail.capacityPicked +
                "<hr>" + getEntityPopoverContent(retail.name, indictmentMap));
            const line = L.polyline([storage_location, location], {color: vcolor}).addTo(map);
            nr = nr + 1;
        });
        const line_back = L.polyline([storage_location, [storage.location.lat, storage.location.lon]],{color: vcolor}).addTo(map);
    });
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

function getRetailIcon(v_type, indictment) {
    return pickupIcon;
}

function getColor() {
    color_idx = (color_idx + 1) % colors.length;
    return colors[color_idx];
}

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

function getHardScore(score) {
    return score.slice(0,score.indexOf("hard"))
}

function getSoftScore(score) {
    return score.slice(score.indexOf("hard/"),score.indexOf("soft"))
}

function formatTime(timeInSeconds) {
    if (timeInSeconds != null) {
        const HH = Math.floor(timeInSeconds / 3600);
        const MM = Math.floor((timeInSeconds % 3600) / 60);
        const SS = Math.floor(timeInSeconds % 60);
        return HH + ":" + MM + ":" + SS;
    } else return "null";
}