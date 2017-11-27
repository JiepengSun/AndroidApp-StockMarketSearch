// Price //
function getPrice(symbol) {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getStockQuote" ;
    $.ajax({
    	url: requestLink,
    	data:{
    		'symbol': symbol,
    	},
    	method: 'GET',
    	success: function(result) {
    		getPriceData(result);
   		}
    });
}

function getPriceData(jsonObj) {

    var keys = Object.keys(jsonObj);
    if(keys[0] == "Error Message") {
        Android.showErrorMessage();
        return;
    }

    var keys = Object.keys(jsonObj["Time Series (Daily)"]);
    var values = Object.values(jsonObj["Time Series (Daily)"]);

    // Symbol
    symbol = jsonObj["Meta Data"]["2. Symbol"];

	// Last Price
	if(isTrading(keys[0])) {
        lastPrice = parseFloat(values[1]["4. close"]).toFixed(2);
    } else {
        lastPrice = parseFloat(values[0]["4. close"]).toFixed(2);
    }

    // Change & Change Percent
    closePriceLastDay = values[0]["4. close"];
    closePricePrevDay = values[1]["4. close"];
    change = (closePriceLastDay - closePricePrevDay).toFixed(2);
    changePercentTemp = (change * 100 / closePricePrevDay).toFixed(3);
    changePercent = changePercentTemp.toString().slice(0, 5);

    // Timestamp
    if(isTrading(keys[0])) {
        var currentDate = new Date();
        var currentTime = currentDate.toString().slice(16, 24);
        var currentTimestamp = keys[0] + " " + currentTime + " " + "PST";
        timestamp = currentTimestamp;
    } else {
        var currentTimestamp = keys[0] + " " + "16:00:00 EST";
        timestamp = currentTimestamp;
    }

    // Open & Close
    open = parseFloat(values[0]["1. open"]).toFixed(2);
    close = parseFloat(values[0]["4. close"]).toFixed(2);

    // Day's Range
    low = parseFloat(values[0]["3. low"]).toFixed(2);
    high = parseFloat(values[0]["2. high"]).toFixed(2);

    // Volume
    volume = formatVolume(values[0]["5. volume"]);

    // Chart Data
	var numOfDays = keys.length >= 91 ? 91 : keys.length;

    for(days = 0; days < numOfDays; days++) {
        arrayDates.push(keys[days]);
        arrayPrice.push(parseFloat(values[days]["4. close"]));
        arrayVolume.push(parseFloat(values[days]["5. volume"]));
    }
    arrayDates.reverse();
    arrayPrice.reverse();
    arrayVolume.reverse();

	Price_title = symbol + " Stock Price and Volume";

	drawPriceChart();
	sendDataToActivity();
	Android.priceIsReady();
	getNewsFeed(symbol);
}

function drawPriceChart() {
    initChartProperties();
    title = { text: Price_title };
    xAxis.tickInterval = 10;
    xAxis.categories = arrayDates;
    yAxis = [
        { title: { text: 'Stock Price' } },
        { title: { text: 'Volume' }, opposite: true }
    ];
    series = [
        { name: 'Price', type: 'area', data: arrayPrice, color: '#9dc8f1', yAxis: 0 },
        { name: 'Volume', type: 'column', data: arrayVolume, color: '#f37f81', yAxis: 1 }
    ];
    plotOptions = {};
    drawChart() ;
}

function sendDataToActivity() {
    //Android.showToast("Data from JS");
    var stockDetailsData = [];
    stockDetailsData.push(symbol);
    stockDetailsData.push(lastPrice);
    stockDetailsData.push(change + " (" + changePercent + "%) ");
    stockDetailsData.push(timestamp);
    stockDetailsData.push(open);
    stockDetailsData.push(close);
    stockDetailsData.push(low + " - " + high);
    stockDetailsData.push(volume);
    Android.getStockDetailsData(stockDetailsData, change, changePercent);
    //Android.showToast("Data from JS");
}

