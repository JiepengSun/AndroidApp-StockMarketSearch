// Stock Details Variable
var symbol;
var lastPrice;
var change;
var changePercentTemp;
var changePercent;
var timestamp;
var open;
var close;
var low;
var high;
var volume;

// Charts Data
var arrayDates = [];
var arrayPrice = [];
var arrayVolume = [];
var arraySMADates = [];
var arraySMAData = [];
var arrayEMADates = [];
var arrayEMAData = [];
var arraySTOCHDates = [];
var arraySTOCHSlowD = [];
var arraySTOCHSlowK = [];
var arrayRSIDates = [];
var arrayRSIData = [];
var arrayADXDates = [];
var arrayADXData = [];
var arrayCCIDates = [];
var arrayCCIData = [];
var arrayBBANDSDates = [];
var arrayBBANDSRealMiddleBand = [];
var arrayBBANDSRealLowerBand = [];
var arrayBBANDSRealUpperBand = [];
var arrayMACDDates = [];
var arrayMACDData = [];
var arrayMACDHist = [];
var arrayMACDSignal = [];

// Historical Chart Data
var arrayHistoricalChart = [];

// News Feed Data
var newsFeedTitle = [];
var newsFeedLink = [];
var newsFeedAuthor = [];
var newsFeedDate = [];

// Properties of Charts
var chart, title, subtitle, xAxis, yAxis, legend, series, plotOptions;
var chart2, title2, subtitle2, xAxis2, yAxis2, legend2, series2, plotOptions2;

// Chart Titles
var symbol;
var Price_title;
var SMA_title;
var EMA_title;
var STOCH_title;
var RIS_title;
var ADX_title;
var CCI_title;
var BBANDS_title;
var MACD_title;