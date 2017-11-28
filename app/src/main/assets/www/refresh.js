var symbol;
var lastPrice;
var change;
var changePercent;

function refreshFavList(symbol) {
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

	sendDataToActivity();
}

function sendDataToActivity() {
    var refreshData = [];
    refreshData.push(symbol);
    refreshData.push(lastPrice);
    refreshData.push(change + " (" + changePercent + "%) ");
    Android.getRefreshData(refreshData, change, changePercent);
}
