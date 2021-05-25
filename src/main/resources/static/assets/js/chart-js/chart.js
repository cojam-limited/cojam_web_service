function fnDrawChart(bettings){


    //배팅 보기 퍼센트 계산 및

    //차트 데이터 삽입
    const colors = ["#007bff", "#6610f2", "#e83e8c", "#fd7e14", "#ffc107"
        , "#28a745", "#17a2b8", "#868e96", "#343a40", "#007bff"
        , "#868e96", "#28a745", "#17a2b8", "#ffc107", "#dc3545"
        , "#f8f9fa", "#343a40"];
    const datasets = [];

    const labelsAvg = [];

    const labelsAvgData = [];

    const borderColorArr = [];

    const pointBackgroundColorArr = [];

    let chartCount = 0;

    bettings.forEach(element => {
        labelsAvg.push(element.answerTitle);


        datasets.push(
            {
                label: [element.answerTitle],
                backgroundColor: colors[chartCount],
                borderWidth: 3,
                borderColor: colors[chartCount],
                pointBackgroundColor: colors[chartCount],
                pointBorderColor: colors[chartCount],
                pointBorderWidth: 1,
                pointRadius: 3,
                pointHoverBackgroundColor: "#fff",
                pointHoverBorderColor: colors[chartCount],
                data: [element.bettingAvg]
            }
        );
        chartCount++;
    })







    //line
    const ctxL = document.getElementById("chart").getContext('2d');
    const myLineChart = new Chart(ctxL, {
        type: 'bar',
        data: {
            //labels: labelsAvg,
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