// Get SMA //
function getSMA() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getSMA" ;
    $.ajax({
        url: requestLink,
        data:{
            'symbol': symbol,
        },
        method: 'GET',
        success: function(result) {
            getSMAData(result);
        }
    });
}

function getSMAData(jsonObj) {
    var keys = Object.keys(jsonObj["Technical Analysis: SMA"]);
	var values = Object.values(jsonObj["Technical Analysis: SMA"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arraySMADates.push(keys[days]);
        arraySMAData.push(parseFloat(values[days]["SMA"]));
    }
    arraySMADates.reverse();
    arraySMAData.reverse();
    SMA_title = jsonObj["Meta Data"]["2: Indicator"];
    drawSMAChart();
    Android.smaIsReady();
}

function drawSMAChart() {
    initChartProperties();
    title = { text: SMA_title };
    xAxis.categories = arraySMADates;
    yAxis = { title: { text: 'SMA' } };
    series = [{ name: symbol, data: arraySMAData, color: '#f37f81' }];
    drawChart();
}

// Get EMA //
function getEMA() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getEMA" ;
    $.ajax({
        url: requestLink,
        data:{
            'symbol': symbol,
        },
        method: 'GET',
        success: function(result) {
            getEMAData(result);
        }
    });
}

function getEMAData(jsonObj) {
    var keys = Object.keys(jsonObj["Technical Analysis: EMA"]);
    var values = Object.values(jsonObj["Technical Analysis: EMA"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arrayEMADates.push(keys[days]);
        arrayEMAData.push(parseFloat(values[days]["EMA"]));
    }
    arrayEMADates.reverse();
    arrayEMAData.reverse();
    EMA_title = jsonObj["Meta Data"]["2: Indicator"];
    drawEMAChart();
    Android.emaIsReady();
}

function drawEMAChart() {
    initChartProperties();
    title = { text: EMA_title };
    xAxis.categories = arrayEMADates;
    yAxis = { title: { text: 'EMA' } };
    series = [{ name: symbol, data: arrayEMAData, color: '#f37f81' }];
    drawChart();
}

// Get STOCH //
function getSTOCH() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getSTOCH" ;
    $.ajax({
        url: requestLink,
        data:{
            'symbol': symbol,
        },
        method: 'GET',
        success: function(result) {
            getSTOCHData(result);
        }
    });
}

function getSTOCHData(jsonObj) {
    var keys = Object.keys(jsonObj["Technical Analysis: STOCH"]);
    var values = Object.values(jsonObj["Technical Analysis: STOCH"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arraySTOCHDates.push(keys[days]);
        arraySTOCHSlowD.push(parseFloat(values[days]["SlowD"]));
        arraySTOCHSlowK.push(parseFloat(values[days]["SlowK"]));
    }
    arraySTOCHDates.reverse();
    arraySTOCHSlowD.reverse();
    arraySTOCHSlowK.reverse();
    STOCH_title = jsonObj["Meta Data"]["2: Indicator"];
    drawSTOCHChart();
    Android.stochIsReady();
}

function drawSTOCHChart() {
    initChartProperties();
    title = { text: STOCH_title };
    xAxis.categories = arraySTOCHDates;
    yAxis = { title: { text: 'STOCH' } };
    series = [
        { name: symbol.concat(" SlowD"), data: arraySTOCHSlowD, color: '#f37f81' },
        { name: symbol.concat(" SlowK"), data: arraySTOCHSlowK }
    ];
    drawChart();
}

// Get RSI //
function getRSI() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getRSI" ;
    $.ajax({
       url: requestLink,
       data:{
           'symbol': symbol,
       },
       method: 'GET',
       success: function(result) {
           getRSIData(result);
       }
    });
}

