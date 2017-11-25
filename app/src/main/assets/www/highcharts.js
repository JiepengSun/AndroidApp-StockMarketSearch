// Init Chart
function initChartProperties() {
    chart = { renderTo : 'stockDetailsChartContainer', zoomType: 'x' };
    title = {};
    subtitle = {
        useHTML: true,
        text: "<a href='https://www.alphavantage.co/' target='_blank'>Source: Alpha Vantage</a>"
    };
    xAxis = {
        tickInterval: 5,
        labels: {
            formatter: function() {
                return this.value.substring(5,7)+'/'+this.value.substring(8);
            }
        }
    };
    yAxis = {};
    legend = {};
    series = {};
    plotOptions = {
        series: {
            lineWidth: 1
        }
    }
}

// Draw Chart
function drawChart() {
    var myChart = new Highcharts.chart({
        chart: chart,
        title: title,
        subtitle: subtitle,
        xAxis: xAxis,
        yAxis: yAxis,
        plotOptions: plotOptions,
        legend: legend,
        series: series
    });
}