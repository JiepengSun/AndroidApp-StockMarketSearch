// Generate Timestamp
Date.prototype.getUnixTime = function() {
    return this.getTime() / 1000 | 0;
};
if(!Date.now){
    Date.now = function() {
        return new Date();
    }
}
Date.time = function() {
    return Date.now().getUnixTime();
}

// Check Trading Hour
function isTrading(date) {
    var tradingEnd = new Date(date + " 13:00:00 PST").getUnixTime();
    var currentDate = new Date();
    var currentTimestamp = new Date(currentDate).getUnixTime();
    if(parseFloat(currentTimestamp) > parseFloat(tradingEnd)) {
        return false;
    } else {
        return true;
    }
}

// Format Volume
function formatVolume(str) {
    reverseStrArr = str.split("").reverse();
    resultStrArr = [];
    for(index = 0; index < reverseStrArr.length; index++) {
        resultStrArr.push(reverseStrArr[index]);
        if(index % 3 == 2 && index != reverseStrArr.length - 1) {
            resultStrArr.push(',');
        }
    }
    return resultStrArr.reverse().join("");
}