function getRSIData(jsonObj) {
var keys = Object.keys(jsonObj["Technical Analysis: RSI"]);
    var values = Object.values(jsonObj["Technical Analysis: RSI"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arrayRSIDates.push(keys[days]);
        arrayRSIData.push(parseFloat(values[days]["RSI"]));
    }
    arrayRSIDates.reverse();
    arrayRSIData.reverse();
    RSI_title = jsonObj["Meta Data"]["2: Indicator"];
    drawRSIChart();
    Android.rsiIsReady();
}

function drawRSIChart() {
    initChartProperties();
    title = { text: RSI_title };
    xAxis.categories = arrayRSIDates;
    yAxis = { title: { text: 'RSI' } };
    series = [{ name: symbol, data: arrayRSIData, color: '#f37f81' }];
    drawChart();
}

// Get ADX //
function getADX() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getADX" ;
    $.ajax({
       url: requestLink,
       data:{
           'symbol': symbol,
       },
       method: 'GET',
       success: function(result) {
           getADXData(result);
       }
    });
}

function getADXData(jsonObj) {
    var keys = Object.keys(jsonObj["Technical Analysis: ADX"]);
    var values = Object.values(jsonObj["Technical Analysis: ADX"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arrayADXDates.push(keys[days]);
        arrayADXData.push(parseFloat(values[days]["ADX"]));
    }
    arrayADXDates.reverse();
    arrayADXData.reverse();
    ADX_title = jsonObj["Meta Data"]["2: Indicator"];
    drawADXChart();
    Android.adxIsReady();
}

function drawADXChart() {
    initChartProperties();
    title = { text: ADX_title };
    xAxis.categories = arrayADXDates;
    yAxis = { title: { text: 'ADX' } };
    series = [{ name: symbol, data: arrayADXData, color: '#f37f81' }];
    drawChart();
}

// Get CCI //
function getCCI() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getCCI" ;
    $.ajax({
       url: requestLink,
       data:{
           'symbol': symbol,
       },
       method: 'GET',
       success: function(result) {
           getCCIData(result);
       }
    });
}

function getCCIData(jsonObj) {
    var keys = Object.keys(jsonObj["Technical Analysis: CCI"]);
    var values = Object.values(jsonObj["Technical Analysis: CCI"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arrayCCIDates.push(keys[days]);
        arrayCCIData.push(parseFloat(values[days]["CCI"]));
    }
    arrayCCIDates.reverse();
    arrayCCIData.reverse();
    CCI_title = jsonObj["Meta Data"]["2: Indicator"];
    drawCCIChart();
    Android.cciIsReady();
}

function drawCCIChart() {
    initChartProperties();
    title = { text: CCI_title };
    xAxis.categories = arrayCCIDates;
    yAxis = { title: { text: 'CCI' } };
    series = [{ name: symbol, data: arrayCCIData, color: '#f37f81' }];
    drawChart();
}

// Get BBANDS //
function getBBANDS() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getBBANDS" ;
    $.ajax({
       url: requestLink,
       data:{
           'symbol': symbol,
       },
       method: 'GET',
       success: function(result) {
           getBBANDSData(result);
       }
    });
}

