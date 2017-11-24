function sayHello() {
    alert('Hi ' + document.getElementById('name').value + ' !');
}

function callFromActivity() {
    //alert("This function is called directly from Android Activity");
    var object = document.getElementById("msg");
    object.innerText = 100;
}