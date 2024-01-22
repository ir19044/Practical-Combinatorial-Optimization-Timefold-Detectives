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

    const detective_div = $("#detective_container");
    solution.detectiveList.forEach((detective) => {

        var v_badge = "badge bg-danger";
        if (indictmentMap[detective.regNr]==null || getHardScore(indictmentMap[detective.regNr].score)==0) { v_badge = "badge bg-success"; }
        detective_div.append($('<a data-toggle="popover" data-bs-html="true" data-bs-content="' +
            'expLevel=' + detective.experienceMonths +
            '<br>maxGroupCount=' + detective.maxGroupCount +
            '<br>hasCar=' + detective.hasCar +
            '<br>twStart=' + formatTime(detective.twStart) +
            '<br>twFinish=' + formatTime(detective.twFinish) +
            '<br>location=(' + detective.workOffice.lat + ', ' + detective.workOffice.lon + ')' +
            '<hr>' +
            getEntityPopoverContent(detective.regNr, indictmentMap) +
            '" data-bs-original-title="'+ detective.empNr + ' (' + detective.experienceMonths + ')' +'"><span class="'+ v_badge +'">'+
            detective.empNr + ' (' + detective.experienceMonths + ')' +'</span></a>'));
        var visit_nr = 1;
        detective.visits.forEach((visit) => {
            var visit_badge = "badge bg-danger";

            if (indictmentMap[visit.name] == null || getHardScore(indictmentMap[visit.name].score)==0) { visit_badge = "badge bg-success"; }

            if (visit.visitType === "PHOTO" && visit.photoTime == 0) {
                visit_badge = "badge bg-secondary";
            }

            var thiefSetContent = visit.thiefSet !== null ? '<br>thiefSet={' + Array.from(visit.thiefSet).map(thief => thief.id).sort((a, b) => a - b).join(', ') + '}' : '';

            detective_div.append($('<a data-toggle="popover" data-bs-html="true" data-bs-content="'+
                'distanceToVisit=' + visit.distanceToVisit +
                '<br>arrival=' + formatTime(visit.arrivalTime) +
                '<br>photoTime=' + formatTime(visit.photoTime) +
                '<br>catchGroupCount=' + visit.catchGroupCount +
                '<br>twStart=' + formatTime(visit.twStart) +
                '<br>twFinish=' + formatTime(visit.twFinish) +
                '<br>location=(' + visit.location.lat + ', ' + visit.location.lon + ')' +
                thiefSetContent +
                '<hr>' +
                getEntityPopoverContent(visit.name, indictmentMap) +
                '" data-bs-original-title="'+
                '#' + visit_nr + ' ' +visit.visitType + ' ' + visit.name+'"><span class="'+visit_badge+'">'+
                '#' + visit_nr + ' ' +visit.visitType + ' ' + visit.name + ' (' + visit.expMonths + ')' +'</span></a>'));

            visit_nr = visit_nr + 1;
        })
        detective_div.append($('<br>'));
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