function getBBANDSData(jsonObj) {
    var keys = Object.keys(jsonObj["Technical Analysis: BBANDS"]);
    var values = Object.values(jsonObj["Technical Analysis: BBANDS"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arrayBBANDSDates.push(keys[days]);
        arrayBBANDSRealMiddleBand.push(parseFloat(values[days]["Real Middle Band"]));
        arrayBBANDSRealLowerBand.push(parseFloat(values[days]["Real Lower Band"]));
        arrayBBANDSRealUpperBand.push(parseFloat(values[days]["Real Upper Band"]));
    }
    arrayBBANDSDates.reverse();
    arrayBBANDSRealMiddleBand.reverse();
    arrayBBANDSRealLowerBand.reverse();
    arrayBBANDSRealUpperBand.reverse();
    BBANDS_title = jsonObj["Meta Data"]["2: Indicator"];
    drawBBANDSChart();
    Android.bbandsIsReady();
}

function drawBBANDSChart() {
    initChartProperties();
    title = { text: BBANDS_title };
    xAxis.categories = arrayBBANDSDates;
    yAxis = { title: { text: 'BBANDS' } };
    series = [
        { name: symbol.concat(" Real Middle Band"), data: arrayBBANDSRealMiddleBand, color: '#f37f81' },
        { name: symbol.concat(" Real Lower Band"), data: arrayBBANDSRealLowerBand },
        { name: symbol.concat(" Real Upper Band"), data: arrayBBANDSRealUpperBand }
    ];
    drawChart();
}

// Get MACD //
function getMACD() {
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getMACD" ;
    $.ajax({
       url: requestLink,
       data:{
           'symbol': symbol,
       },
       method: 'GET',
       success: function(result) {
           getMACDData(result);
       }
    });
}

function getMACDData(jsonObj) {
    var keys = Object.keys(jsonObj["Technical Analysis: MACD"]);
    var values = Object.values(jsonObj["Technical Analysis: MACD"]);
    var numOfDays = keys.length >= 131 ? 131 : keys.length;
    for(days = 0; days < numOfDays; days++) {
        arrayMACDDates.push(keys[days]);
        arrayMACDData.push(parseFloat(values[days]["MACD"]));
        arrayMACDHist.push(parseFloat(values[days]["MACD_Hist"]));
        arrayMACDSignal.push(parseFloat(values[days]["MACD_Signal"]));

    }
    arrayMACDDates.reverse();
    arrayMACDData.reverse();
    arrayMACDHist.reverse();
    arrayMACDSignal.reverse();
    MACD_title = jsonObj["Meta Data"]["2: Indicator"];
    drawMACDChart();
    Android.macdIsReady();
}

function drawMACDChart() {
    initChartProperties();
    title = { text: MACD_title };
    xAxis.categories = arrayMACDDates;
    yAxis = { title: { text: 'MACD' } };
    series = [
        { name: symbol.concat(" MACD"), data: arrayMACDData, color: '#f37f81' },
        { name: symbol.concat(" MACD_Hist"), data: arrayMACDHist },
        { name: symbol.concat(" MACD_Signal"), data: arrayMACDSignal }
    ];
    drawChart();
}

// Get News Feed //
function getNewsFeed(symbol) {

    //sendNewsFeedToActivity();
    var requestLink = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getNewsFeed" ;
    $.ajax({
       url: requestLink,
       data:{
           'symbol': symbol,
       },
       method: 'GET',
       success: function(result) {
           getNewsFeedData(result);
           sendNewsFeedToActivity();
       }
    });
}

function getNewsFeedData(xml) {
    // Parse XML
    xmlDoc = (new DOMParser()).parseFromString(xml, 'text/xml');

    // Get Data
    var numOfNews = 0;
    var numOfLoop = 0;
    while(numOfNews < 5) {
        item = xmlDoc.getElementsByTagName("item")[numOfLoop];
        if(item == null) {
            break;
        }
        if(!item.getElementsByTagName("link")[0].firstChild.data.includes("article")) {
            numOfLoop++;
            continue;
        }
        newsFeedTitle.push(item.getElementsByTagName("title")[0].firstChild.data);
        newsFeedLink.push(item.getElementsByTagName("link")[0].firstChild.data);
        date = item.getElementsByTagName("pubDate")[0].firstChild.data;
        index = date.indexOf("-");
        date = date.substr(0, index - 1);
        newsFeedDate.push(date);
        try {
            newsFeedAuthor.push(item.getElementsByTagName("author_name")[0].firstChild.data);
        }
        catch(e) {
            newsFeedAuthor.push(item.getElementsByTagName("sa:author_name")[0].firstChild.data);
        }
        numOfNews++;
        numOfLoop++;
    }
}

function sendNewsFeedToActivity() {
    Android.getNewsFeed(newsFeedTitle, newsFeedAuthor, newsFeedDate, newsFeedLink);
}



