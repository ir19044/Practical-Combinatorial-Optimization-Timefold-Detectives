var map = L.map('map').setView([56.9947, 24.0309], 11);
var color_idx = 0;
const colors = ["#f44336","#e81e63","#9c27b0","#673ab7","#3f51b5","#2196f3","#03a9f4","#00bcd4","#009688",
    "#4caf50","#8bc34a","#cddc39","#ffeb3b","#ffc107","#ff9800","#ff5722"];
;
const defaultIcon = new L.Icon.Default();
const officeIcon = L.divIcon({
    html: '<i class="fas fa-building"></i>'
});
const thiefIcon = L.divIcon({
    html: '<i class="fas fa-user-secret"></i>'
});
const officeIcon_red = L.divIcon({
    html: '<i class="fas fa-building" style="color: #ff0000"></i>'
});
const thiefIcon_red = L.divIcon({
    html: '<i class="far fa-building" style="color: #ff0000"></i>'
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
    $("#solutionTitle").text("Version 22/Jan/2024 solutionId: " + solution.solutionId);

    var indictmentMap = {};
    indictments.forEach((indictment) => {
        indictmentMap[indictment.indictedObjectID] = indictment;
    })

    solution.detectiveList.forEach((detective) => {
        let previous_location = [detective.workOffice.lat, detective.workOffice.lon];

        let nr = 1;
        const vcolor = getColor();
        const vmarker = L.marker(previous_location).addTo(map);

        vmarker.setIcon(officeIcon);
        detective.visits.forEach((visit) => {
            const location = [visit.location.lat, visit.location.lon];
            const marker = L.marker(location).addTo(map);
            const thiefSetContent = visit.thiefSet !== null
                ? '<br>thiefSet={' + Array.from(visit.thiefSet).map(thief => thief.id).sort((a, b) => a - b).join(', ') + '}'
                : '';

            marker.setIcon(getVisitIcon(visit.visitType, indictmentMap[visit.name]));
            marker.bindPopup("<b>#"+nr+"</b>" +
                "<br>id="+visit.name+
                "<br>type="+visit.visitType+
                "<br>expLvl="+visit.expMonths +
                "<br>arrival=" + formatTime(visit.arrivalTime) +
                '<br>photoTime=' + formatTime(visit.photoTime) +
                '<br>twStart=' + formatTime(visit.twStart) +
                '<br>twFinish=' + formatTime(visit.twFinish) +
                thiefSetContent +
                "<hr>" + getEntityPopoverContent(visit.name, indictmentMap));

            if ((visit.visitType === "PHOTO" && visit.photoTime > 0) ||
                visit.visitType === "PROTOCOL" &&
                ((visit.prev == null && visit.distanceToVisit > 0)
                    || (visit.prev != null && visit.distanceToVisit - visit.prev.distanceToVisit > 0)))
            {
                const line = L.polyline([previous_location, location], {color: vcolor}).addTo(map);
                previous_location = location;
            }
            else{
                previous_location = [detective.workOffice.lat, detective.workOffice.lon];
            }

            nr = nr + 1;
        });
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

function getVisitIcon(v_type, indictment) {
    if (indictment==undefined || getHardScore(indictment.score) == 0) {
        return v_type == "PROTOCOL" ? officeIcon : thiefIcon;
    } else {
        return v_type == "PROTOCOL" ? officeIcon : thiefIcon;
    }

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