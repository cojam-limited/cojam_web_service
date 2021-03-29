function fnDrawChart(bettings){
    let i;
    const answers = [];

    const resultJSON = {};
    for (i = 0; i < bettings.length; i++) {

        const obj = bettings[i]; //배팅 기록
        let key = getDateString(new Date(obj.createdDateTime)); //배팅한 날짜
        const dateArray = key.split("-");

        key = dateArray[1] + "-" + dateArray[2];

        if (!resultJSON[key]) {
            resultJSON[key] = {};
        }

        var answer = obj.answerTitle;
        answers.push(answer);

        if (typeof resultJSON[key][answer] == 'undefined')
            resultJSON[key][answer] = 1;
        else
            resultJSON[key][answer]++;

    }
    //
    //베팅된 보기 전체 갯수
    const numberOfAnswers = {};
    answers.forEach(function (i) {
        numberOfAnswers[i] = (numberOfAnswers[i] || 0) + 1;
    });

    //배팅 날짜
    const monthDateArray = [];

    for (const k in resultJSON) {
        monthDateArray.push(k);
    }

    monthDateArray.sort();

    //배팅 보기 퍼센트 계산 및
    const answerValues = [];

    $.each(resultJSON, function (key, value) {
        let totalCount = 0;

        $.each(value, function (skey, svalue) {
            totalCount += Number(svalue);

            if (answerValues.indexOf(skey) == -1) {
                answerValues.push(skey);
            }
        });

        $.each(value, function (skey, svalue) {
            value[skey + "-p"] = (Number(svalue) / totalCount) * 100;
        });
    });

    answerValues.sort();
    if (answerValues[0] == "No") {
        answerValues.reverse();
    }

    //차트 데이터 삽입
    const colors = ["#007bff", "#6610f2", "#e83e8c", "#fd7e14", "#ffc107"
        , "#28a745", "#17a2b8", "#868e96", "#343a40", "#007bff"
        , "#868e96", "#28a745", "#17a2b8", "#ffc107", "#dc3545"
        , "#f8f9fa", "#343a40"];
    const datasets = [];
    for (i in answerValues) {

        const data = [];
        for (const j in monthDateArray) {

            if (resultJSON[monthDateArray[j]][answerValues[i] + "-p"]) {
                data.push(resultJSON[monthDateArray[j]][answerValues[i] + "-p"]);
            } else {
                data.push(0);
            }
        }

        datasets.push(
            {
                label: answerValues[i],
                backgroundColor: "rgba(255,255,255,0)",
                borderWidth: 3,
                borderColor: colors[i],
                pointBackgroundColor: colors[i],
                pointBorderColor: colors[i],
                pointBorderWidth: 1,
                pointRadius: 3,
                pointHoverBackgroundColor: "#fff",
                pointHoverBorderColor: colors[i],
                data: data
            }
        );
    }

    //line
    const ctxL = document.getElementById("chart").getContext('2d');
    const myLineChart = new Chart(ctxL, {
        type: 'line',
        data: {
            labels: monthDateArray,
            datasets: datasets
        },
        options: {
            maintainAspectRatio: false,
            legend: {
                position: 'bottom',
                labels: {
                    fontColor: '#bbc0c7',
                    usePointStyle: true
                }
            },
            scales: {
                yAxes: [{
                    gridLines: {
                        color: '#757e8a',
                        lineWidth: 0.5
                    }
                }]
            }
        }
    });
}

function getDateString(date) {
    return date.getFullYear() + "-" + numberPad(date.getMonth() + 1, 2) + "-" + numberPad(date.getDate(), 2);
}

/**
 * if n == 1, width = 2 return '01'
 * @param n
 * @param width
 * @returns {*}
 */
function numberPad(n, width) {
    n = n + '';
    return n.length >= width ? n : new Array(width - n.length + 1).join('0') + n;
}
