function getHistoricalChart(symbol) {
    historicalChartSymbol = symbol;
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getHistoricalChart" ;
    $.ajax({
       url: requestLink,
       data:{
           'symbol': symbol,
       },
       method: 'GET',
       success: function(result) {
           getHistoricalChartData(result);
       }
    });
}

function getHistoricalChartData(jsonObj) {

    var keys = Object.keys(jsonObj["Time Series (Daily)"]);
    var values = Object.values(jsonObj["Time Series (Daily)"]);

    var numOfDays = keys.length > 1000 ? 1000 : keys.length;
    for(i = 0; i < numOfDays; i++) {
        parsedUnixTime = new Date(keys[i]).getUnixTime();
        price = parseFloat(values[i]["4. close"]).toFixed(2);
        var arr = [];
        arr.push(parsedUnixTime * 1000, price * 1.0);
        arrayHistoricalChart.push(arr);
    }
    arrayHistoricalChart.reverse();

    drawHistoricalChart();
}

function drawHistoricalChart() {
    var historicalChart = new Highcharts.stockChart({
        chart: {
            renderTo: 'historicalChartContainer',
            height: (4 / 3 * 100) + '%'
        },
        rangeSelector: { selected: 1 },
        title: { text: historicalChartSymbol + ' Stock Price' },
        subtitle: {
            useHTML: true,
            text: "<a href='https://www.alphavantage.co/' target='_blank'>Source: Alpha Vantage</a>"
        },
        series: [{
            name: symbol,
            data: arrayHistoricalChart,
            tooltip: { valueDecimals: 2 }
        }]
    });
}